package com.necrosed.noesis.ai

import com.necrosed.noesis.data.db.entity.*
import com.necrosed.noesis.data.model.CompositionSection

data class CompositionResult(
    val title: String,
    val subtitle: String?,
    val sections: List<CompositionSection>,
    val keyInsight: String?,
    val openQuestions: List<String>,
    val rawJson: String,
    val modelId: String = OnDeviceModelManager.MODEL_ID
)

fun CompositionResult.toEntities(entryNumber: Int): Triple<CompositionEntity, List<CompositionSectionEntity>, List<CompositionQuestionEntity>> {
    val composition = CompositionEntity(
        entryNumber = entryNumber,
        title = title,
        subtitle = subtitle,
        keyInsight = keyInsight,
        rawJson = rawJson,
        modelId = modelId
    )
    // IDs are assigned by Room; DAO replaces the composition and patches these IDs.
    return Triple(
        composition,
        sections.mapIndexed { i, s -> 
            CompositionSectionEntity(
                id = 0, 
                compositionId = 0, 
                position = i, 
                type = s.type, 
                title = s.title, 
                content = s.content,
                interpretation = s.interpretation,
                sourceFragments = s.sourceFragments.joinToString("|"),
                epistemicStatus = s.epistemicStatus
            ) 
        },
        openQuestions.mapIndexed { i, q -> CompositionQuestionEntity(0, 0, i, q) }
    )
}
