package com.example.repomaster.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        VehicleEntity::class,
        PendingImageUploadEntity::class,
        SearchHistoryEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao

    abstract fun pendingImageUploadDao(): PendingImageUploadDao

    abstract fun searchHistoryDao(): SearchHistoryDao
}