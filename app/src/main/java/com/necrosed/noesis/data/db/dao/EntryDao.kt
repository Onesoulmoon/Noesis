package com.necrosed.noesis.data.db.dao

import androidx.room.*
import com.necrosed.noesis.data.db.entity.EntryEntity
import com.necrosed.noesis.data.db.entity.EntryRevisionEntity
import com.necrosed.noesis.data.db.entity.EntrySequenceEntity
import com.necrosed.noesis.data.db.entity.EntryStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    // ─── SEQUENCE ───────────────────────────────────────────────

    @Query("SELECT * FROM entry_sequence WHERE id = 1")
    suspend fun getSequence(): EntrySequenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSequence(seq: EntrySequenceEntity)

    @Transaction
    suspend fun nextEntryNumber(): Int {
        val seq = getSequence() ?: EntrySequenceEntity()
        val next = seq.nextEntryNumber
        upsertSequence(seq.copy(nextEntryNumber = next + 1))
        return next
    }

    @Transaction
    suspend fun nextConceptNumber(): Int {
        val seq = getSequence() ?: EntrySequenceEntity()
        val next = seq.nextConceptNumber
        upsertSequence(seq.copy(nextConceptNumber = next + 1))
        return next
    }

    // ─── INSERT ─────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(revision: EntryRevisionEntity): Long

    // ─── QUERY ──────────────────────────────────────────────────

    @Query("SELECT * FROM entries WHERE status != 'PURGED' ORDER BY created_at DESC")
    fun observeAllActive(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries ORDER BY created_at DESC")
    fun observeAll(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE entry_number = :number LIMIT 1")
    suspend fun getByNumber(number: Int): EntryEntity?

    @Query("SELECT * FROM entries WHERE status = 'ACTIVE' ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 5): List<EntryEntity>

    @Query("SELECT COUNT(*) FROM entries WHERE status = 'ACTIVE'")
    suspend fun getActiveCount(): Int

    @Query("SELECT COUNT(*) FROM entries WHERE status = 'ACTIVE' AND is_unresolved = 1")
    suspend fun getUnresolvedCount(): Int

    // Stream query
    @Query("SELECT * FROM entries WHERE status = 'ACTIVE' ORDER BY created_at DESC")
    fun streamActive(): Flow<List<EntryEntity>>

    @Query("""
        SELECT * FROM entries
        WHERE status = 'ACTIVE'
        AND (content LIKE '%' || :query || '%')
        ORDER BY created_at DESC
    """)
    fun searchEntries(query: String): Flow<List<EntryEntity>>

    // Entries referencing a concept (via relations)
    @Query("""
        SELECT e.* FROM entries e
        INNER JOIN concept_entry_relations r ON e.entry_number = r.entry_number
        WHERE r.concept_number = :conceptNumber
        AND e.status != 'PURGED'
        ORDER BY e.created_at DESC
    """)
    suspend fun getEntriesForConcept(conceptNumber: Int): List<EntryEntity>

    // ─── REVISIONS ──────────────────────────────────────────────

    @Query("""
        SELECT * FROM entry_revisions
        WHERE entry_number = :entryNumber
        ORDER BY revision_number ASC
    """)
    suspend fun getRevisions(entryNumber: Int): List<EntryRevisionEntity>

    @Query("""
        SELECT MAX(revision_number) FROM entry_revisions
        WHERE entry_number = :entryNumber
    """)
    suspend fun getLatestRevisionNumber(entryNumber: Int): Int?

    // ─── UPDATE ─────────────────────────────────────────────────

    @Query("""
        UPDATE entries
        SET status = :status, last_modified_at = :now
        WHERE entry_number = :entryNumber
    """)
    suspend fun updateStatus(entryNumber: Int, status: String, now: Long = System.currentTimeMillis())

    @Query("""
        UPDATE entries
        SET is_unresolved = :unresolved, last_modified_at = :now
        WHERE entry_number = :entryNumber
    """)
    suspend fun updateUnresolved(entryNumber: Int, unresolved: Boolean, now: Long = System.currentTimeMillis())

    @Query("""
        UPDATE entries
        SET language = :language
        WHERE entry_number = :entryNumber
    """)
    suspend fun updateLanguage(entryNumber: Int, language: String)

    // ─── PURGE ──────────────────────────────────────────────────
    // Permanent deletion. Content is wiped, row is retained for continuity.
    // The sequence number N-XXXX is permanently retired.

    @Query("""
        UPDATE entries
        SET content = '', status = 'PURGED',
            purged_at = :now, last_modified_at = :now
        WHERE entry_number = :entryNumber
    """)
    suspend fun purgeEntry(entryNumber: Int, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM entry_revisions WHERE entry_number = :entryNumber")
    suspend fun deleteRevisions(entryNumber: Int)

    // ─── STATS ──────────────────────────────────────────────────

    @Query("""
        SELECT COUNT(*) FROM entries
        WHERE status = 'ACTIVE'
        AND created_at >= :since
    """)
    suspend fun getCountSince(since: Long): Int

    @Query("SELECT * FROM entries WHERE status = 'ACTIVE' ORDER BY created_at ASC LIMIT 1")
    suspend fun getOldestActive(): EntryEntity?
}
