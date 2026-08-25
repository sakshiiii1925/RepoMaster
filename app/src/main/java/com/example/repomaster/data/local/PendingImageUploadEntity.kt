package com.example.repomaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_image_uploads")
data class PendingImageUploadEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val vehicleNumber: String,

    val status: String,

    val uploadStatus: String = "PENDING",

    val createdAt: Long = System.currentTimeMillis()
)