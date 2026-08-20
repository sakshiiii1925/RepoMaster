package com.example.repomaster.data.local



import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [VehicleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
}