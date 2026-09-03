package com.example.repomaster.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    private val MIGRATION_2_3 =
        object : Migration(2, 3) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS pending_image_uploads (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        vehicleNumber TEXT NOT NULL,
                        status TEXT NOT NULL,
                        uploadStatus TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

    private val MIGRATION_3_4 =
        object : Migration(3, 4) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN agencyId TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )
            }
        }

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "repomaster_database"
            )
                .addMigrations(
                    MIGRATION_2_3,
                    MIGRATION_3_4
                )
                .build()

            INSTANCE = instance

            instance
        }
    }
}