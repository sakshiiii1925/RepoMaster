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
        WHERE uploadStatus = 'PENDING'
        ORDER BY createdAt DESC
    """)
    suspend fun getPendingUploads(): List<PendingImageUploadEntity>

    @Query("""
        SELECT *
        FROM pending_image_uploads
        WHERE vehicleNumber = :vehicleNumber
        AND uploadStatus = 'PENDING'
        LIMIT 1
    """)
    suspend fun getPendingForVehicle(
        vehicleNumber: String
    ): PendingImageUploadEntity?

    @Query("""
        UPDATE pending_image_uploads
        SET uploadStatus = 'UPLOADED'
        WHERE id = :id
    """)
    suspend fun markUploaded(
        id: Int
    )

    @Query("""
        DELETE FROM pending_image_uploads
        WHERE id = :id
    """)
    suspend fun delete(
        id: Int
    )
}