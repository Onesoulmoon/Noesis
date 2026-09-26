package com.necrosed.noesis.pkm.repository

import com.necrosed.noesis.ai.AiRole
import com.necrosed.noesis.ai.GemmaCompositionEngine
import com.necrosed.noesis.data.db.dao.CompositionDao
import com.necrosed.noesis.data.db.dao.EntryDao
import com.necrosed.noesis.data.model.CaptureInput
import com.necrosed.noesis.data.repository.EntryRepository
import com.necrosed.noesis.pkm.parser.WikiLinkParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PkmRepository(
    private val entryDao: EntryDao,
    private val compositionDao: CompositionDao,
    private val entryRepository: EntryRepository,
    private val gemmaEngine: GemmaCompositionEngine
) {
    /**
     * Core PKM Action: Saves entry & indexes [[WikiLinks]] deterministically (zero AI cost).
     */
    suspend fun saveEntry(title: String, body: String): Int = withContext(Dispatchers.IO) {
        val fullContent = if (title.isNotBlank()) "$title\n\n$body" else body
        val parsed = WikiLinkParser.parse(fullContent)
        
        val entryNumber = entryRepository.captureEntry(CaptureInput(content = parsed.rawContent))
        entryRepository.analyzeCapturedEntry(entryNumber)
        entryNumber
    }

    /**
     * Optional AI Surface: Runs an AI Role lens on-demand over an existing note.
     */
    suspend fun applyAiRoleToEntry(entryNumber: Int, body: String, role: AiRole): Result<String> {
        return gemmaEngine.executeRole(body, role).onSuccess {
            entryRepository.composeEntry(entryNumber, role = role)
        }
    }
}
