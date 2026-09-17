package com.example.repomaster.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        history: SearchHistoryEntity
    ): Long

    @Query("""
        SELECT *
        FROM search_history
        WHERE agencyId = :agencyId
        AND syncPending = 1
        ORDER BY searchTime ASC
    """)
    suspend fun getPendingSearchHistory(
        agencyId: String
    ): List<SearchHistoryEntity>

    @Query("""
        UPDATE search_history
        SET syncPending = 0
        WHERE id = :id
    """)
    suspend fun markSynced(
        id: Long
    )

    @Query("""
        DELETE FROM search_history
        WHERE id = :id
    """)
    suspend fun delete(
        id: Long
    )

    @Query("""
        SELECT *
        FROM search_history
        WHERE agencyId = :agencyId
        ORDER BY searchTime DESC
    """)
    suspend fun getAll(
        agencyId: String
    ): List<SearchHistoryEntity>

    @Query("""
        SELECT *
        FROM search_history
        WHERE agencyId = :agencyId
        AND vehicleNumber = :vehicleNumber
        ORDER BY searchTime DESC
    """)
    suspend fun getByVehicle(
        agencyId: String,
        vehicleNumber: String
    ): List<SearchHistoryEntity>
    @Query("""
    SELECT *
    FROM search_history
    WHERE vehicleNumber = :vehicleNumber
    AND userEmail = :userEmail
    AND agencyId = :agencyId
    AND syncPending = 1
    LIMIT 1
""")
    suspend fun getPendingDuplicate(
        vehicleNumber: String,
        userEmail: String,
        agencyId: String
    ): SearchHistoryEntity?
}