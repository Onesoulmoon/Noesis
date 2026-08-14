package com.necrosed.noesis.analysis

import com.necrosed.noesis.data.db.entity.ConceptEntity
import com.necrosed.noesis.data.db.entity.PersistenceLevel
import kotlin.math.abs

// ═══════════════════════════════════════════════════════════════
// TEXT ANALYZER
// Orchestrates the full pipeline for a single entry.
// Input: raw text
// Output: TokenizedEntry with term frequencies + phrase candidates
// ═══════════════════════════════════════════════════════════════

class TextAnalyzer {

    fun analyze(text: String, language: String = "unknown"): TokenizedEntry {
        val lang = if (language == "unknown") detectLanguage(text) else language
        return analyzerFor(lang).analyze(text)
    }

    // Unresolved detection — auto-tags entries that look like open questions
    fun isUnresolved(text: String): Boolean {
        val trimmed = text.trim()
        return trimmed.endsWith("?") ||
            trimmed.contains("?") ||
            UNRESOLVED_PREFIXES.any { trimmed.lowercase().startsWith(it) }
    }

    companion object {
        val UNRESOLVED_PREFIXES = listOf(
            "why", "how", "what", "when", "where", "who", "should",
            "pourquoi", "comment", "quand", "qui", "est-ce que",
            "i wonder", "i keep", "je me demande", "is it", "can i",
            "would", "could", "maybe i", "perhaps", "do i", "should i"
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// CONCEPT MATCHER
//
// Given an entry's analysis and the existing concept library,
// determines:
//   1. Which existing concepts this entry confirms (MATCH)
//   2. Which terms in this entry could seed new concepts (CANDIDATE)
//
// The user's original text is never modified.
// Only the relation table changes.
// ═══════════════════════════════════════════════════════════════

data class ConceptMatch(
    val conceptNumber: Int,
    val conceptLabel: String,
    val conceptStem: String,
    val confidence: Float,          // 0.0–1.0
    val matchedTerms: List<String>  // Which surface forms triggered this
)

data class NewConceptCandidate(
    val label: String,
    val stem: String,
    val surfaceForms: Set<String>,
    val count: Int,                 // Times it appeared in this entry
    val confidence: Float
)

class ConceptMatcher {

    // Match entry analysis against known concepts
    fun matchAgainstConcepts(
        analysis: TokenizedEntry,
        existingConcepts: List<ConceptEntity>
    ): List<ConceptMatch> {
        val matches = mutableListOf<ConceptMatch>()

        for (concept in existingConcepts) {
            val conceptStems = concept.stem.split("|").map { it.trim() }
            val forms = concept.surfaceForms.split("|").map { it.trim() }.toSet()

            // Check how many times this concept's stems appear in the entry
            val matchedTerms = mutableListOf<String>()
            var totalWeight = 0f

            for (termInfo in analysis.termFrequency.values) {
                val stemMatch = termInfo.stem in conceptStems
                val formMatch = termInfo.forms.any { it in forms }
                if (stemMatch || formMatch) {
                    matchedTerms.addAll(termInfo.forms)
                    totalWeight += termInfo.count * (if (stemMatch) 1.0f else 0.8f)
                }
            }

            // Check phrases too
            for (phrase in analysis.phrasesCandidates) {
                if (conceptStems.any { phrase.phrase.contains(it) } ||
                    forms.any { phrase.phrase.contains(it) }) {
                    matchedTerms.add(phrase.phrase)
                    totalWeight += phrase.count * 1.5f  // Phrase match is stronger
                }
            }

            if (matchedTerms.isNotEmpty()) {
                val confidence = (totalWeight / (analysis.filteredTokens.size
                    .coerceAtLeast(1).toFloat())).coerceIn(0f, 1f)

                if (confidence >= 0.02f) {
                    matches.add(
                        ConceptMatch(
                            conceptNumber = concept.conceptNumber,
                            conceptLabel  = concept.label,
                            conceptStem   = concept.stem,
                            confidence    = confidence,
                            matchedTerms  = matchedTerms.distinct()
                        )
                    )
                }
            }
        }
        return matches.sortedByDescending { it.confidence }
    }

    // Identify terms that could seed new concepts
    fun proposeCandidates(
        analysis: TokenizedEntry,
        existingConcepts: List<ConceptEntity>,
        minCount: Int = 1
    ): List<NewConceptCandidate> {
        val existingStems = existingConcepts
            .flatMap { it.stem.split("|").map { s -> s.trim() } }
            .toSet()

        val candidates = mutableListOf<NewConceptCandidate>()

        // Single-term candidates from high-frequency or high-weight terms
        for (termInfo in analysis.termFrequency.values) {
            if (termInfo.stem in existingStems) continue
            if (termInfo.stem.length < 3) continue
            if (termInfo.count >= minCount) {
                val confidence = (termInfo.count * 0.3f).coerceIn(0f, 1f)
                candidates.add(
                    NewConceptCandidate(
                        label       = termInfo.forms.maxByOrNull { it.length }
                            ?.uppercase() ?: termInfo.stem.uppercase(),
                        stem        = termInfo.stem,
                        surfaceForms = termInfo.forms,
                        count       = termInfo.count,
                        confidence  = confidence
                    )
                )
            }
        }

        // Phrase candidates
        for (phrase in analysis.phrasesCandidates) {
            val phraseStem = phrase.stem
            if (existingStems.any { phraseStem.contains(it) }) continue
            candidates.add(
                NewConceptCandidate(
                    label        = phrase.phrase.uppercase(),
                    stem         = phraseStem,
                    surfaceForms = setOf(phrase.phrase),
                    count        = phrase.count,
                    confidence   = (phrase.count * 0.5f).coerceIn(0f, 1f)
                )
            )
        }

        return candidates
            .filter { it.confidence >= 0.1f }
            .sortedByDescending { it.confidence }
            .take(5)  // Cap candidates per entry — keep the engine conservative
    }

    // Human-readable explanation of why a concept was matched
    fun explainMatch(
        match: ConceptMatch,
        termInfoMap: Map<String, TermInfo>
    ): List<String> {
        val explanation = mutableListOf<String>()
        explanation.add("STEM: ${match.conceptStem}")
        val forms = match.matchedTerms.take(4)
        if (forms.isNotEmpty()) {
            val formStr = forms.joinToString(" → ") { it }
            explanation.add("FORMS: $formStr → ${match.conceptStem}")
        }
        return explanation
    }
}

// ═══════════════════════════════════════════════════════════════
// PERSISTENCE ENGINE
//
// Evaluates a concept's behavioral history and assigns
// one of four persistence levels. Transparent and auditable.
//
// NONE             → not enough signal
// RECURRING        → 2+ observations (any window)
// PERSISTENT       → 3+ obs, ≥7 days span, active recently
// DEEPLY_PERSISTENT→ 5+ obs, ≥30 days span, 2+ in last 14 days
// ═══════════════════════════════════════════════════════════════

data class PersistenceAssessment(
    val level: String,
    val observationCount: Int,
    val spanDays: Int,
    val recentCount14d: Int,
    val confidence: Int,            // 0–100
    val explanation: List<String>   // Human-readable breakdown
)

class PersistenceEngine {

    fun assess(
        concept: ConceptEntity,
        allObservationTimestamps: List<Long>,  // All entry timestamps where concept appears
        recentCount14d: Int
    ): PersistenceAssessment {
        val count = allObservationTimestamps.size
        val spanDays = if (count >= 2) {
            val earliest = allObservationTimestamps.min()
            val latest   = allObservationTimestamps.max()
            ((latest - earliest) / (24 * 60 * 60 * 1000L)).toInt()
        } else 0

        val level = when {
            count >= PersistenceLevel.DEEP_MIN_OBS &&
            spanDays >= PersistenceLevel.DEEP_MIN_SPAN_DAYS &&
            recentCount14d >= PersistenceLevel.DEEP_MIN_RECENT_14D ->
                PersistenceLevel.DEEPLY_PERSISTENT

            count >= PersistenceLevel.PERSISTENT_MIN_OBS &&
            spanDays >= PersistenceLevel.PERSISTENT_MIN_SPAN_DAYS ->
                PersistenceLevel.PERSISTENT

            count >= PersistenceLevel.RECURRING_MIN_OBS ->
                PersistenceLevel.RECURRING

            else -> PersistenceLevel.NONE
        }

        val confidence = calculateConfidence(count, spanDays, recentCount14d)
        val explanation = buildExplanation(level, count, spanDays, recentCount14d)

        return PersistenceAssessment(level, count, spanDays, recentCount14d, confidence, explanation)
    }

    private fun calculateConfidence(count: Int, spanDays: Int, recent: Int): Int {
        val countScore  = (count * 8).coerceAtMost(40)
        val spanScore   = (spanDays * 0.5f).toInt().coerceAtMost(30)
        val recentScore = (recent * 10).coerceAtMost(30)
        return (countScore + spanScore + recentScore).coerceIn(0, 100)
    }

    private fun buildExplanation(
        level: String, count: Int, spanDays: Int, recent: Int
    ): List<String> = listOf(
        "OBSERVATIONS     $count",
        "SPAN             ${spanDays}d",
        "RECENT (14D)     $recent",
        "LEVEL            $level"
    )

    // Compute related concept strength (co-occurrence based)
    fun coOccurrenceStrength(sharedEntries: Int, totalA: Int, totalB: Int): Float {
        if (totalA == 0 || totalB == 0) return 0f
        // Jaccard-like: shared / (union)
        val union = totalA + totalB - sharedEntries
        return if (union <= 0) 0f
        else (sharedEntries.toFloat() / union).coerceIn(0f, 1f)
    }
}
