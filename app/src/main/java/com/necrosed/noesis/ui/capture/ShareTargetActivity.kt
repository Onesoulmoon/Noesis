package com.necrosed.noesis.ui.capture

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.necrosed.noesis.data.db.NoesisDatabase
import com.necrosed.noesis.data.model.CaptureInput
import com.necrosed.noesis.data.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShareTargetActivity : ComponentActivity() {

    private val db by lazy { NoesisDatabase.getInstance(applicationContext) }
    private val entryRepository by lazy {
        EntryRepository(db.entryDao(), db.conceptDao(), db.compositionDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            handleSharedText(intent)
        } else {
            finish()
        }
    }

    private fun handleSharedText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        val sharedSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: "Quick Capture"

        if (!sharedText.isNullOrBlank()) {
            val contentToSave = if (sharedSubject != "Quick Capture" && sharedSubject.isNotBlank()) {
                "$sharedSubject\n\n$sharedText"
            } else {
                sharedText
            }

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    entryRepository.captureEntry(CaptureInput(content = contentToSave))
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@ShareTargetActivity, "Saved to Noesis Vault", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (_: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@ShareTargetActivity, "Failed to save capture", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        } else {
            finish()
        }
    }
}
