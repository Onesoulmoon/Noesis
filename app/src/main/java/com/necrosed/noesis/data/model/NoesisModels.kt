package com.necrosed.noesis.data.model

import com.necrosed.noesis.data.db.entity.PersistenceLevel

// ═══════════════════════════════════════════════════════════════
// NOESIS — DOMAIN MODELS
// Clean domain objects that the UI and ViewModel work with.
// Mapped from DB entities by the repositories.
// ═══════════════════════════════════════════════════════════════

// ─── ENTRY ──────────────────────────────────────────────────────

data class Entry(
    val entryNumber: Int,
    val displayId: String,          // "N-0048"
    val content: String,
    val status: String,
    val language: String,
    val isUnresolved: Boolean,
    val createdAt: Long,
    val lastModifiedAt: Long,
    val revisionCount: Int,
    val conceptLinks: List<ConceptLink>   // Concepts found in this entry
)

data class ConceptLink(
    val conceptNumber: Int,
    val label: String,
    val confidence: Float,
    val matchedTerms: List<String>
)

// ─── CONCEPT ────────────────────────────────────────────────────

data class Concept(
    val conceptNumber: Int,
    val displayId: String,          // "C-0018"
    val label: String,
    val stem: String,
    val surfaceForms: List<String>,
    val language: String,
    val persistenceLevel: PLevel,
    val observationCount: Int,
    val firstObserved: Long,
    val lastObserved: Long,
    val spanDays: Int,
    val recentCount14d: Int,
    val confidence: Int,
    val relatedConcepts: List<RelatedConcept> = emptyList()
)

data class RelatedConcept(
    val conceptNumber: Int,
    val label: String,
    val coOccurrenceStrength: Float  // 0.0–1.0
)

enum class PLevel(val displayName: String) {
    NONE              ("NONE"),
    RECURRING         ("RECURRING"),
    PERSISTENT        ("PERSISTENT"),
    DEEPLY_PERSISTENT ("DEEPLY PERSISTENT");

    companion object {
        fun from(s: String) = entries.firstOrNull { it.name == s } ?: NONE
    }
}

// ─── ANALYSIS WINDOW ────────────────────────────────────────────

enum class AnalysisWindow(val label: String, val days: Int) {
    DAYS_7   ("7 DAYS",   7),
    DAYS_30  ("30 DAYS",  30),
    DAYS_90  ("90 DAYS",  90),
    DAYS_365 ("1 YEAR",   365),
    ALL_TIME ("ALL TIME", Int.MAX_VALUE);

    fun startMs(): Long {
        if (days == Int.MAX_VALUE) return 0L
        return System.currentTimeMillis() - (days.toLong() * 24 * 60 * 60 * 1000L)
    }
}

// ─── ARCHIVE STATS ──────────────────────────────────────────────

data class ArchiveStats(
    val totalEntries: Int,
    val unresolvedCount: Int,
    val persistentConceptCount: Int,
    val totalConceptCount: Int,
    val oldestEntryMs: Long?,
    val window: AnalysisWindow
)

// ─── CAPTURE INPUT ──────────────────────────────────────────────

data class CaptureInput(
    val content: String,
    val language: String = "unknown"
)

// ─── REVISION RECORD ────────────────────────────────────────────

data class Revision(
    val revisionNumber: Int,
    val content: String,
    val createdAt: Long
)
