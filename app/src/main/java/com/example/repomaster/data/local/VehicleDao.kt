package com.example.repomaster.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VehicleDao {

    // Search exact vehicle number
    @Query("""
        SELECT * FROM vehicles
        WHERE vehicleNumber = :vehicleNumber
        LIMIT 1
    """)
    suspend fun getVehicle(
        vehicleNumber: String
    ): VehicleEntity?

    // Search vehicles for suggestions
    @Query("""
        SELECT * FROM vehicles
        WHERE vehicleNumber LIKE '%' || :keyword || '%'
        ORDER BY vehicleNumber
    """)
    suspend fun searchVehicles(
        keyword: String
    ): List<VehicleEntity>

    // Save/update one vehicle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(
        vehicle: VehicleEntity
    )

    // Save/update multiple vehicles
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(
        vehicles: List<VehicleEntity>
    )

    // Delete all local vehicles
    @Query("DELETE FROM vehicles")
    suspend fun deleteAll()

    // Count local vehicles
    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun getVehicleCount(): Int
    @Query("SELECT * FROM vehicles")
    suspend fun getAllVehicles(): List<VehicleEntity>
    @Query("""
    UPDATE vehicles
    SET repoStatus = :status,
        statusSyncPending = 1,
        statusUpdatedOffline = 1
    WHERE vehicleNumber = :vehicleNumber
""")
    suspend fun updateStatusOffline(
        vehicleNumber: String,
        status: String
    )
    @Query("""
    SELECT * FROM vehicles
    WHERE statusSyncPending = 1
""")
    suspend fun getPendingStatusUpdates(): List<VehicleEntity>
    @Query("""
    UPDATE vehicles
    SET statusSyncPending = 0,
        statusUpdatedOffline = 0
    WHERE vehicleNumber = :vehicleNumber
""")
    suspend fun markStatusSynced(
        vehicleNumber: String
    )
    @Query("""
    UPDATE vehicles
    SET repoStatus = :status,
        statusSyncPending = 0,
        statusUpdatedOffline = 0
    WHERE vehicleNumber = :vehicleNumber
""")
    suspend fun updateStatusFromServer(
        vehicleNumber: String,
        status: String
    )
    @Query("""
    SELECT * FROM vehicles
    WHERE vehicleNumber = :vehicleNumber
    LIMIT 1
""")
    suspend fun getVehicleByNumber(
        vehicleNumber: String
    ): VehicleEntity?
    @Query("""
    UPDATE vehicles
    SET imageUploadPending = 1
    WHERE vehicleNumber = :vehicleNumber
""")
    suspend fun markImageUploadPending(
        vehicleNumber: String
    )
    @Query("""
    SELECT * FROM vehicles
    WHERE imageUploadPending = 1
    ORDER BY vehicleNumber
""")
    suspend fun getPendingImageUploads(): List<VehicleEntity>
    @Query("""
    UPDATE vehicles
    SET imageUploadPending = 0
    WHERE vehicleNumber = :vehicleNumber
""")
    suspend fun markImageUploadCompleted(
        vehicleNumber: String
    )
}