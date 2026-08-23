package com.necrosed.noesis.ai

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** Device/model gate for NOESIS' private on-device LLM. */
class OnDeviceModelManager(private val context: Context) {
    companion object {
        const val MODEL_ID = "Gemma-4-E2B-it"
        const val MODEL_FILE = "gemma-4-E2B-it.litertlm"
        const val MIN_RAM_GB = 6L
        const val EXPECTED_BYTES = 2_583_085_056L
        private const val MODEL_URL =
            "https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm/resolve/7fa1d78473894f7e736a21d920c3aa80f950c0db/gemma-4-E2B-it.litertlm?download=true"
    }

    data class Compatibility(
        val compatible: Boolean,
        val ramGb: Long,
        val arm64: Boolean,
        val freeStorageGb: Long,
        val reason: String? = null
    )

    fun modelFile(): File = File(context.filesDir, MODEL_FILE)

    fun compatibility(): Compatibility {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo().also(am::getMemoryInfo)
        val ramGb = info.totalMem / 1_000_000_000L
        val arm64 = Build.SUPPORTED_ABIS.any { it == "arm64-v8a" }
        val freeGb = context.filesDir.freeSpace / 1_000_000_000L
        val reason = when {
            !arm64 -> "NOESIS requires a 64-bit ARM device for Gemma 4 E2B."
            ramGb < MIN_RAM_GB -> "Gemma 4 E2B requires at least 8 GB RAM."
            freeGb < 4L -> "At least 4 GB of free storage is recommended for the model and runtime cache."
            else -> null
        }
        return Compatibility(reason == null, ramGb, arm64, freeGb, reason)
    }

    fun isInstalled(): Boolean = modelFile().let { it.exists() && it.length() == EXPECTED_BYTES }

    suspend fun download(onProgress: (Int) -> Unit = {}): File = withContext(Dispatchers.IO) {
        val compatibility = compatibility()
        check(compatibility.compatible) { compatibility.reason ?: "Device is not compatible." }

        val target = modelFile()
        val temp = File(context.filesDir, "$MODEL_FILE.download")
        val connection = (URL(MODEL_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 20_000
            readTimeout = 60_000
            instanceFollowRedirects = true
            requestMethod = "GET"
        }
        try {
            connection.connect()
            check(connection.responseCode in 200..299) { "Model download failed: HTTP ${connection.responseCode}" }
            val total = connection.contentLengthLong.takeIf { it > 0 } ?: EXPECTED_BYTES
            connection.inputStream.use { input ->
                temp.outputStream().use { output ->
                    val buffer = ByteArray(1024 * 1024)
                    var readTotal = 0L
                    var last = -1
                    while (true) {
                        val n = input.read(buffer)
                        if (n < 0) break
                        output.write(buffer, 0, n)
                        readTotal += n
                        val progress = ((readTotal * 100L) / total).toInt().coerceIn(0, 100)
                        if (progress != last) { last = progress; onProgress(progress) }
                    }
                }
            }
            check(temp.length() == EXPECTED_BYTES) {
                "Downloaded model size mismatch: ${temp.length()} bytes."
            }
            if (target.exists()) target.delete()
            check(temp.renameTo(target)) { "Unable to finalize model download." }
            target
        } catch (t: Throwable) {
            temp.delete()
            throw t
        } finally {
            connection.disconnect()
        }
    }
}
