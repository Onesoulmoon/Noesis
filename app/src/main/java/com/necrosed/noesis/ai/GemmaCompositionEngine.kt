package com.necrosed.noesis.ai

import android.content.Context
import com.google.ai.edge.litertlm.Backend
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

    suspend fun compose(rawThought: String, promptSuffix: String = ""): CompositionResult = withContext(Dispatchers.IO) {
        check(modelManager.isInstalled()) { "The on-device model is not installed." }

        val config = EngineConfig(
            modelPath = modelManager.modelFile().absolutePath,
            backend = Backend.GPU(),
            cacheDir = appContext.cacheDir.absolutePath
        )

        try {
            runEngine(config, rawThought, promptSuffix)
        } catch (_: Throwable) {
            // GPU availability is device-dependent. Fall back to CPU without
            // sending the thought anywhere.
            runEngine(EngineConfig(
                modelPath = modelManager.modelFile().absolutePath,
                backend = Backend.CPU(),
                cacheDir = appContext.cacheDir.absolutePath
            ), rawThought, promptSuffix)
        }
    }

    private fun runEngine(config: EngineConfig, rawThought: String, promptSuffix: String = ""): CompositionResult {
        val system = """You are NOESIS COMPOSE, a cognitive archivist and intellectual editor.
Your job is to discover and reconstruct the latent intellectual structure hiding inside a user's raw, messy dump.

Follow these four operations:
01 — CORRECT: Fix spelling, typos, punctuation, and duplications. Preserve the user's authentic voice; do not sanitize it.
02 — DISTILL: Identify the central question or argument. Explicitly model meaningful TENSIONS and CONTRADICTIONS rather than burying them in prose. Use the "TENSION" section type for these.
03 — ARCHITECT: Discover the ideas contained within. Group material into coherent sections that are already latent in the dump.
04 — SYNTHESIZE: Write a concise, beautiful representation of each structure.

RULES:
- Distinguish observations (the user's ideas) from interpretations (your analysis).
- Use precise, descriptive titles for sections.
- For each section, provide a list of "sourceFragments" (exact short quotes from the raw dump) that justify the section.
- For each section, provide an "interpretation" field explaining WHY these fragments belong together in this section.
- For each section, assess the "epistemicStatus": FACT (asserted as true), BELIEF (personal conviction), HYPOTHESIS (speculation), QUESTION (unresolved inquiry), or OBSERVATION (neutral report).
- BE CAUTIOUS with contradictions: use words like "TENSION", "POSSIBLE CONTRADICTION", "UNRESOLVED", or "SHIFT IN POSITION" unless the evidence is overwhelming.
- Make the result substantially more concise than the raw dump.
- Return ONLY valid JSON matching this schema:
{"title":string,"subtitle":string|null,"sections":[{"type":"ARGUMENT|OBSERVATION|QUESTION|TENSION|INTERPRETATION|CONTEXT|CONCLUSION","title":string,"content":string,"interpretation":string,"epistemicStatus":"FACT|BELIEF|HYPOTHESIS|QUESTION|OBSERVATION","sourceFragments":[string]}],"keyInsight":string|null,"openQuestions":[string]}
""".trimIndent()

        Engine(config).use { engine ->
            engine.initialize()
            val conversationConfig = ConversationConfig(
                systemInstruction = Contents.of(system),
                samplerConfig = SamplerConfig(topK = 32, topP = 0.9, temperature = 0.35)
            )
            engine.createConversation(conversationConfig).use { conversation ->
                val prompt = """Discover the intellectual structure in the following RAW DUMP.

RAW DUMP:
---
$rawThought
---

Compose the result into a beautiful, structured document following the NOESIS COMPOSE principles.
$promptSuffix"""
                val response = conversation.sendMessage(prompt)
                val text = response.contents.contents
                    .filterIsInstance<com.google.ai.edge.litertlm.Content.Text>()
                    .joinToString("") { it.text }
                return parse(text.ifBlank { response.toString() })
            }
        }
    }

    private fun parse(text: String): CompositionResult {
        val cleaned = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
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
                    interpretation = s.optString("interpretation").trim(),
                    epistemicStatus = s.optString("epistemicStatus").takeIf { it.isNotBlank() },
                    sourceFragments = fragments
                ))
            }
        }.filter { it.title.isNotBlank() && it.content.isNotBlank() }
        val questionsJson = obj.optJSONArray("openQuestions") ?: JSONArray()
        val questions = buildList { for (i in 0 until questionsJson.length()) add(questionsJson.optString(i).trim()) }
            .filter { it.isNotBlank() }
        return CompositionResult(
            title = obj.optString("title", "UNTITLED THOUGHT").trim(),
            subtitle = obj.optString("subtitle").takeIf { it.isNotBlank() && it != "null" },
            sections = sections,
            keyInsight = obj.optString("keyInsight").takeIf { it.isNotBlank() && it != "null" },
            openQuestions = questions,
            rawJson = cleaned
        )
    }
}
