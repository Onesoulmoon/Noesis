package com.necrosed.noesis.data.repository

import com.necrosed.noesis.analysis.PersistenceEngine
import com.necrosed.noesis.data.db.dao.ConceptDao
import com.necrosed.noesis.data.db.dao.EntryDao
import com.necrosed.noesis.data.db.entity.ConceptEntity
import com.necrosed.noesis.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ═══════════════════════════════════════════════════════════════
// CONCEPT REPOSITORY
//
// Reads, aggregates, and exposes concept data to the ViewModel.
// Concept writes are handled inside EntryRepository's analysis
// pipeline — concepts are always derived from entries, never
// created directly by the user.
// ═══════════════════════════════════════════════════════════════

class ConceptRepository(
    private val conceptDao: ConceptDao,
    private val entryDao: EntryDao
) {
    private val persistEngine = PersistenceEngine()

    // ─── OBSERVE ────────────────────────────────────────────────

    fun observeAllRanked(): Flow<List<Concept>> =
        conceptDao.observeAllRanked().map { entities ->
            entities.map { it.toDomain() }
        }

    fun observeSignificant(): Flow<List<Concept>> =
        conceptDao.observeAllRanked().map { entities ->
            entities
                .filter { it.persistenceLevel != "NONE" }
                .map { it.toDomain() }
        }

    // ─── SINGLE CONCEPT ─────────────────────────────────────────

    suspend fun getConcept(conceptNumber: Int): Concept? {
        val entity = conceptDao.getByNumber(conceptNumber) ?: return null
        val related = getRelatedConcepts(conceptNumber)
        return entity.toDomain(related)
    }

    private suspend fun getRelatedConcepts(conceptNumber: Int): List<RelatedConcept> {
        val related = conceptDao.getRelatedConcepts(conceptNumber)
        val selfRelations = conceptDao.getRelationsForConcept(conceptNumber)
        val selfCount = selfRelations.size

        return related.map { relatedEntity ->
            val relatedRelations = conceptDao.getRelationsForConcept(relatedEntity.conceptNumber)
            val shared = conceptDao.getCoOccurrenceCount(conceptNumber, relatedEntity.conceptNumber)
            val strength = persistEngine.coOccurrenceStrength(
                sharedEntries = shared,
                totalA        = selfCount,
                totalB        = relatedRelations.size
            )
            RelatedConcept(
                conceptNumber       = relatedEntity.conceptNumber,
                label               = relatedEntity.label,
                coOccurrenceStrength = strength
            )
        }.filter { it.coOccurrenceStrength > 0f }
            .sortedByDescending { it.coOccurrenceStrength }
            .take(6)
    }

    // ─── ENTRIES FOR CONCEPT ────────────────────────────────────

    suspend fun getEntriesForConcept(conceptNumber: Int): List<Entry> {
        val entities = entryDao.getEntriesForConcept(conceptNumber)
        val relations = conceptDao.getRelationsForConcept(conceptNumber)
            .associateBy { it.entryNumber }

        return entities.map { entity ->
            val rel = relations[entity.entryNumber]
            Entry(
                entryNumber    = entity.entryNumber,
                displayId      = "N-${entity.entryNumber.toString().padStart(4, '0')}",
                content        = entity.content,
                status         = entity.status,
                language       = entity.language,
                isUnresolved   = entity.isUnresolved,
                createdAt      = entity.createdAt,
                lastModifiedAt = entity.lastModifiedAt,
                revisionCount  = 0,
                conceptLinks   = listOf(
                    ConceptLink(
                        conceptNumber = conceptNumber,
                        label         = "",
                        confidence    = rel?.confidence ?: 0f,
                        matchedTerms  = rel?.matchedTerms?.split("|") ?: emptyList()
                    )
                )
            )
        }
    }

    // ─── STATS ──────────────────────────────────────────────────

    suspend fun getStats(window: AnalysisWindow): ArchiveStats {
        val active      = entryDao.getActiveCount()
        val unresolved  = entryDao.getUnresolvedCount()
        val persistent  = conceptDao.getPersistentCount()
        val totalConcepts = conceptDao.getAll().size
        val oldest      = entryDao.getOldestActive()?.createdAt
        return ArchiveStats(active, unresolved, persistent, totalConcepts, oldest, window)
    }

    suspend fun getWidgetStats(): Triple<Int, Int, Int> {
        val active     = entryDao.getActiveCount()
        val persistent = conceptDao.getWidgetPersistentCount()
        val unresolved = entryDao.getUnresolvedCount()
        return Triple(active, persistent, unresolved)
    }

    // ─── ENTITY → DOMAIN ────────────────────────────────────────

    private fun ConceptEntity.toDomain(
        related: List<RelatedConcept> = emptyList()
    ) = Concept(
        conceptNumber  = conceptNumber,
        displayId      = "C-${conceptNumber.toString().padStart(4, '0')}",
        label          = label,
        stem           = stem,
        surfaceForms   = surfaceForms.split("|").filter { it.isNotBlank() },
        language       = language,
        persistenceLevel = PLevel.from(persistenceLevel),
        observationCount = observationCount,
        firstObserved  = firstObserved,
        lastObserved   = lastObserved,
        spanDays       = spanDays,
        recentCount14d = recentCount14d,
        confidence     = confidence,
        relatedConcepts = related
    )
}
