package com.necrosed.noesis.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import com.necrosed.noesis.data.db.dao.ConceptDao
import com.necrosed.noesis.data.db.dao.EntryDao
import com.necrosed.noesis.data.db.entity.*
import com.necrosed.noesis.security.KeystoreManager

// ═══════════════════════════════════════════════════════════════
// NOESIS — ENCRYPTED DATABASE
//
// Room over SQLCipher. The database file is encrypted at rest.
// The passphrase is generated once by SecureRandom, stored in
// EncryptedSharedPreferences (Android Keystore backed), and
// never appears in plaintext outside of KeystoreManager.
//
// noesis_archive.db is the only persistent state in the app.
// No cloud. No analytics. No logs. The archive stays local.
// ═══════════════════════════════════════════════════════════════

@Database(
    entities = [
        EntryEntity::class,
        EntryRevisionEntity::class,
        EntrySequenceEntity::class,
        ConceptEntity::class,
        ConceptEntryRelationEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NoesisDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun conceptDao(): ConceptDao

    companion object {
        private const val DB_NAME = "noesis_archive.db"

        @Volatile
        private var INSTANCE: NoesisDatabase? = null

        fun getInstance(context: Context): NoesisDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): NoesisDatabase {
            val passphrase = KeystoreManager.getOrCreatePassphrase(context)
            val factory = SupportFactory(
                SQLiteDatabase.getBytes(passphrase),
                null,
                false
            )
            // Wipe the passphrase from memory after handing to SQLCipher
            KeystoreManager.wipeCharArray(passphrase)

            return Room.databaseBuilder(
                context.applicationContext,
                NoesisDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(factory)
                // !! Remove fallbackToDestructiveMigration before 1.0 release.
                // Replace with proper Migration objects as schema evolves.
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
