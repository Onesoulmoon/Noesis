package com.necrosed.noesis.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════════════════
// NOESIS — CONCEPT DATABASE ENTITIES
//
// The machine's interpretation layer. NEVER overwrites entries.
//
// concepts                → Identified recurring themes / ideas.
// concept_entry_relations → Junction table linking concepts to
//                           the specific entries where they appear.
//
// This separation means:
//   - The NLP engine can be completely replaced in v2.0
//   - Relations can be re-derived from the original text
//   - The user's thoughts remain the source of truth
// ═══════════════════════════════════════════════════════════════

@Entity(
    tableName = "concepts",
    indices = [
        Index(value = ["concept_number"], unique = true),
        Index(value = ["stem"]),
        Index(value = ["persistence_level"]),
        Index(value = ["last_observed"])
    ]
)
data class ConceptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // C-XXXX — user-visible concept identifier
    @ColumnInfo(name = "concept_number")
    val conceptNumber: Int,

    // Human-readable label: "BUILDING SOFTWARE", "IDENTITY", "CREATION"
    @ColumnInfo(name = "label")
    val label: String,

    // Normalized canonical stem used for matching
    // e.g. "build" (for "building", "built", "builds")
    @ColumnInfo(name = "stem")
    val stem: String,

    // All surface forms observed, pipe-separated: "build|building|built|builds"
    @ColumnInfo(name = "surface_forms")
    val surfaceForms: String = "",

    // Language of this concept: "en", "fr", "mixed"
    @ColumnInfo(name = "language")
    val language: String = "en",

    // NONE | RECURRING | PERSISTENT | DEEPLY_PERSISTENT
    @ColumnInfo(name = "persistence_level")
    val persistenceLevel: String = PersistenceLevel.NONE,

    // Total observations across all time
    @ColumnInfo(name = "observation_count")
    val observationCount: Int = 0,

    // Milliseconds — from the oldest entry containing this concept
    @ColumnInfo(name = "first_observed")
    val firstObserved: Long,

    // Milliseconds — from the most recent entry
    @ColumnInfo(name = "last_observed")
    val lastObserved: Long,

    // How many days between first and last observation
    @ColumnInfo(name = "span_days")
    val spanDays: Int = 0,

    // Observations in last 14 days (updated on each analysis pass)
    @ColumnInfo(name = "recent_count_14d")
    val recentCount14d: Int = 0,

    // Confidence of the concept's significance: 0–100
    @ColumnInfo(name = "confidence")
    val confidence: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long = System.currentTimeMillis()
)

object PersistenceLevel {
    const val NONE             = "NONE"
    const val RECURRING        = "RECURRING"
    const val PERSISTENT       = "PERSISTENT"
    const val DEEPLY_PERSISTENT = "DEEPLY_PERSISTENT"

    // Thresholds — user-configurable in ArchiveScreen, hardcoded defaults here
    const val RECURRING_MIN_OBS         = 2
    const val PERSISTENT_MIN_OBS        = 3
    const val PERSISTENT_MIN_SPAN_DAYS  = 7
    const val DEEP_MIN_OBS              = 5
    const val DEEP_MIN_SPAN_DAYS        = 30
    const val DEEP_MIN_RECENT_14D       = 2
}

// ─── CONCEPT-ENTRY RELATION ─────────────────────────────────────

@Entity(
    tableName = "concept_entry_relations",
    primaryKeys = ["concept_number", "entry_number"],
    indices = [
        Index(value = ["entry_number"]),
        Index(value = ["concept_number"]),
        Index(value = ["observed_at"])
    ]
)
data class ConceptEntryRelationEntity(
    @ColumnInfo(name = "concept_number")
    val conceptNumber: Int,

    @ColumnInfo(name = "entry_number")
    val entryNumber: Int,

    // 0.0–1.0 — how strongly this concept appears in this entry
    @ColumnInfo(name = "confidence")
    val confidence: Float,

    // Pipe-separated matched terms: "build|building|software build"
    @ColumnInfo(name = "matched_terms")
    val matchedTerms: String,

    // Timestamp of the source entry
    @ColumnInfo(name = "observed_at")
    val observedAt: Long = System.currentTimeMillis()
)
