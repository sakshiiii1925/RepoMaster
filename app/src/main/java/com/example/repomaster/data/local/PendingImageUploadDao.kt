package com.example.repomaster.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PendingImageUploadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        item: PendingImageUploadEntity
    )

    @Query("""
        SELECT *
        FROM pending_image_uploads
        WHERE agencyId = :agencyId
        AND userEmail = :userEmail
        AND uploadStatus = 'PENDING'
        ORDER BY createdAt DESC
    """)
    suspend fun getPendingUploads(
        agencyId: String,
        userEmail: String
    ): List<PendingImageUploadEntity>

    @Query("""
        SELECT *
        FROM pending_image_uploads
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
        AND userEmail = :userEmail
        AND uploadStatus = 'PENDING'
        LIMIT 1
    """)
    suspend fun getPendingForVehicle(
        vehicleNumber: String,
        agencyId: String,
        userEmail: String
    ): PendingImageUploadEntity?

    @Query("""
        UPDATE pending_image_uploads
        SET
            inventoryImage1Path = :inventoryImage1Path,
            inventoryImage2Path = :inventoryImage2Path,
            vehicleImage1Path = :vehicleImage1Path,
            vehicleImage2Path = :vehicleImage2Path,
            vehicleImage3Path = :vehicleImage3Path,
            vehicleImage4Path = :vehicleImage4Path,
            vehicleImage5Path = :vehicleImage5Path,
            uploadStatus = 'PENDING'
        WHERE id = :id
        AND agencyId = :agencyId
        AND userEmail = :userEmail
    """)
    suspend fun updateImagePaths(
        id: Int,
        agencyId: String,
        userEmail: String,
        inventoryImage1Path: String,
        inventoryImage2Path: String,
        vehicleImage1Path: String,
        vehicleImage2Path: String,
        vehicleImage3Path: String,
        vehicleImage4Path: String,
        vehicleImage5Path: String
    )

    @Query("""
        UPDATE pending_image_uploads
        SET uploadStatus = 'UPLOADED'
        WHERE id = :id
        AND agencyId = :agencyId
        AND userEmail = :userEmail
    """)
    suspend fun markUploaded(
        id: Int,
        agencyId: String,
        userEmail: String
    )

    @Query("""
        DELETE FROM pending_image_uploads
        WHERE id = :id
        AND agencyId = :agencyId
        AND userEmail = :userEmail
    """)
    suspend fun delete(
        id: Int,
        agencyId: String,
        userEmail: String
    )

    @Query("""
        UPDATE pending_image_uploads
        SET uploadStatus = 'UPLOADED'
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
        AND userEmail = :userEmail
        AND uploadStatus = 'PENDING'
    """)
    suspend fun markUploadedByVehicle(
        vehicleNumber: String,
        agencyId: String,
        userEmail: String
    )
}