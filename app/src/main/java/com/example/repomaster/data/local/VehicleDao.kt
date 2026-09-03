package com.example.repomaster.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VehicleDao {

    // =========================================================
    // GET VEHICLE BY NUMBER + AGENCY
    // =========================================================

    @Query("""
        SELECT * FROM vehicles
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
        LIMIT 1
    """)
    suspend fun getVehicle(
        vehicleNumber: String,
        agencyId: String
    ): VehicleEntity?


    // =========================================================
    // SEARCH VEHICLES FOR SUGGESTIONS
    // =========================================================

    @Query("""
        SELECT * FROM vehicles
        WHERE vehicleNumber LIKE '%' || :keyword || '%'
        AND agencyId = :agencyId
        ORDER BY vehicleNumber
    """)
    suspend fun searchVehicles(
        keyword: String,
        agencyId: String
    ): List<VehicleEntity>


    // =========================================================
    // INSERT ONE VEHICLE
    // =========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(
        vehicle: VehicleEntity
    )


    // =========================================================
    // INSERT MULTIPLE VEHICLES
    // =========================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(
        vehicles: List<VehicleEntity>
    )


    // =========================================================
    // DELETE ALL LOCAL VEHICLES
    // =========================================================

    @Query("DELETE FROM vehicles")
    suspend fun deleteAll()


    // =========================================================
    // COUNT LOCAL VEHICLES
    // =========================================================

    @Query("""
        SELECT COUNT(*)
        FROM vehicles
    """)
    suspend fun getVehicleCount(): Int


    // =========================================================
    // GET ALL VEHICLES
    // =========================================================

    @Query("""
        SELECT * FROM vehicles
    """)
    suspend fun getAllVehicles(): List<VehicleEntity>


    // =========================================================
    // UPDATE OFFLINE STATUS
    // =========================================================

    @Query("""
        UPDATE vehicles
        SET repoStatus = :status,
            statusSyncPending = 1,
            statusUpdatedOffline = 1
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
    """)
    suspend fun updateStatusOffline(
        vehicleNumber: String,
        status: String,
        agencyId: String
    )


    // =========================================================
    // PENDING STATUS UPDATES
    // =========================================================

    @Query("""
    SELECT * FROM vehicles
    WHERE statusSyncPending = 1
    AND agencyId = :agencyId
""")
    suspend fun getPendingStatusUpdates(
        agencyId: String
    ): List<VehicleEntity>

    // =========================================================
    // MARK STATUS SYNCED
    // =========================================================

    @Query("""
        UPDATE vehicles
        SET statusSyncPending = 0,
            statusUpdatedOffline = 0
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
    """)
    suspend fun markStatusSynced(
        vehicleNumber: String,
        agencyId: String
    )


    // =========================================================
    // UPDATE STATUS FROM SERVER
    // =========================================================

    @Query("""
        UPDATE vehicles
        SET repoStatus = :status,
            statusSyncPending = 0,
            statusUpdatedOffline = 0
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
    """)
    suspend fun updateStatusFromServer(
        vehicleNumber: String,
        status: String,
        agencyId: String
    )


    // =========================================================
    // GET VEHICLE BY NUMBER + AGENCY
    // =========================================================

    @Query("""
        SELECT * FROM vehicles
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
        LIMIT 1
    """)
    suspend fun getVehicleByNumber(
        vehicleNumber: String,
        agencyId: String
    ): VehicleEntity?


    // =========================================================
    // IMAGE UPLOAD PENDING
    // =========================================================

    @Query("""
        UPDATE vehicles
        SET imageUploadPending = 1
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
    """)
    suspend fun markImageUploadPending(
        vehicleNumber: String,
        agencyId: String
    )


    // =========================================================
    // VEHICLES WITH PENDING IMAGE UPLOAD
    // =========================================================

    @Query("""
    SELECT * FROM vehicles
    WHERE imageUploadPending = 1
    AND agencyId = :agencyId
    ORDER BY vehicleNumber
""")
    suspend fun getVehiclesWithPendingImageUpload(
        agencyId: String
    ): List<VehicleEntity>

    // =========================================================
    // IMAGE UPLOAD COMPLETED
    // =========================================================

    @Query("""
        UPDATE vehicles
        SET imageUploadPending = 0
        WHERE vehicleNumber = :vehicleNumber
        AND agencyId = :agencyId
    """)
    suspend fun markImageUploadCompleted(
        vehicleNumber: String,
        agencyId: String
    )
}