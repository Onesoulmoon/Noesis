package com.necrosed.noesis.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "compositions", indices = [Index(value = ["entry_number"], unique = true)])
data class CompositionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "entry_number") val entryNumber: Int,
    val title: String,
    val subtitle: String?,
    val keyInsight: String?,
    val rawJson: String,
    val modelId: String,
    val status: String = "READY",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "composition_sections", indices = [Index(value = ["composition_id"])])
data class CompositionSectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "composition_id") val compositionId: Long,
    val position: Int,
    val type: String,
    val title: String,
    val content: String,
    val interpretation: String?,
    @ColumnInfo(name = "source_fragments") val sourceFragments: String, // pipe-separated
    @ColumnInfo(name = "epistemic_status") val epistemicStatus: String?
)

@Entity(tableName = "composition_questions", indices = [Index(value = ["composition_id"])])
data class CompositionQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "composition_id") val compositionId: Long,
    val position: Int,
    val question: String
)
