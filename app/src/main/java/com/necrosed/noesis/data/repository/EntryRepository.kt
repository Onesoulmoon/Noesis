package com.necrosed.noesis.data.repository

import com.necrosed.noesis.analysis.ConceptMatcher
import com.necrosed.noesis.analysis.PersistenceEngine
import com.necrosed.noesis.analysis.TextAnalyzer
import com.necrosed.noesis.analysis.detectLanguage
import com.necrosed.noesis.data.db.dao.ConceptDao
import com.necrosed.noesis.data.db.dao.EntryDao
import com.necrosed.noesis.data.db.entity.*
import com.necrosed.noesis.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ═══════════════════════════════════════════════════════════════
// ENTRY REPOSITORY
//
// The primary write path for user thoughts. Every capture:
//   1. Assigns a permanent sequential entry number
//   2. Writes the entry to the encrypted DB
//   3. Runs the NLP pipeline asynchronously
//   4. Updates or creates concept relations
//   5. Recalculates persistence levels
//
// The analysis runs AFTER the entry is committed — the user
// sees their thought archived immediately, analysis follows.
// ═══════════════════════════════════════════════════════════════

class EntryRepository(
    private val entryDao: EntryDao,
    private val conceptDao: ConceptDao
) {
    private val textAnalyzer   = TextAnalyzer()
    private val conceptMatcher = ConceptMatcher()
    private val persistEngine  = PersistenceEngine()

    // ─── CAPTURE ────────────────────────────────────────────────

    suspend fun captureEntry(input: CaptureInput): Int {
        val entryNumber = entryDao.nextEntryNumber()
        val language    = if (input.language == "unknown")
                              detectLanguage(input.content) else input.language
        val isUnresolved = textAnalyzer.isUnresolved(input.content)

        val entity = EntryEntity(
            entryNumber    = entryNumber,
            content        = input.content.trim(),
            language       = language,
            isUnresolved   = isUnresolved,
            createdAt      = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis()
        )
        entryDao.insertEntry(entity)

        // Analyze and update concepts — runs synchronously in the repo coroutine
        // so the caller (ViewModel) can launch it on IO dispatcher
        runAnalysis(entryNumber, input.content, language)
        return entryNumber
    }

    // ─── REVISE ─────────────────────────────────────────────────

    suspend fun reviseEntry(entryNumber: Int, newContent: String) {
        val entry = entryDao.getByNumber(entryNumber) ?: return
        val latestRevNum = entryDao.getLatestRevisionNumber(entryNumber) ?: 0
        val revision = EntryRevisionEntity(
            entryNumber    = entryNumber,
            revisionNumber = latestRevNum + 1,
            content        = entry.content,  // Archive the CURRENT content as a revision
            createdAt      = System.currentTimeMillis()
        )
        entryDao.insertRevision(revision)

        // Update the entry's content and modified timestamp
        // Note: we don't update content field directly through a dedicated update DAO
        // method — instead delete+reinsert preserving created_at for immutability feel.
        // Actually Room won't let us update partial columns easily, so use raw update:
        val updated = entry.copy(
            content        = newContent.trim(),
            lastModifiedAt = System.currentTimeMillis()
        )
        entryDao.insertEntry(updated)  // REPLACE strategy handles upsert

        val language = detectLanguage(newContent)
        runAnalysis(entryNumber, newContent, language)
    }

    // ─── ARCHIVE / PURGE ────────────────────────────────────────

    suspend fun archiveEntry(entryNumber: Int) {
        entryDao.updateStatus(entryNumber, EntryStatus.ARCHIVED)
    }

    suspend fun purgeEntry(entryNumber: Int) {
        entryDao.purgeEntry(entryNumber)
        entryDao.deleteRevisions(entryNumber)
        // Relations remain in concept_entry_relations for continuity but
        // the entry content is gone — only the entry number lingers as a ghost
    }

    suspend fun toggleUnresolved(entryNumber: Int) {
        val entry = entryDao.getByNumber(entryNumber) ?: return
        entryDao.updateUnresolved(entryNumber, !entry.isUnresolved)
    }

    // ─── OBSERVE ────────────────────────────────────────────────

    fun observeStream(): Flow<List<Entry>> =
        entryDao.streamActive().map { entities ->
            entities.map { it.toDomain(emptyList()) }
        }

    fun searchStream(query: String): Flow<List<Entry>> =
        entryDao.searchEntries(query).map { entities ->
            entities.map { it.toDomain(emptyList()) }
        }

    suspend fun getEntry(entryNumber: Int): Entry? {
        val entity = entryDao.getByNumber(entryNumber) ?: return null
        val concepts = conceptDao.getConceptsForEntry(entryNumber)
        val links = concepts.map { c ->
            val rel = conceptDao.getRelationsForEntry(entryNumber)
                .firstOrNull { it.conceptNumber == c.conceptNumber }
            ConceptLink(
                conceptNumber = c.conceptNumber,
                label         = c.label,
                confidence    = rel?.confidence ?: 0f,
                matchedTerms  = rel?.matchedTerms?.split("|") ?: emptyList()
            )
        }
        return entity.toDomain(links)
    }

    suspend fun getRevisions(entryNumber: Int): List<Revision> =
        entryDao.getRevisions(entryNumber).map {
            Revision(it.revisionNumber, it.content, it.createdAt)
        }

    suspend fun getRecent(limit: Int = 5): List<Entry> =
        entryDao.getRecent(limit).map { it.toDomain(emptyList()) }

    suspend fun getStats(): Triple<Int, Int, Long?> {
        val active     = entryDao.getActiveCount()
        val unresolved = entryDao.getUnresolvedCount()
        val oldest     = entryDao.getOldestActive()?.createdAt
        return Triple(active, unresolved, oldest)
    }

    // ─── ANALYSIS PIPELINE ──────────────────────────────────────

    private suspend fun runAnalysis(entryNumber: Int, content: String, language: String) {
        val analysis = textAnalyzer.analyze(content, language)
        if (analysis.filteredTokens.isEmpty()) return

        val allConcepts = conceptDao.getAll()
        val entryTs = entryDao.getByNumber(entryNumber)?.createdAt
            ?: System.currentTimeMillis()

        // 1. Match against existing concepts
        val matches = conceptMatcher.matchAgainstConcepts(analysis, allConcepts)
        for (match in matches) {
            conceptDao.insertRelation(
                ConceptEntryRelationEntity(
                    conceptNumber = match.conceptNumber,
                    entryNumber   = entryNumber,
                    confidence    = match.confidence,
                    matchedTerms  = match.matchedTerms.joinToString("|"),
                    observedAt    = entryTs
                )
            )
            // Recalculate persistence for matched concept
            recalculatePersistence(match.conceptNumber)
        }

        // 2. Propose new concept candidates
        val candidates = conceptMatcher.proposeCandidates(analysis, allConcepts)
        val existingStems = allConcepts.map { it.stem }.toSet()

        for (candidate in candidates) {
            if (candidate.stem in existingStems) continue
            val conceptNumber = entryDao.nextConceptNumber()
            val newConcept = ConceptEntity(
                conceptNumber    = conceptNumber,
                label            = candidate.label,
                stem             = candidate.stem,
                surfaceForms     = candidate.surfaceForms.joinToString("|"),
                language         = language,
                persistenceLevel = PersistenceLevel.NONE,
                observationCount = 1,
                firstObserved    = entryTs,
                lastObserved     = entryTs,
                confidence       = (candidate.confidence * 100).toInt()
            )
            conceptDao.insertConcept(newConcept)
            conceptDao.insertRelation(
                ConceptEntryRelationEntity(
                    conceptNumber = conceptNumber,
                    entryNumber   = entryNumber,
                    confidence    = candidate.confidence,
                    matchedTerms  = candidate.surfaceForms.joinToString("|"),
                    observedAt    = entryTs
                )
            )
        }
    }

    private suspend fun recalculatePersistence(conceptNumber: Int) {
        val concept   = conceptDao.getByNumber(conceptNumber) ?: return
        val relations = conceptDao.getRelationsForConcept(conceptNumber)
        val timestamps = relations.map { it.observedAt }
        val since14d  = System.currentTimeMillis() - (14L * 24 * 60 * 60 * 1000L)
        val recent14d = timestamps.count { it >= since14d }

        val assessment = persistEngine.assess(concept, timestamps, recent14d)

        conceptDao.updatePersistence(
            conceptNumber = conceptNumber,
            level         = assessment.level,
            count         = assessment.observationCount,
            lastObserved  = timestamps.maxOrNull() ?: concept.lastObserved,
            spanDays      = assessment.spanDays,
            recent14d     = assessment.recentCount14d,
            confidence    = assessment.confidence
        )
    }

    // ─── ENTITY → DOMAIN ────────────────────────────────────────

    private fun EntryEntity.toDomain(links: List<ConceptLink>) = Entry(
        entryNumber  = entryNumber,
        displayId    = "N-${entryNumber.toString().padStart(4, '0')}",
        content      = content,
        status       = status,
        language     = language,
        isUnresolved = isUnresolved,
        createdAt    = createdAt,
        lastModifiedAt = lastModifiedAt,
        revisionCount = 0,
        conceptLinks  = links
    )
}
