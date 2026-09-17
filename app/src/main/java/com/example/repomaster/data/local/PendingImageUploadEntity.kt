package com.example.repomaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_image_uploads")
data class PendingImageUploadEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val vehicleNumber: String,

    val status: String,

    val agencyId: String,

    val uploadStatus: String = "PENDING",

    val userId: String,

    val userEmail: String,

    val createdAt: Long = System.currentTimeMillis(),

    val inventoryImage1Path: String? = null,

    val inventoryImage2Path: String? = null,

    val vehicleImage1Path: String? = null,

    val vehicleImage2Path: String? = null,

    val vehicleImage3Path: String? = null,

    val vehicleImage4Path: String? = null,

    val vehicleImage5Path: String? = null
)