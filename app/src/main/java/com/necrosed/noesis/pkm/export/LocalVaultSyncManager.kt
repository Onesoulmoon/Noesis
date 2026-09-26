package com.necrosed.noesis.pkm.export

import com.necrosed.noesis.data.db.dao.EntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalVaultSyncManager(
    private val entryDao: EntryDao
) {
    /**
     * Synchronizes all internal Room entries to a designated external storage directory
     * readable by local-first apps like Obsidian or Logseq.
     */
    suspend fun syncVaultToStorage(targetDirectory: File): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            if (!targetDirectory.exists()) {
                targetDirectory.mkdirs()
            }

            val entries = entryDao.getAllActive()
            var exportedCount = 0

            entries.forEach { entry ->
                VaultExporter.exportToMarkdown(entry, targetDirectory)
                exportedCount++
            }

            exportedCount
        }
    }
}
