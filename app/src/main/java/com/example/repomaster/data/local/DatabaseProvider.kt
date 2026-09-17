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

    private val MIGRATION_4_5 =
        object : Migration(4, 5) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN userId TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN userEmail TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )
            }
        }

    // =========================================================
    // VERSION 5 -> 6
    // ADD SEARCH HISTORY
    // =========================================================

    private val MIGRATION_5_6 =
        object : Migration(5, 6) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS search_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        vehicleNumber TEXT NOT NULL,
                        userEmail TEXT NOT NULL,
                        userName TEXT NOT NULL,
                        agencyId TEXT NOT NULL,
                        searchTime INTEGER NOT NULL,
                        syncPending INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

    // =========================================================
    // VERSION 6 -> 7
    // ADD PENDING IMAGE FILE PATHS
    // =========================================================

    private val MIGRATION_6_7 =
        object : Migration(6, 7) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN inventoryImage1Path TEXT
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN inventoryImage2Path TEXT
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN vehicleImage1Path TEXT
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN vehicleImage2Path TEXT
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN vehicleImage3Path TEXT
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN vehicleImage4Path TEXT
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pending_image_uploads
                    ADD COLUMN vehicleImage5Path TEXT
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
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7
                )
                .build()

            INSTANCE = instance

            instance
        }
    }
}