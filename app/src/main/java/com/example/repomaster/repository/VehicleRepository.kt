package com.example.repomaster.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

import com.example.repomaster.data.local.DatabaseProvider
import com.example.repomaster.data.local.toEntity
import com.example.repomaster.data.local.toVehicle
import com.example.repomaster.models.StatusUpdateRequest
import com.example.repomaster.models.Vehicle
import com.example.repomaster.models.UploadResponse
import com.example.repomaster.network.RetrofitClient

import okhttp3.MultipartBody
import retrofit2.Response


class VehicleRepository(
    private val context: Context
) {

    private val api = RetrofitClient.api

    private val vehicleDao =
        DatabaseProvider
            .getDatabase(context)
            .vehicleDao()


    // =========================================================
    // SEARCH VEHICLE - ONLINE + OFFLINE
    // =========================================================

    suspend fun searchVehicle(
        vehicleNumber: String
    ): Vehicle? {

        val number = vehicleNumber
            .trim()
            .replace("-", "")
            .replace("/", "")
            .replace(".", "")
            .replace(" ", "")
            .uppercase()

        Log.d("VEHICLE_SEARCH", "Searching: $number")

        // -----------------------------------------------------
        // 1. CHECK INTERNET
        // -----------------------------------------------------

        if (isNetworkAvailable()) {

            try {

                Log.d(
                    "VEHICLE_SEARCH",
                    "Internet available - searching API"
                )

                val response =
                    api.getVehicle(number)

                Log.d(
                    "VEHICLE_SEARCH",
                    "API response: ${response.code()}"
                )

                if (response.isSuccessful) {

                    val vehicle = response.body()

                    if (vehicle != null) {

                        // SAVE VEHICLE LOCALLY
                        vehicleDao.insertVehicle(
                            vehicle.toEntity()
                        )

                        Log.d(
                            "VEHICLE_SEARCH",
                            "Vehicle saved to Room"
                        )

                        return vehicle
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    "VEHICLE_SEARCH",
                    "API failed, trying Room",
                    e
                )
            }
        }

        // -----------------------------------------------------
        // 2. OFFLINE OR API FAILED
        // -----------------------------------------------------

        Log.d(
            "VEHICLE_SEARCH",
            "Searching Room database"
        )

        val localVehicle =
            vehicleDao.getVehicle(number)

        if (localVehicle != null) {

            Log.d(
                "VEHICLE_SEARCH",
                "Vehicle found offline"
            )

            return localVehicle.toVehicle()
        }

        Log.d(
            "VEHICLE_SEARCH",
            "Vehicle not found offline"
        )

        return null
    }


    // =========================================================
    // INTERNET CHECK
    // =========================================================




    private fun isNetworkAvailable(): Boolean {

        val connectivityManager =
            context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

        val network =
            connectivityManager.activeNetwork
                ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(
                network
            )
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    suspend fun updateStatus(
        vehicleNumber: String,
        status: String
    ): StatusSaveResult {

        val number = vehicleNumber
            .trim()
            .replace("-", "")
            .replace("/", "")
            .replace(".", "")
            .replace(" ", "")
            .uppercase()

        return try {

            // ============================================
            // 1. ALWAYS SAVE TO ROOM FIRST
            // ============================================

            vehicleDao.updateStatusOffline(
                number,
                status
            )

            Log.d(
                "STATUS_UPDATE",
                "Status saved locally: $number -> $status"
            )

            // ============================================
            // 2. NO INTERNET
            // ============================================

            if (!isNetworkAvailable()) {

                Log.d(
                    "STATUS_UPDATE",
                    "Offline - status marked pending"
                )

                return StatusSaveResult.SAVED_OFFLINE
            }

            // ============================================
            // 3. INTERNET AVAILABLE → TRY API
            // ============================================

            try {

                val response =
                    api.updateStatus(
                        number,
                        StatusUpdateRequest(status)
                    )

                if (response.isSuccessful) {

                    vehicleDao.markStatusSynced(number)

                    Log.d(
                        "STATUS_UPDATE",
                        "Status synced successfully"
                    )

                    StatusSaveResult.SAVED_AND_SYNCED

                } else {

                    Log.e(
                        "STATUS_UPDATE",
                        "API failed: ${response.code()}"
                    )

                    // Local data is still safe
                    StatusSaveResult.SAVED_OFFLINE
                }

            } catch (e: Exception) {

                Log.e(
                    "STATUS_UPDATE",
                    "API failed, keeping local status",
                    e
                )

                // Local save succeeded
                StatusSaveResult.SAVED_OFFLINE
            }

        } catch (e: Exception) {

            Log.e(
                "STATUS_UPDATE",
                "Local Room save failed",
                e
            )

            StatusSaveResult.FAILED
        }
    }
    suspend fun syncPendingStatuses(): Boolean {

        if (!isNetworkAvailable()) {
            return false
        }

        val pendingVehicles =
            vehicleDao.getPendingStatusUpdates()

        if (pendingVehicles.isEmpty()) {
            Log.d(
                "STATUS_SYNC",
                "No pending status updates"
            )

            return true
        }

        var allSuccessful = true

        for (vehicle in pendingVehicles) {

            val status =
                vehicle.repoStatus ?: continue

            try {

                Log.d(
                    "STATUS_SYNC",
                    "Syncing ${vehicle.vehicleNumber} -> $status"
                )

                val response =
                    api.updateStatus(
                        vehicle.vehicleNumber,
                        StatusUpdateRequest(status)
                    )

                if (response.isSuccessful) {

                    vehicleDao.markStatusSynced(
                        vehicle.vehicleNumber
                    )

                    Log.d(
                        "STATUS_SYNC",
                        "Synced: ${vehicle.vehicleNumber}"
                    )

                } else {

                    allSuccessful = false

                    Log.e(
                        "STATUS_SYNC",
                        "Failed ${vehicle.vehicleNumber}: ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                allSuccessful = false

                Log.e(
                    "STATUS_SYNC",
                    "Sync error: ${vehicle.vehicleNumber}",
                    e
                )
            }
        }

        return allSuccessful
    }


    // =========================================================
    // ADD VEHICLE
    // =========================================================

    suspend fun addVehicle(
        vehicle: Vehicle
    ): Response<Vehicle> {

        return api.addVehicle(vehicle)
    }


    // =========================================================
    // GET ALL VEHICLES
    // =========================================================

    suspend fun getVehicle(
        vehicleNumber: String
    ): Vehicle? {

        val number = vehicleNumber
            .trim()
            .replace("-", "")
            .replace("/", "")
            .replace(".", "")
            .replace(" ", "")
            .uppercase()

        Log.d(
            "VEHICLE_GET",
            "Searching vehicle: $number"
        )

        // =====================================================
        // 1. ONLINE SEARCH
        // =====================================================

        if (isNetworkAvailable()) {

            try {

                Log.d(
                    "VEHICLE_GET",
                    "Internet available - calling API"
                )

                val response =
                    api.getVehicle(number)

                Log.d(
                    "VEHICLE_GET",
                    "API response: ${response.code()}"
                )

                if (response.isSuccessful) {

                    val vehicle =
                        response.body()

                    if (vehicle != null) {

                        // Save/update vehicle in Room
                        vehicleDao.insertVehicle(
                            vehicle.toEntity()
                        )

                        Log.d(
                            "VEHICLE_GET",
                            "Vehicle saved to Room"
                        )

                        return vehicle
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    "VEHICLE_GET",
                    "API failed - checking Room",
                    e
                )
            }
        }

        // =====================================================
        // 2. OFFLINE / API FAILED → ROOM
        // =====================================================

        Log.d(
            "VEHICLE_GET",
            "Searching vehicle in Room"
        )

        val localVehicle =
            vehicleDao.getVehicle(number)

        if (localVehicle != null) {

            Log.d(
                "VEHICLE_GET",
                "Vehicle found in Room"
            )

            return localVehicle.toVehicle()
        }

        Log.d(
            "VEHICLE_GET",
            "Vehicle not found in Room"
        )

        return null
    }


    // =========================================================
    // DELETE VEHICLE
    // =========================================================

    suspend fun deleteVehicle(
        vehicleNumber: String
    ): Boolean {

        val response =
            api.deleteVehicle(vehicleNumber)

        return response.isSuccessful
    }


    // =========================================================
    // EXCEL UPLOAD
    // =========================================================

    suspend fun uploadExcel(
        file: MultipartBody.Part,
        agencyId: String
    ): Response<UploadResponse> {

        return api.uploadExcel(
            agencyId,
            file
        )
    }


    // =========================================================
    // SEARCH VEHICLES / SUGGESTIONS
    // =========================================================

    suspend fun searchVehicles(
        keyword: String
    ): Response<List<Vehicle>> {

        val searchKeyword = keyword
            .trim()
            .replace("-", "")
            .replace("/", "")
            .replace(".", "")
            .replace(" ", "")
            .uppercase()

        Log.d(
            "VEHICLE_SUGGESTION",
            "Searching suggestions: $searchKeyword"
        )

        // =====================================================
        // 1. SEARCH ROOM FIRST
        // =====================================================

        try {

            val localVehicles =
                vehicleDao.searchVehicles(searchKeyword)

            if (localVehicles.isNotEmpty()) {

                Log.d(
                    "VEHICLE_SUGGESTION",
                    "Found ${localVehicles.size} vehicles in Room"
                )

                return Response.success(
                    localVehicles.map {
                        it.toVehicle()
                    }
                )
            }

        } catch (e: Exception) {

            Log.e(
                "VEHICLE_SUGGESTION",
                "Room search failed",
                e
            )
        }

        // =====================================================
        // 2. IF ROOM HAS NOTHING → API
        // =====================================================

        if (isNetworkAvailable()) {

            try {

                Log.d(
                    "VEHICLE_SUGGESTION",
                    "Searching API"
                )

                val response =
                    api.searchVehicles(searchKeyword)

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    val vehicles =
                        response.body()!!

                    // Save API results into Room
                    try {

                        vehicleDao.insertVehicles(
                            vehicles.map {
                                it.toEntity()
                            }
                        )

                        Log.d(
                            "VEHICLE_SUGGESTION",
                            "Suggestions saved to Room"
                        )

                    } catch (e: Exception) {

                        Log.e(
                            "VEHICLE_SUGGESTION",
                            "Failed to save suggestions to Room",
                            e
                        )
                    }

                    return response
                }

            } catch (e: Exception) {

                Log.e(
                    "VEHICLE_SUGGESTION",
                    "API suggestion search failed",
                    e
                )
            }
        }

        // =====================================================
        // 3. OFFLINE + NOTHING IN ROOM
        // =====================================================

        Log.d(
            "VEHICLE_SUGGESTION",
            "No vehicles found"
        )

        return Response.success(
            emptyList()
        )
    }

    // =========================================================
    // SEARCH HISTORY
    // =========================================================

    suspend fun saveSearchHistory(
        vehicleNumber: String,
        userEmail: String,
        userName: String,
        agencyId: String
    ) =
        api.saveSearchHistory(
            vehicleNumber,
            userEmail,
            userName,
            agencyId
        )


    suspend fun getSearchHistory(
        userEmail: String
    ) =
        api.getSearchHistory(userEmail)


    suspend fun getAllSearchHistory() =
        api.getAllSearchHistory()


    suspend fun searchHistoryByVehicle(
        agencyId: String,
        vehicleNumber: String
    ) =
        api.searchHistoryByVehicle(
            agencyId,
            vehicleNumber
        )


    suspend fun sortSearchHistory(
        agencyId: String,
        order: String
    ) =
        api.sortSearchHistory(
            agencyId,
            order
        )


    suspend fun filterByUser(
        agencyId: String,
        userName: String
    ) =
        api.filterByUser(
            agencyId,
            userName
        )


    suspend fun filterByDate(
        agencyId: String,
        date: String
    ) =
        api.filterByDate(
            agencyId,
            date
        )
    suspend fun syncAllVehicles(
        agencyId: String
    ): Boolean {

        if (!isNetworkAvailable()) {
            Log.d(
                "VEHICLE_SYNC",
                "No internet - sync skipped"
            )

            return false
        }

        return try {

            Log.d(
                "VEHICLE_SYNC",
                "Downloading all vehicles..."
            )

            val response =
                api.getAllVehicles(agencyId)

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val vehicles =
                    response.body()!!

                Log.d(
                    "VEHICLE_SYNC",
                    "Vehicles received: ${vehicles.size}"
                )

                val entities =
                    vehicles.map { vehicle ->
                        vehicle.toEntity()
                    }

                vehicleDao.insertVehicles(
                    entities
                )

                Log.d(
                    "VEHICLE_SYNC",
                    "All vehicles saved to Room"
                )

                true

            } else {

                Log.e(
                    "VEHICLE_SYNC",
                    "API failed: ${response.code()}"
                )

                false
            }

        } catch (e: Exception) {

            Log.e(
                "VEHICLE_SYNC",
                "Synchronization failed",
                e
            )

            false
        }
    }
    suspend fun searchVehicleOffline(
        vehicleNumber: String
    ): Vehicle? {

        val number =
            vehicleNumber
                .trim()
                .replace("-", "")
                .replace("/", "")
                .replace(".", "")
                .replace(" ", "")
                .uppercase()

        val entity =
            vehicleDao.getVehicle(number)

        return entity?.toVehicle()
    }
    suspend fun getAllVehicles(
        agencyId: String
    ): List<Vehicle> {

        return try {

            val response =
                api.getAllVehicles(agencyId)

            if (response.isSuccessful && response.body() != null) {

                response.body()!!

            } else {

                emptyList()
            }

        } catch (e: Exception) {

            Log.e(
                "GET_ALL_VEHICLES",
                "Failed to get vehicles",
                e
            )

            emptyList()
        }
    }
}