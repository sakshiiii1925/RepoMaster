package com.example.repomaster.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.repomaster.models.UploadedImage
import com.example.repomaster.data.local.DatabaseProvider
import com.example.repomaster.data.local.toEntity
import com.example.repomaster.data.local.toVehicle
import com.example.repomaster.models.StatusUpdateRequest
import com.example.repomaster.models.Vehicle
import com.example.repomaster.models.UploadResponse
import com.example.repomaster.network.RetrofitClient
import com.example.repomaster.data.local.PendingImageUploadEntity
import okhttp3.MultipartBody
import retrofit2.Response
import com.example.repomaster.models.UploadedImageDetails
import com.example.repomaster.utils.SessionManager

class VehicleRepository(
    private val context: Context
) {

    private val api = RetrofitClient.api
    private val sessionManager =
        SessionManager(context)
    private val vehicleDao =
        DatabaseProvider
            .getDatabase(context)
            .vehicleDao()
    private val pendingImageUploadDao =
        DatabaseProvider
            .getDatabase(context)
            .pendingImageUploadDao()

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

                val userId =
                    sessionManager.getUserId()

                if (userId <= 0) {

                    Log.e(
                        "VEHICLE_SEARCH",
                        "User not logged in"
                    )

                    return null
                }

                Log.d(
                    "VEHICLE_SEARCH",
                    "Searching API for userId=$userId"
                )

                val response =
                    api.getVehicle(
                        number,
                        userId
                    )

                if (response.isSuccessful) {

                    val vehicle =
                        response.body()

                    if (vehicle != null) {

                        vehicleDao.insertVehicle(
                            vehicle.toEntity()
                        )

                        return vehicle
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    "VEHICLE_SEARCH",
                    "API failed",
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

        val agencyId =
            sessionManager.getAgencyId()

        if (agencyId.isBlank()) {
            return null
        }

        val localVehicle =
            vehicleDao.getVehicle(
                number,
                agencyId
            )

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
            // 1. ALWAYS SAVE STATUS TO ROOM FIRST
            // ============================================

            val agencyId = getCurrentAgencyId()

            if (agencyId.isBlank()) {
                Log.e(
                    "STATUS_UPDATE",
                    "Agency ID not found"
                )
                return StatusSaveResult.FAILED
            }

            vehicleDao.updateStatusOffline(
                number,
                status,
                agencyId
            )

            Log.d(
                "STATUS_UPDATE",
                "Status saved locally: $number -> $status"
            )

            // ============================================
            // 2. CHECK INTERNET
            // ============================================

            if (!isNetworkAvailable()) {

                Log.d(
                    "STATUS_UPDATE",
                    "Offline - status marked pending"
                )

                // ----------------------------------------
                // Repo Mark / Parked
                // Create pending image upload
                // ----------------------------------------

                if (
                    status == "repo mark" ||
                    status == "Parked"
                ) {

                    val existing =
                        pendingImageUploadDao
                            .getPendingForVehicle(
                                number,
                                agencyId
                            )
                    if (existing == null) {

                        pendingImageUploadDao.insert(
                            PendingImageUploadEntity(
                                vehicleNumber = number,
                                status = status,
                                uploadStatus = "PENDING",
                                agencyId = agencyId
                            )
                        )

                        Log.d(
                            "IMAGE_UPLOAD",
                            "Pending image upload created: $number"
                        )

                    } else {

                        Log.d(
                            "IMAGE_UPLOAD",
                            "Pending image upload already exists: $number"
                        )
                    }
                }

                return StatusSaveResult.SAVED_OFFLINE
            }

            // ============================================
            // 3. INTERNET AVAILABLE → API
            // ============================================

            try {

                val userId =
                    sessionManager.getUserId()

                if (userId <= 0) {

                    Log.e(
                        "STATUS_UPDATE",
                        "Invalid userId: $userId"
                    )

                    return StatusSaveResult.FAILED
                }

                val response =
                    api.updateStatus(
                        number,
                        userId,
                        StatusUpdateRequest(status)
                    )

                if (response.isSuccessful) {

                    vehicleDao.markStatusSynced(
                        number,
                        agencyId
                    )

                    Log.d(
                        "STATUS_UPDATE",
                        "Status synced successfully"
                    )

                    return StatusSaveResult.SAVED_AND_SYNCED

                } else {

                    Log.e(
                        "STATUS_UPDATE",
                        "API failed: ${response.code()}"
                    )

                    // ------------------------------------
                    // API failed although internet exists
                    // Keep status pending.
                    //
                    // Also create pending image record
                    // for Repo Mark / Parked.
                    // ------------------------------------

                    if (
                        status == "repo mark" ||
                        status == "Parked"
                    ) {

                        val existing =
                            pendingImageUploadDao.getPendingForVehicle(
                                number,
                                agencyId
                            )
                        if (existing == null) {

                            pendingImageUploadDao.insert(
                                PendingImageUploadEntity(
                                    vehicleNumber = number,
                                    status = status,
                                    agencyId = agencyId,
                                    uploadStatus = "PENDING"
                                )
                            )
                        }
                    }

                    return StatusSaveResult.SAVED_OFFLINE
                }

            } catch (e: Exception) {

                Log.e(
                    "STATUS_UPDATE",
                    "API failed, keeping local status",
                    e
                )

                // ----------------------------------------
                // API failed
                // Keep status locally.
                // ----------------------------------------

                if (
                    status == "repo mark" ||
                    status == "Parked"
                ) {

                    val existing =
                        pendingImageUploadDao.getPendingForVehicle(
                            number,
                            agencyId
                        )
                    if (existing == null) {

                        pendingImageUploadDao.insert(
                            PendingImageUploadEntity(
                                vehicleNumber = number,
                                status = status,
                                agencyId = agencyId,
                                uploadStatus = "PENDING"
                            )
                        )
                    }
                }

                return StatusSaveResult.SAVED_OFFLINE
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

        val agencyId =
            getCurrentAgencyId()

        if (agencyId.isBlank()) {
            Log.e(
                "STATUS_SYNC",
                "Agency ID not found"
            )
            return false
        }

        val pendingVehicles =

            vehicleDao.getPendingStatusUpdates(agencyId)

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

                val userId =
                    sessionManager.getUserId()

                if (userId <= 0) {

                    Log.e(
                        "STATUS_SYNC",
                        "Invalid userId: $userId"
                    )

                    return false
                }

                val response =
                    api.updateStatus(
                        vehicle.vehicleNumber,
                        userId,
                        StatusUpdateRequest(status)
                    )

                if (response.isSuccessful) {

                    vehicleDao.markStatusSynced(
                        vehicle.vehicleNumber,
                        agencyId
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

                val userId =
                    sessionManager.getUserId()

                if (userId <= 0) {
                    return null
                }

                val response =
                    api.getVehicle(
                        number,
                        userId
                    )
                Log.d(
                    "VEHICLE_GET",
                    "API response: ${response.code()}"
                )

                if (response.isSuccessful) {

                    val vehicle =
                        response.body()

                    if (vehicle != null) {

                        val agencyId = getCurrentAgencyId()

                        if (agencyId.isBlank()) {
                            return null
                        }

                        val localVehicle =
                            vehicleDao.getVehicle(
                                number,
                                agencyId
                            )

                        if (
                            localVehicle != null &&
                            localVehicle.statusSyncPending
                        ) {

                            Log.d(
                                "VEHICLE_GET",
                                "Local pending status found. Preserving local status."
                            )

                            val serverEntity =
                                vehicle.toEntity()

                            val mergedEntity =
                                serverEntity.copy(
                                    repoStatus =
                                        localVehicle.repoStatus,

                                    statusSyncPending =
                                        true,

                                    statusUpdatedOffline =
                                        true
                                )

                            vehicleDao.insertVehicle(
                                mergedEntity
                            )

                            return mergedEntity.toVehicle()

                        } else {

                            vehicleDao.insertVehicle(
                                vehicle.toEntity()
                            )

                            return vehicle
                        }
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

        val agencyId =
            getCurrentAgencyId()

        if (agencyId.isBlank()) {

            Log.e(
                "VEHICLE_GET",
                "Agency ID not found"
            )

            return null
        }

        val localVehicle =
            vehicleDao.getVehicle(
                number,
                agencyId
            )

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

            val agencyId =
                getCurrentAgencyId()

            if (agencyId.isBlank()) {

                Log.e(
                    "VEHICLE_SUGGESTION",
                    "Agency ID not found"
                )

                return Response.success(emptyList())
            }

            val localVehicles =
                vehicleDao.searchVehicles(
                    searchKeyword,
                    agencyId
                )

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

                val userId =
                    sessionManager.getUserId()

                if (userId <= 0) {

                    Log.e(
                        "VEHICLE_SUGGESTION",
                        "Invalid userId"
                    )

                    return Response.success(emptyList())
                }

                val response =
                    api.searchVehicles(
                        searchKeyword,
                        userId
                    )

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
    suspend fun syncAllVehicles(): Boolean {

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

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {
                return false
            }

            val response =
                api.getAllVehicles(userId)

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

        val agencyId =
            getCurrentAgencyId()

        if (agencyId.isBlank()) {
            return null
        }

        val entity =
            vehicleDao.getVehicle(
                number,
                agencyId
            )

        return entity?.toVehicle()
    }
    suspend fun getAllVehicles(): List<Vehicle> {

        return try {

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {
                return emptyList()
            }

            val response =
                api.getAllVehicles(userId)

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                response.body()!!

            } else {

                emptyList()
            }

        } catch (e: Exception) {

            Log.e(
                "GET_ALL_VEHICLES",
                "Failed",
                e
            )

            emptyList()
        }
    }
    suspend fun markImageUploadPending(
        vehicleNumber: String,
        status: String
    ) {

        val number =
            vehicleNumber
                .trim()
                .replace("-", "")
                .replace("/", "")
                .replace(".", "")
                .replace(" ", "")
                .uppercase()

        val agencyId =
            getCurrentAgencyId()

        if (agencyId.isBlank()) {
            Log.e(
                "IMAGE_PENDING",
                "Agency ID not found"
            )
            return
        }

        val existing =
            pendingImageUploadDao.getPendingForVehicle(
                number,
                agencyId
            )

        if (existing == null) {

            pendingImageUploadDao.insert(
                PendingImageUploadEntity(
                    vehicleNumber = number,
                    status = status,
                    uploadStatus = "PENDING",
                    agencyId = agencyId
                )
            )

            Log.d(
                "IMAGE_PENDING",
                "Image upload added: $number"
            )

        } else {

            Log.d(
                "IMAGE_PENDING",
                "Image upload already pending: $number"
            )
        }
    }



    suspend fun getPendingImageUploads(): List<PendingImageUploadEntity> {

        val agencyId = getCurrentAgencyId()

        if (agencyId.isBlank()) {
            Log.e(
                "IMAGE_PENDING",
                "Agency ID not found"
            )
            return emptyList()
        }

        return pendingImageUploadDao.getPendingUploads(
            agencyId
        )
    }

    suspend fun markImageUploadCompleted(
        vehicleNumber: String
    ) {

        val number =
            vehicleNumber
                .trim()
                .replace("-", "")
                .replace("/", "")
                .replace(".", "")
                .replace(" ", "")
                .uppercase()

        val agencyId =
            getCurrentAgencyId()

        if (agencyId.isBlank()) {
            Log.e(
                "IMAGE_UPLOAD",
                "Agency ID not found"
            )
            return
        }

        // Mark pending image upload as completed
        pendingImageUploadDao.markUploadedByVehicle(
            number,
            agencyId
        )

        // Mark vehicle image upload completed
        vehicleDao.markImageUploadCompleted(
            number,
            agencyId
        )

        Log.d(
            "IMAGE_UPLOAD",
            "Image upload completed: $number"
        )
    }



    suspend fun getUploadedImages(): List<UploadedImage> {

        val response =
            RetrofitClient.repoImageApi.getUploadedImages()

        if (response.isSuccessful) {

            return response.body()?.data
                ?: emptyList()

        } else {

            throw Exception(
                "Failed to load uploaded images: ${response.code()}"
            )
        }
    }
    suspend fun getUploadedImage(
        id: Int
    ): UploadedImageDetails? {

        val response =
            RetrofitClient.repoImageApi.getUploadedImage(id)

        if (response.isSuccessful) {

            return response.body()?.data

        } else {

            throw Exception(
                "Failed to load uploaded image details: ${response.code()}"
            )
        }
    }
    suspend fun deleteUploadedImage(
        id: Int
    ): Boolean {

        val response =
            RetrofitClient.repoImageApi.deleteUploadedImage(id)

        if (response.isSuccessful) {

            return response.body()?.success == true

        } else {

            val error =
                response.errorBody()?.string()

            throw Exception(
                "Delete failed (${response.code()}): $error"
            )
        }
    }

    suspend fun removePendingImageUpload(
        vehicleNumber: String
    ) {

        val number =
            vehicleNumber
                .trim()
                .replace("-", "")
                .replace("/", "")
                .replace(".", "")
                .replace(" ", "")
                .uppercase()

        val agencyId =
            getCurrentAgencyId()

        if (agencyId.isBlank()) {
            Log.e(
                "IMAGE_PENDING",
                "Agency ID not found"
            )
            return
        }

        val pending =
            pendingImageUploadDao.getPendingForVehicle(
                number,
                agencyId
            )

        if (pending != null) {

            pendingImageUploadDao.delete(
                pending.id,
                agencyId
            )

            Log.d(
                "IMAGE_PENDING",
                "Pending image removed: $number"
            )
        }
    }


    private fun getCurrentAgencyId(): String {
        return sessionManager.getAgencyId().trim()
    }
}