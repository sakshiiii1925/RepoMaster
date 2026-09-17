package com.example.repomaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val vehicleNumber: String,

    val userEmail: String,

    val userName: String,

    val agencyId: String,

    val searchTime: Long = System.currentTimeMillis(),

    val syncPending: Boolean = true
)