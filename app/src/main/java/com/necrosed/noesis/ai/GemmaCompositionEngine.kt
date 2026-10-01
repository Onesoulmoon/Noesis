package com.necrosed.noesis.ai

import android.content.Context
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Contents
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

    suspend fun compose(
        rawThought: String,
        role: AiRole = AiRole.SYNTHESIS,
        promptSuffix: String = ""
    ): CompositionResult = withContext(Dispatchers.IO) {
        check(modelManager.isInstalled()) { "The on-device model is not installed." }

        val config = EngineConfig(
            modelPath = modelManager.modelFile().absolutePath,
            backend = Backend.GPU(),
            cacheDir = appContext.cacheDir.absolutePath
        )

        try {
            runEngine(config, rawThought, role, promptSuffix)
        } catch (_: Throwable) {
            // GPU availability is device-dependent. Fall back to CPU without
            // sending the thought anywhere.
            runEngine(EngineConfig(
                modelPath = modelManager.modelFile().absolutePath,
                backend = Backend.CPU(),
                cacheDir = appContext.cacheDir.absolutePath
            ), rawThought, role, promptSuffix)
        }
    }

    suspend fun executeRole(rawThought: String, role: AiRole): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = compose(rawThought, role = role)
            Result.success(result.rawJson)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun runEngine(
        config: EngineConfig,
        rawThought: String,
        role: AiRole,
        promptSuffix: String = ""
    ): CompositionResult {
        val systemInstruction = "You are NOESIS COMPOSE, an advanced cognitive archivist and intellectual assistant. Output strictly valid JSON matching the requested schema."

        Engine(config).use { engine ->
            engine.initialize()
            val conversationConfig = ConversationConfig(
                systemInstruction = Contents.of(systemInstruction),
                samplerConfig = SamplerConfig(topK = 32, topP = 0.9, temperature = 0.35)
            )
            engine.createConversation(conversationConfig).use { conversation ->
                val prompt = RolePromptFactory.buildPrompt(role, rawThought, promptSuffix)
                val response = conversation.sendMessage(prompt)
                val text = response.contents.contents
                    .filterIsInstance<Content.Text>()
                    .joinToString("") { it.text }
                return parse(text.ifBlank { response.toString() }, role)
            }
        }
    }

    private fun parse(text: String, role: AiRole): CompositionResult {
        val cleaned = JsonSanitizer.cleanAndExtractJson(text)
        return try {
            val obj = JSONObject(cleaned)
            val sectionsJson = obj.optJSONArray("sections") ?: JSONArray()
            val sections = buildList<CompositionSection> {
                for (i in 0 until sectionsJson.length()) {
                    val s = sectionsJson.getJSONObject(i)
                    val fragmentsJson = s.optJSONArray("sourceFragments") ?: JSONArray()
                    val fragments = buildList<String> { for (j in 0 until fragmentsJson.length()) add(fragmentsJson.optString(j)) }
                    
                    var sectionContent = s.optString("content").trim()
                    
                    // Sanitize nested raw JSON if model encoded it into content string
                    if (sectionContent.startsWith("{") && sectionContent.endsWith("}") && sectionContent.contains("\"title\"")) {
                        try {
                            val innerObj = JSONObject(sectionContent)
                            sectionContent = innerObj.optString("content", innerObj.optString("text", sectionContent))
                        } catch (_: Exception) {}
                    }

                    val sectionTitle = s.optString("title").trim()
                        .takeIf { it.isNotBlank() } ?: "KEY POINT ${i + 1}"

                    add(CompositionSection(
                        type = s.optString("type", "OBSERVATION"),
                        title = sectionTitle,
                        content = sectionContent,
                        interpretation = s.optString("interpretation").trim().takeIf { it.isNotBlank() && it != "null" },
                        epistemicStatus = s.optString("epistemicStatus").takeIf { it.isNotBlank() && it != "null" },
                        sourceFragments = fragments
                    ))
                }
            }.filter { it.content.isNotBlank() }

            val questionsJson = obj.optJSONArray("openQuestions") ?: JSONArray()
            val questions = buildList { for (i in 0 until questionsJson.length()) add(questionsJson.optString(i).trim()) }
                .filter { it.isNotBlank() && it != "null" }

            val parsedTitle = obj.optString("title").trim()
                .takeIf { it.isNotBlank() && it != "null" && it != "UNTITLED THOUGHT" }
                ?: "${role.displayName.uppercase()} ANALYSIS"

            CompositionResult(
                title = parsedTitle,
                subtitle = obj.optString("subtitle").takeIf { it.isNotBlank() && it != "null" },
                sections = if (sections.isNotEmpty()) sections else listOf(
                    CompositionSection(
                        type = "OBSERVATION",
                        title = role.displayName.uppercase(),
                        content = text.replace(Regex("```json|```"), "").trim()
                    )
                ),
                keyInsight = obj.optString("keyInsight").takeIf { it.isNotBlank() && it != "null" },
                openQuestions = questions,
                rawJson = cleaned
            )
        } catch (_: Exception) {
            // Robust fallback if JSON parsing fails completely
            val fallbackProse = text.replace(Regex("```json|```"), "").trim()
            CompositionResult(
                title = "${role.displayName.uppercase()} THOUGHT",
                subtitle = null,
                sections = listOf(
                    CompositionSection(
                        type = "OBSERVATION",
                        title = role.displayName.uppercase(),
                        content = if (fallbackProse.isNotBlank()) fallbackProse else "No response generated."
                    )
                ),
                keyInsight = null,
                openQuestions = emptyList(),
                rawJson = "{}"
            )
        }
    }
}

object JsonSanitizer {
    fun cleanAndExtractJson(rawOutput: String): String {
        var cleaned = rawOutput.trim()

        // Strip markdown backticks
        cleaned = cleaned
            .replace(Regex("^```json\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^```\\s*"), "")
            .replace(Regex("\\s*```$"), "")
            .trim()

        // Extract substring between first '{' and last '}'
        val firstBrace = cleaned.indexOf('{')
        val lastBrace = cleaned.lastIndexOf('}')

        return if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            cleaned.substring(firstBrace, lastBrace + 1)
        } else {
            cleaned
        }
    }
}
