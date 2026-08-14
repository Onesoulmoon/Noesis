package com.necrosed.noesis.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

// ═══════════════════════════════════════════════════════════════
// NOESIS — KEYSTORE MANAGER
//
// The database passphrase never appears in plaintext outside
// this class. Flow:
//
//   First launch  → SecureRandom generates 64-char hex passphrase
//                 → Stored in EncryptedSharedPreferences
//                 → EncryptedSharedPreferences is backed by
//                   an AES256-GCM key in Android Keystore
//
//   All launches  → Retrieve passphrase from EncryptedSharedPreferences
//                 → Pass as CharArray to SQLCipher SupportFactory
//                 → Clear CharArray from memory after use
//
// The passphrase never hits SharedPreferences in plaintext.
// The Keystore key never leaves secure hardware (on supported devices).
// ═══════════════════════════════════════════════════════════════

object KeystoreManager {

    private const val PREFS_FILE    = "noesis_vault"
    private const val KEY_PASSPHRASE = "archive_key"
    private const val MASTER_KEY_ALIAS = "noesis_master"

    fun getOrCreatePassphrase(context: Context): CharArray {
        val masterKey = MasterKey.Builder(context, MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(false)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val existing = prefs.getString(KEY_PASSPHRASE, null)
        if (existing != null) return existing.toCharArray()

        val passphrase = generatePassphrase()
        val passphraseStr = String(passphrase)
        prefs.edit().putString(KEY_PASSPHRASE, passphraseStr).apply()
        return passphrase
    }

    private fun generatePassphrase(): CharArray {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        // 64-char hex string — unambiguous, no special chars
        val hex = bytes.joinToString("") { "%02x".format(it) }
        return hex.toCharArray()
    }

    // Call after handing passphrase to SQLCipher to minimize exposure window
    fun wipeCharArray(chars: CharArray) {
        chars.fill('\u0000')
    }
}
