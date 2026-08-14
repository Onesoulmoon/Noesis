package com.necrosed.noesis.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════════════════
// NOESIS — ENTRY DATABASE ENTITIES
//
// Three tables:
//   entries          — The primary archive. Each entry is a
//                      single thought-capture moment.
//   entry_revisions  — Preserves the history of an entry.
//                      The original is sacred; revisions layer on top.
//   entry_sequence   — Global monotonic counter. Never reused.
//                      A purged entry leaves a permanent retirement.
// ═══════════════════════════════════════════════════════════════

@Entity(
    tableName = "entries",
    indices = [
        Index(value = ["entry_number"], unique = true),
        Index(value = ["created_at"]),
        Index(value = ["status"])
    ]
)
data class EntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // N-XXXX — the user-visible identifier. Permanent. Never recycled.
    @ColumnInfo(name = "entry_number")
    val entryNumber: Int,

    // The original thought. SACRED. Never overwritten.
    // Revisions live in entry_revisions, never here.
    @ColumnInfo(name = "content")
    val content: String,

    // ACTIVE | ARCHIVED | PURGED
    // PURGED entries keep the row (for sequence continuity) but content is null'd.
    @ColumnInfo(name = "status")
    val status: String = EntryStatus.ACTIVE,

    // Detected language: "en", "fr", "unknown"
    @ColumnInfo(name = "language")
    val language: String = "unknown",

    // Whether this entry looks like an open question / unresolved thought.
    // Auto-detected OR user-toggled.
    @ColumnInfo(name = "is_unresolved")
    val isUnresolved: Boolean = false,

    // Milliseconds since epoch
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_modified_at")
    val lastModifiedAt: Long = System.currentTimeMillis(),

    // If PURGED: when it happened. The content field is cleared.
    @ColumnInfo(name = "purged_at")
    val purgedAt: Long? = null
)

object EntryStatus {
    const val ACTIVE   = "ACTIVE"
    const val ARCHIVED = "ARCHIVED"
    const val PURGED   = "PURGED"
}

// ─── REVISIONS ──────────────────────────────────────────────────

@Entity(
    tableName = "entry_revisions",
    indices = [Index(value = ["entry_number", "revision_number"], unique = true)]
)
data class EntryRevisionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "entry_number")
    val entryNumber: Int,

    // 01, 02, 03... monotonic per entry
    @ColumnInfo(name = "revision_number")
    val revisionNumber: Int,

    // The revised content text
    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

// ─── SEQUENCE GENERATOR ─────────────────────────────────────────
// Single row. Never deleted. Transactionally incremented.

@Entity(tableName = "entry_sequence")
data class EntrySequenceEntity(
    @PrimaryKey
    val id: Int = 1,       // Always row 1 — singleton

    @ColumnInfo(name = "next_entry_number")
    val nextEntryNumber: Int = 1,

    @ColumnInfo(name = "next_concept_number")
    val nextConceptNumber: Int = 1
)
