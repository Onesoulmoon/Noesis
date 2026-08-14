package com.necrosed.noesis.data.db.dao

import androidx.room.*
import com.necrosed.noesis.data.db.entity.ConceptEntity
import com.necrosed.noesis.data.db.entity.ConceptEntryRelationEntity
import com.necrosed.noesis.data.db.entity.PersistenceLevel
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptDao {

    // ─── CONCEPT INSERT / UPDATE ────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcept(concept: ConceptEntity): Long

    @Update
    suspend fun updateConcept(concept: ConceptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(relation: ConceptEntryRelationEntity)

    // ─── CONCEPT QUERY ──────────────────────────────────────────

    @Query("SELECT * FROM concepts ORDER BY observation_count DESC")
    fun observeAll(): Flow<List<ConceptEntity>>

    @Query("""
        SELECT * FROM concepts
        ORDER BY
            CASE persistence_level
                WHEN 'DEEPLY_PERSISTENT' THEN 0
                WHEN 'PERSISTENT'        THEN 1
                WHEN 'RECURRING'         THEN 2
                ELSE 3
            END,
            observation_count DESC
    """)
    fun observeAllRanked(): Flow<List<ConceptEntity>>

    @Query("SELECT * FROM concepts WHERE concept_number = :number LIMIT 1")
    suspend fun getByNumber(number: Int): ConceptEntity?

    @Query("SELECT * FROM concepts WHERE stem = :stem LIMIT 1")
    suspend fun getByStem(stem: String): ConceptEntity?

    @Query("SELECT * FROM concepts")
    suspend fun getAll(): List<ConceptEntity>

    @Query("SELECT * FROM concepts WHERE persistence_level != 'NONE' ORDER BY observation_count DESC")
    suspend fun getSignificant(): List<ConceptEntity>

    @Query("""
        SELECT COUNT(*) FROM concepts
        WHERE persistence_level IN ('PERSISTENT', 'DEEPLY_PERSISTENT')
    """)
    suspend fun getPersistentCount(): Int

    // Check if a stem already exists in any concept
    @Query("SELECT * FROM concepts WHERE stem LIKE '%' || :stem || '%' OR surface_forms LIKE '%' || :stem || '%'")
    suspend fun findByStem(stem: String): List<ConceptEntity>

    // Concepts linked to a specific entry
    @Query("""
        SELECT c.* FROM concepts c
        INNER JOIN concept_entry_relations r ON c.concept_number = r.concept_number
        WHERE r.entry_number = :entryNumber
        ORDER BY r.confidence DESC
    """)
    suspend fun getConceptsForEntry(entryNumber: Int): List<ConceptEntity>

    // Related concepts: concepts that share entries with the given concept
    @Query("""
        SELECT DISTINCT c.* FROM concepts c
        INNER JOIN concept_entry_relations r1 ON c.concept_number = r1.concept_number
        INNER JOIN concept_entry_relations r2 ON r1.entry_number = r2.entry_number
        WHERE r2.concept_number = :conceptNumber
        AND c.concept_number != :conceptNumber
        ORDER BY c.observation_count DESC
        LIMIT 10
    """)
    suspend fun getRelatedConcepts(conceptNumber: Int): List<ConceptEntity>

    // Co-occurrence count between two concepts (shared entries)
    @Query("""
        SELECT COUNT(*) FROM concept_entry_relations r1
        INNER JOIN concept_entry_relations r2 ON r1.entry_number = r2.entry_number
        WHERE r1.concept_number = :c1 AND r2.concept_number = :c2
    """)
    suspend fun getCoOccurrenceCount(c1: Int, c2: Int): Int

    // ─── RELATION QUERY ─────────────────────────────────────────

    @Query("""
        SELECT * FROM concept_entry_relations
        WHERE concept_number = :conceptNumber
        ORDER BY observed_at DESC
    """)
    suspend fun getRelationsForConcept(conceptNumber: Int): List<ConceptEntryRelationEntity>

    @Query("""
        SELECT * FROM concept_entry_relations
        WHERE entry_number = :entryNumber
        ORDER BY confidence DESC
    """)
    suspend fun getRelationsForEntry(entryNumber: Int): List<ConceptEntryRelationEntity>

    @Query("""
        SELECT COUNT(*) FROM concept_entry_relations
        WHERE concept_number = :conceptNumber
        AND observed_at >= :since
    """)
    suspend fun getRecentObservationCount(conceptNumber: Int, since: Long): Int

    // ─── PERSISTENCE UPDATE ─────────────────────────────────────

    @Query("""
        UPDATE concepts
        SET persistence_level = :level,
            observation_count = :count,
            last_observed = :lastObserved,
            span_days = :spanDays,
            recent_count_14d = :recent14d,
            confidence = :confidence,
            last_updated_at = :now
        WHERE concept_number = :conceptNumber
    """)
    suspend fun updatePersistence(
        conceptNumber: Int,
        level: String,
        count: Int,
        lastObserved: Long,
        spanDays: Int,
        recent14d: Int,
        confidence: Int,
        now: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE concepts
        SET surface_forms = :forms, last_updated_at = :now
        WHERE concept_number = :conceptNumber
    """)
    suspend fun updateSurfaceForms(conceptNumber: Int, forms: String, now: Long = System.currentTimeMillis())

    // ─── STATS FOR WIDGET ───────────────────────────────────────

    @Query("""
        SELECT COUNT(*) FROM concepts
        WHERE persistence_level IN ('PERSISTENT', 'DEEPLY_PERSISTENT')
    """)
    suspend fun getWidgetPersistentCount(): Int

    // ─── ANALYSIS WINDOW QUERY ──────────────────────────────────
    // Returns observations within the given time range

    @Query("""
        SELECT COUNT(*) FROM concept_entry_relations
        WHERE concept_number = :conceptNumber
        AND observed_at >= :windowStart
    """)
    suspend fun getObservationCountInWindow(conceptNumber: Int, windowStart: Long): Int
}
