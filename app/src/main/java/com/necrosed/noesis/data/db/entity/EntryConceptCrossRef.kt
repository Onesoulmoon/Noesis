package com.necrosed.noesis.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = "entry_concept_cross_ref",
    primaryKeys = ["entryNumber", "conceptNumber"]
)
data class EntryConceptCrossRef(
    val entryNumber: Int,
    val conceptNumber: Int
)
