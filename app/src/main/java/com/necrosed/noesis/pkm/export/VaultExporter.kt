package com.necrosed.noesis.pkm.export

import com.necrosed.noesis.data.db.entity.EntryEntity
import java.io.File
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object VaultExporter {

    /**
     * Converts a database entry into an Obsidian/Logseq-compatible Markdown file with YAML headers.
     */
    fun exportToMarkdown(entry: EntryEntity, outputDir: File): File {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val title = "N-${entry.entryNumber.toString().padStart(4, '0')}"
        val sanitizedTitle = title.replace(Regex("[^a-zA-Z0-9-_]"), "_")
        val file = File(outputDir, "$sanitizedTitle.md")

        val yamlFrontmatter = """
            ---
            entry_number: ${entry.entryNumber}
            language: "${entry.language}"
            is_unresolved: ${entry.isUnresolved}
            created_at: ${entry.createdAt}
            last_modified_at: ${entry.lastModifiedAt}
            ---
            
        """.trimIndent()

        file.writeText("$yamlFrontmatter\n${entry.content}")
        return file
    }
}

object VaultEncryptor {
    private const val TRANSFORM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128

    fun encryptVaultData(data: ByteArray, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(data)
        return Pair(encryptedData, cipher.iv)
    }

    fun decryptVaultData(encryptedData: ByteArray, secretKey: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORM)
        val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedData)
    }
}
