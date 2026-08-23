package com.necrosed.noesis.ai

import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.SamplerConfig
import com.necrosed.noesis.data.model.CompositionSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GemmaCompositionEngine(context: Context) {
    private val appContext = context.applicationContext
    private val modelManager = OnDeviceModelManager(appContext)

    suspend fun processEntry(
        inputContent: String,
        role: AiRole = AiRole.SYNTHESIS
    ): Result<String> = runCatching {
        val prompt = RolePromptFactory.buildPrompt(role, inputContent)
        runInference(prompt)
    }

    suspend fun compose(
        rawThought: String,
        role: AiRole = AiRole.SYNTHESIS,
        promptSuffix: String = ""
    ): CompositionResult = withContext(Dispatchers.IO) {
        check(modelManager.isInstalled()) { "The on-device model is not installed." }

        val prompt = RolePromptFactory.buildPrompt(role, rawThought, promptSuffix)

        val responseText = runInference(prompt)
        parse(responseText, role)
    }

    private fun runInference(fullPrompt: String): String {
        val configGPU = EngineConfig(
            modelPath = modelManager.modelFile().absolutePath,
            backend = Backend.GPU(),
            cacheDir = appContext.cacheDir.absolutePath
        )

        return try {
            runEngine(configGPU, fullPrompt)
        } catch (_: Throwable) {
            // Fallback to CPU if GPU inference is not available
            val configCPU = EngineConfig(
                modelPath = modelManager.modelFile().absolutePath,
                backend = Backend.CPU(),
                cacheDir = appContext.cacheDir.absolutePath
            )
            runEngine(configCPU, fullPrompt)
        }
    }

    private fun runEngine(config: EngineConfig, fullPrompt: String): String {
        Engine(config).use { engine ->
            engine.initialize()
            val conversationConfig = ConversationConfig(
                samplerConfig = SamplerConfig(topK = 32, topP = 0.9, temperature = 0.35)
            )
            engine.createConversation(conversationConfig).use { conversation ->
                val response = conversation.sendMessage(fullPrompt)
                val text = response.contents.contents
                    .filterIsInstance<Content.Text>()
                    .joinToString("") { it.text }
                return text.ifBlank { response.toString() }
            }
        }
    }

    private fun parse(text: String, role: AiRole): CompositionResult {
        val cleaned = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        return try {
            val obj = JSONObject(cleaned)
            val sectionsJson = obj.optJSONArray("sections") ?: JSONArray()
            val sections = buildList<CompositionSection> {
                for (i in 0 until sectionsJson.length()) {
                    val s = sectionsJson.getJSONObject(i)
                    val fragmentsJson = s.optJSONArray("sourceFragments") ?: JSONArray()
                    val fragments = buildList<String> { for (j in 0 until fragmentsJson.length()) add(fragmentsJson.optString(j)) }
                    add(CompositionSection(
                        type = s.optString("type", "OBSERVATION"),
                        title = s.optString("title").trim(),
                        content = s.optString("content").trim(),
                        interpretation = s.optString("interpretation").trim().takeIf { it.isNotBlank() },
                        epistemicStatus = s.optString("epistemicStatus").takeIf { it.isNotBlank() },
                        sourceFragments = fragments
                    ))
                }
            }.filter { it.title.isNotBlank() && it.content.isNotBlank() }

            val questionsJson = obj.optJSONArray("openQuestions") ?: JSONArray()
            val questions = buildList { for (i in 0 until questionsJson.length()) add(questionsJson.optString(i).trim()) }
                .filter { it.isNotBlank() }

            CompositionResult(
                title = obj.optString("title", role.displayName.uppercase()).trim(),
                subtitle = obj.optString("subtitle").takeIf { it.isNotBlank() && it != "null" },
                sections = sections.ifEmpty {
                    listOf(CompositionSection(type = "OBSERVATION", title = role.displayName, content = cleaned))
                },
                keyInsight = obj.optString("keyInsight").takeIf { it.isNotBlank() && it != "null" },
                openQuestions = questions,
                rawJson = cleaned,
                role = role
            )
        } catch (_: Exception) {
            CompositionResult(
                title = role.displayName.uppercase(),
                subtitle = "PARSED OUTPUT",
                sections = listOf(
                    CompositionSection(
                        type = "OBSERVATION",
                        title = role.displayName,
                        content = cleaned
                    )
                ),
                keyInsight = null,
                openQuestions = emptyList(),
                rawJson = cleaned,
                role = role
            )
        }
    }
}
