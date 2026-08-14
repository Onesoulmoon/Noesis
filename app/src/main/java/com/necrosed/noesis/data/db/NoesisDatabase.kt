package com.necrosed.noesis.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import com.necrosed.noesis.data.db.dao.ConceptDao
import com.necrosed.noesis.data.db.dao.EntryDao
import com.necrosed.noesis.data.db.dao.CompositionDao
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
        ConceptEntryRelationEntity::class,
        CompositionEntity::class,
        CompositionSectionEntity::class,
        CompositionQuestionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class NoesisDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun conceptDao(): ConceptDao
    abstract fun compositionDao(): CompositionDao

    companion object {
        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS compositions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, entry_number INTEGER NOT NULL, title TEXT NOT NULL, subtitle TEXT, keyInsight TEXT, rawJson TEXT NOT NULL, modelId TEXT NOT NULL, status TEXT NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_compositions_entry_number ON compositions(entry_number)")
                db.execSQL("CREATE TABLE IF NOT EXISTS composition_sections (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, composition_id INTEGER NOT NULL, position INTEGER NOT NULL, type TEXT NOT NULL, title TEXT NOT NULL, content TEXT NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_composition_sections_composition_id ON composition_sections(composition_id)")
                db.execSQL("CREATE TABLE IF NOT EXISTS composition_questions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, composition_id INTEGER NOT NULL, position INTEGER NOT NULL, question TEXT NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_composition_questions_composition_id ON composition_questions(composition_id)")
            }
        }
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
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}
