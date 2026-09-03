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
        AND uploadStatus = 'PENDING'
        ORDER BY createdAt DESC
    """)
    suspend fun getPendingUploads(
        agencyId: String
    ): List<PendingImageUploadEntity>

    @Query("""
        SELECT *
        FROM pending_image_uploads
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
        AND uploadStatus = 'PENDING'
        LIMIT 1
    """)
    suspend fun getPendingForVehicle(
        vehicleNumber: String,
        agencyId: String
    ): PendingImageUploadEntity?

    @Query("""
        UPDATE pending_image_uploads
        SET uploadStatus = 'UPLOADED'
        WHERE id = :id
        AND agencyId = :agencyId
    """)
    suspend fun markUploaded(
        id: Int,
        agencyId: String
    )

    @Query("""
        DELETE FROM pending_image_uploads
        WHERE id = :id
        AND agencyId = :agencyId
    """)
    suspend fun delete(
        id: Int,
        agencyId: String
    )

    @Query("""
        UPDATE pending_image_uploads
        SET uploadStatus = 'UPLOADED'
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
        AND uploadStatus = 'PENDING'
    """)
    suspend fun markUploadedByVehicle(
        vehicleNumber: String,
        agencyId: String
    )
}