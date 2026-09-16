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

    // =========================================================
    // GET CURRENT USER'S PENDING UPLOADS
    // =========================================================

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


    // =========================================================
    // GET PENDING UPLOAD FOR VEHICLE
    // CURRENT USER ONLY
    // =========================================================

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


    // =========================================================
    // MARK UPLOAD AS UPLOADED
    // CURRENT USER ONLY
    // =========================================================

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


    // =========================================================
    // DELETE PENDING UPLOAD
    // CURRENT USER ONLY
    // =========================================================

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


    // =========================================================
    // MARK VEHICLE UPLOAD COMPLETED
    // CURRENT USER ONLY
    // =========================================================

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