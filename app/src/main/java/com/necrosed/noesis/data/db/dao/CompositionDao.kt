package com.necrosed.noesis.data.db.dao

import androidx.room.*
import com.necrosed.noesis.data.db.entity.*

@Dao
interface CompositionDao {
    @Query("SELECT * FROM compositions WHERE entry_number = :entryNumber LIMIT 1")
    suspend fun get(entryNumber: Int): CompositionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(composition: CompositionEntity): Long

    @Insert
    suspend fun insertSections(sections: List<CompositionSectionEntity>)

    @Insert
    suspend fun insertQuestions(questions: List<CompositionQuestionEntity>)

    @Query("SELECT * FROM composition_sections WHERE composition_id = :compositionId ORDER BY position ASC")
    suspend fun getSections(compositionId: Long): List<CompositionSectionEntity>

    @Query("SELECT * FROM composition_questions WHERE composition_id = :compositionId ORDER BY position ASC")
    suspend fun getQuestions(compositionId: Long): List<CompositionQuestionEntity>

    @Query("DELETE FROM composition_sections WHERE composition_id = :compositionId")
    suspend fun deleteSections(compositionId: Long)

    @Query("DELETE FROM composition_questions WHERE composition_id = :compositionId")
    suspend fun deleteQuestions(compositionId: Long)

    @Query("DELETE FROM compositions WHERE entry_number = :entryNumber")
    suspend fun delete(entryNumber: Int)

    @Transaction
    suspend fun replace(
        composition: CompositionEntity,
        sections: List<CompositionSectionEntity>,
        questions: List<CompositionQuestionEntity>
    ) {
        val old = get(composition.entryNumber)
        if (old != null) {
            deleteSections(old.id)
            deleteQuestions(old.id)
            delete(composition.entryNumber)
        }
        val id = insert(composition)
        insertSections(sections.map { it.copy(compositionId = id) })
        insertQuestions(questions.map { it.copy(compositionId = id) })
    }
}
