package com.example.repomaster.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.repomaster.data.local.SearchHistoryEntity
import com.example.repomaster.models.BulkDeleteSearchHistoryRequest
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.example.repomaster.models.UploadedImageDetails
import com.example.repomaster.utils.SessionManager

class VehicleRepository(
    private val context: Context
) {
    private val searchHistorySyncMutex =
        Mutex()
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
    private val searchHistoryDao =
        DatabaseProvider.getDatabase(context).searchHistoryDao()

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

                    val userId =
                        sessionManager.getUserId()

                    val userEmail =
                        sessionManager.getUserEmail()

                    val existing =
                        pendingImageUploadDao.getPendingForVehicle(
                            vehicleNumber = number,
                            agencyId = agencyId,
                            userEmail = userEmail
                        )
                    if (existing == null) {

                        pendingImageUploadDao.insert(

                            PendingImageUploadEntity(

                                vehicleNumber = number,

                                status = status,

                                uploadStatus = "PENDING",

                                agencyId = agencyId,

                                userId = userId.toString(),

                                userEmail = userEmail
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

                        val userId =
                            sessionManager.getUserId()

                        val userEmail =
                            sessionManager.getUserEmail()

                        val existing =
                            pendingImageUploadDao.getPendingForVehicle(
                                vehicleNumber = number,
                                agencyId = agencyId,
                                userEmail = userEmail
                            )
                        if (existing == null) {

                            pendingImageUploadDao.insert(

                                PendingImageUploadEntity(

                                    vehicleNumber = number,

                                    status = status,

                                    uploadStatus = "PENDING",

                                    agencyId = agencyId,

                                    userId = userId.toString(),

                                    userEmail = userEmail
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

                    val userId =
                        sessionManager.getUserId()

                    val userEmail =
                        sessionManager.getUserEmail()

                    val existing =
                        pendingImageUploadDao.getPendingForVehicle(
                            vehicleNumber = number,
                            agencyId = agencyId,
                            userEmail = userEmail
                        )
                    if (existing == null) {

                        pendingImageUploadDao.insert(

                            PendingImageUploadEntity(

                                vehicleNumber = number,

                                status = status,

                                uploadStatus = "PENDING",

                                agencyId = agencyId,

                                userId = userId.toString(),

                                userEmail = userEmail
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
    suspend fun syncPendingSearchHistory(): Boolean {

        return searchHistorySyncMutex.withLock {

            Log.d(
                "SEARCH_HISTORY_SYNC",
                "========================================"
            )

            Log.d(
                "SEARCH_HISTORY_SYNC",
                "SYNC STARTED"
            )

            if (!isNetworkAvailable()) {

                Log.d(
                    "SEARCH_HISTORY_SYNC",
                    "No internet - sync skipped"
                )

                return@withLock false
            }

            val currentAgencyId =
                sessionManager
                    .getAgencyId()
                    .trim()

            if (currentAgencyId.isBlank()) {

                Log.e(
                    "SEARCH_HISTORY_SYNC",
                    "Agency ID is empty"
                )

                return@withLock false
            }

            val pending =
                searchHistoryDao.getPendingSearchHistory(
                    agencyId = currentAgencyId
                )

            Log.d(
                "SEARCH_HISTORY_SYNC",
                "Pending search count = ${pending.size}"
            )

            if (pending.isEmpty()) {

                Log.d(
                    "SEARCH_HISTORY_SYNC",
                    "Nothing to sync"
                )

                return@withLock true
            }

            var allSuccessful = true

            for (history in pending) {

                try {

                    Log.d(
                        "SEARCH_HISTORY_SYNC",
                        "----------------------------------------"
                    )

                    Log.d(
                        "SEARCH_HISTORY_SYNC",
                        "ID = ${history.id}"
                    )

                    Log.d(
                        "SEARCH_HISTORY_SYNC",
                        "Vehicle = ${history.vehicleNumber}"
                    )

                    Log.d(
                        "SEARCH_HISTORY_SYNC",
                        "User = ${history.userEmail}"
                    )

                    Log.d(
                        "SEARCH_HISTORY_SYNC",
                        "Agency = ${history.agencyId}"
                    )

                    val response =
                        api.saveSearchHistory(
                            vehicleNumber =
                                history.vehicleNumber,

                            userEmail =
                                history.userEmail,

                            userName =
                                history.userName,

                            agencyId =
                                history.agencyId
                        )

                    Log.d(
                        "SEARCH_HISTORY_SYNC",
                        "HTTP = ${response.code()}"
                    )

                    if (response.isSuccessful) {

                        Log.d(
                            "SEARCH_HISTORY_SYNC",
                            "SUCCESS = ${history.vehicleNumber}"
                        )

                        searchHistoryDao.markSynced(
                            history.id
                        )

                    } else {

                        allSuccessful = false

                        val errorBody =
                            response
                                .errorBody()
                                ?.string()

                        Log.e(
                            "SEARCH_HISTORY_SYNC",
                            "FAILED = ${history.vehicleNumber}"
                        )

                        Log.e(
                            "SEARCH_HISTORY_SYNC",
                            "HTTP = ${response.code()}"
                        )

                        Log.e(
                            "SEARCH_HISTORY_SYNC",
                            "ERROR = $errorBody"
                        )
                    }

                } catch (e: Exception) {

                    allSuccessful = false

                    Log.e(
                        "SEARCH_HISTORY_SYNC",
                        "Exception syncing ${history.vehicleNumber}",
                        e
                    )
                }
            }

            Log.d(
                "SEARCH_HISTORY_SYNC",
                "SYNC FINISHED = $allSuccessful"
            )

            Log.d(
                "SEARCH_HISTORY_SYNC",
                "========================================"
            )

            return@withLock allSuccessful
        }
    }






    suspend fun syncPendingStatuses(): Boolean {

        // =========================================================
        // 1. CHECK INTERNET
        // =========================================================

        if (!isNetworkAvailable()) {

            Log.d(
                "STATUS_SYNC",
                "No internet - pending status sync skipped"
            )

            return false
        }

        // =========================================================
        // 2. GET CURRENT AGENCY
        // =========================================================

        val agencyId =
            getCurrentAgencyId()

        if (agencyId.isBlank()) {

            Log.e(
                "STATUS_SYNC",
                "Agency ID not found"
            )

            return false
        }

        // =========================================================
        // 3. GET PENDING VEHICLES FROM ROOM
        // =========================================================

        val pendingVehicles =
            vehicleDao.getPendingStatusUpdates(
                agencyId
            )

        if (pendingVehicles.isEmpty()) {

            Log.d(
                "STATUS_SYNC",
                "No pending status updates"
            )

            // Nothing to synchronize.
            // It is safe to download server vehicles.
            return true
        }

        Log.d(
            "STATUS_SYNC",
            "Pending vehicles count=${pendingVehicles.size}"
        )

        var allSuccessful = true

        // =========================================================
        // 4. SYNC EACH PENDING VEHICLE
        // =========================================================

        for (vehicle in pendingVehicles) {

            val status =
                vehicle.repoStatus?.trim()

            if (status.isNullOrBlank()) {

                Log.w(
                    "STATUS_SYNC",
                    "Skipping ${vehicle.vehicleNumber}: empty status"
                )

                continue
            }

            try {

                Log.d(
                    "STATUS_SYNC",
                    "----------------------------------------"
                )

                Log.d(
                    "STATUS_SYNC",
                    "Vehicle=${vehicle.vehicleNumber}"
                )

                Log.d(
                    "STATUS_SYNC",
                    "Status=$status"
                )

                // =================================================
                // IMPORTANT:
                // Use the user associated with this pending record.
                //
                // VehicleEntity should contain the user information
                // for the offline status operation.
                // =================================================

                val userId =
                    sessionManager.getUserId()

                if (userId <= 0) {

                    Log.e(
                        "STATUS_SYNC",
                        "Invalid logged-in userId=$userId"
                    )

                    allSuccessful = false
                    continue
                }

                // =================================================
                // SEND STATUS TO PHP
                // =================================================

                val response =
                    api.updateStatus(
                        vehicle.vehicleNumber,
                        userId,
                        StatusUpdateRequest(status)
                    )

                if (response.isSuccessful) {

                    Log.d(
                        "STATUS_SYNC",
                        "Server status update successful: " +
                                "${vehicle.vehicleNumber} -> $status"
                    )

                    // =================================================
                    // MARK LOCAL STATUS AS SYNCHRONIZED
                    // =================================================

                    vehicleDao.markStatusSynced(
                        vehicle.vehicleNumber,
                        agencyId
                    )

                    Log.d(
                        "STATUS_SYNC",
                        "Room status marked as synced: " +
                                vehicle.vehicleNumber
                    )

                } else {

                    allSuccessful = false

                    Log.e(
                        "STATUS_SYNC",
                        "Server status update failed"
                    )

                    Log.e(
                        "STATUS_SYNC",
                        "Vehicle=${vehicle.vehicleNumber}"
                    )

                    Log.e(
                        "STATUS_SYNC",
                        "HTTP=${response.code()}"
                    )

                    Log.e(
                        "STATUS_SYNC",
                        "Error=${response.errorBody()?.string()}"
                    )
                }

            } catch (e: Exception) {

                allSuccessful = false

                Log.e(
                    "STATUS_SYNC",
                    "Exception while syncing " +
                            vehicle.vehicleNumber,
                    e
                )
            }
        }

        // =========================================================
        // 5. FINAL RESULT
        // =========================================================

        if (allSuccessful) {

            Log.d(
                "STATUS_SYNC",
                "ALL pending statuses synchronized successfully"
            )

        } else {

            Log.w(
                "STATUS_SYNC",
                "Some pending statuses are still waiting for sync"
            )
        }

        return allSuccessful
    }
    private suspend fun saveSearchHistoryLocally(
        vehicleNumber: String
    ) {

        val normalizedNumber =
            vehicleNumber
                .trim()
                .replace("-", "")
                .replace("/", "")
                .replace(".", "")
                .replace(" ", "")
                .uppercase()

        val userEmail =
            sessionManager
                .getUserEmail()
                .trim()

        val userName =
            sessionManager
                .getUserName()
                .trim()

        val agencyId =
            sessionManager
                .getAgencyId()
                .trim()

        if (
            normalizedNumber.isBlank() ||
            userEmail.isBlank() ||
            agencyId.isBlank()
        ) {

            Log.e(
                "SEARCH_HISTORY_LOCAL",
                "Invalid search history data"
            )

            return
        }

        /*
         * Prevent the same unsynced search
         * from being inserted multiple times.
         */
        val existing =
            searchHistoryDao.getPendingDuplicate(
                vehicleNumber = normalizedNumber,
                userEmail = userEmail,
                agencyId = agencyId
            )

        if (existing != null) {

            Log.d(
                "SEARCH_HISTORY_LOCAL",
                "Pending duplicate ignored: $normalizedNumber"
            )

            return
        }

        searchHistoryDao.insert(

            SearchHistoryEntity(

                vehicleNumber =
                    normalizedNumber,

                userEmail =
                    userEmail,

                userName =
                    userName,

                agencyId =
                    agencyId,

                searchTime =
                    System.currentTimeMillis(),

                syncPending =
                    true
            )
        )

        Log.d(
            "SEARCH_HISTORY_LOCAL",
            "Search saved locally: $normalizedNumber"
        )
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

                        val agencyId =
                            getCurrentAgencyId()

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

                            // Record actual search
                            saveSearchHistoryLocally(number)

                            syncPendingSearchHistory()

                            return mergedEntity.toVehicle()

                        } else {

                            vehicleDao.insertVehicle(
                                vehicle.toEntity()
                            )

                            // Record actual search
                            saveSearchHistoryLocally(number)

                            syncPendingSearchHistory()

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

            saveSearchHistoryLocally(number)

            Log.d(
                "VEHICLE_GET",
                "Offline search recorded locally: $number"
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

            val agencyId =
                sessionManager.getAgencyId()

            val role =
                sessionManager.getRole()

            Log.d(
                "GET_ALL_VEHICLES",
                "userId=$userId, agencyId=$agencyId, role=$role"
            )

            if (userId <= 0) {

                Log.e(
                    "GET_ALL_VEHICLES",
                    "Invalid userId=$userId"
                )

                return emptyList()
            }

            val response =
                api.getAllVehicles(userId)

            Log.d(
                "GET_ALL_VEHICLES",
                "HTTP code=${response.code()}"
            )

            Log.d(
                "GET_ALL_VEHICLES",
                "successful=${response.isSuccessful}"
            )

            if (response.isSuccessful) {

                val vehicles =
                    response.body()

                Log.d(
                    "GET_ALL_VEHICLES",
                    "vehicles received=${vehicles?.size ?: 0}"
                )

                vehicles ?: emptyList()

            } else {

                val error =
                    response.errorBody()?.string()

                Log.e(
                    "GET_ALL_VEHICLES",
                    "API error=$error"
                )

                emptyList()
            }

        } catch (e: Exception) {

            Log.e(
                "GET_ALL_VEHICLES",
                "Exception while loading vehicles",
                e
            )

            emptyList()
        }
    }
// =========================================================
// GET TOTAL VEHICLE COUNT
// =========================================================

    suspend fun getVehicleCount(): Int {

        return try {

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {

                Log.e(
                    "VEHICLE_COUNT",
                    "User ID not found: $userId"
                )

                return 0
            }

            Log.d(
                "VEHICLE_COUNT",
                "Loading vehicle count for userId=$userId"
            )

            val response =
                api.getVehicleCount(
                    userId
                )

            Log.d(
                "VEHICLE_COUNT",
                "HTTP code=${response.code()}"
            )

            if (response.isSuccessful) {

                val result =
                    response.body()

                val count =
                    result?.count ?: 0

                Log.d(
                    "VEHICLE_COUNT",
                    "Total vehicles=$count"
                )

                count

            } else {

                val error =
                    response.errorBody()?.string()

                Log.e(
                    "VEHICLE_COUNT",
                    "API error=$error"
                )

                0
            }

        } catch (e: Exception) {

            Log.e(
                "VEHICLE_COUNT",
                "Exception while loading vehicle count",
                e
            )

            0
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

        // =========================================================
        // CURRENT USER
        // =========================================================

        val userId =
            sessionManager.getUserId()

        val userEmail =
            sessionManager.getUserEmail()

        if (userId <= 0) {

            Log.e(
                "IMAGE_PENDING",
                "Invalid userId=$userId"
            )

            return
        }

        if (userEmail.isBlank()) {

            Log.e(
                "IMAGE_PENDING",
                "User email not found"
            )

            return
        }

        Log.d(
            "IMAGE_PENDING",
            "User=$userEmail, Vehicle=$number"
        )

        // =========================================================
        // CHECK CURRENT USER'S PENDING RECORD
        // =========================================================

        val existing =
            pendingImageUploadDao.getPendingForVehicle(
                vehicleNumber = number,
                agencyId = agencyId,
                userEmail = userEmail
            )

        if (existing == null) {

            pendingImageUploadDao.insert(

                PendingImageUploadEntity(

                    vehicleNumber = number,

                    status = status,

                    agencyId = agencyId,

                    uploadStatus = "PENDING",

                    userId = userId.toString(),

                    userEmail = userEmail
                )
            )

            Log.d(
                "IMAGE_PENDING",
                "Pending image added for $userEmail: $number"
            )

        } else {

            Log.d(
                "IMAGE_PENDING",
                "Pending image already exists for $userEmail: $number"
            )
        }
    }






    // =========================================================
// GET CURRENT USER'S PENDING IMAGE UPLOADS
// =========================================================

    suspend fun getPendingImageUploads(
        agencyId: String,
        userEmail: String
    ): List<PendingImageUploadEntity> {

        if (agencyId.isBlank()) {

            Log.e(
                "IMAGE_PENDING",
                "Agency ID not found"
            )

            return emptyList()
        }

        if (userEmail.isBlank()) {

            Log.e(
                "IMAGE_PENDING",
                "User email not found"
            )

            return emptyList()
        }

        Log.d(
            "IMAGE_PENDING",
            "Loading pending uploads"
        )

        Log.d(
            "IMAGE_PENDING",
            "Agency ID: $agencyId"
        )

        Log.d(
            "IMAGE_PENDING",
            "User Email: $userEmail"
        )

        return pendingImageUploadDao.getPendingUploads(
            agencyId = agencyId,
            userEmail = userEmail
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

        val userEmail =
            sessionManager.getUserEmail()

        if (userEmail.isBlank()) {

            Log.e(
                "IMAGE_UPLOAD",
                "User email not found"
            )

            return
        }

        // =========================================================
        // MARK PENDING IMAGE AS UPLOADED
        // CURRENT USER ONLY
        // =========================================================

        pendingImageUploadDao.markUploadedByVehicle(
            vehicleNumber = number,
            agencyId = agencyId,
            userEmail = userEmail
        )

        // =========================================================
        // MARK VEHICLE IMAGE UPLOAD COMPLETED
        // =========================================================

        vehicleDao.markImageUploadCompleted(
            number,
            agencyId
        )

        Log.d(
            "IMAGE_UPLOAD",
            "Image upload completed for $userEmail: $number"
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

        val userEmail =
            sessionManager.getUserEmail()

        if (userEmail.isBlank()) {

            Log.e(
                "IMAGE_PENDING",
                "User email not found"
            )

            return
        }

        val pending =
            pendingImageUploadDao.getPendingForVehicle(
                vehicleNumber = number,
                agencyId = agencyId,
                userEmail = userEmail
            )

        if (pending != null) {

            pendingImageUploadDao.delete(
                id = pending.id,
                agencyId = agencyId,
                userEmail = userEmail
            )

            Log.d(
                "IMAGE_PENDING",
                "Pending image removed for $userEmail: $number"
            )
        }
    }

// =========================================================
// DELETE SEARCH HISTORY
// =========================================================

    suspend fun deleteSearchHistory(
        id: Long
    ): Boolean {

        return try {

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {

                Log.e(
                    "DELETE_HISTORY",
                    "Invalid userId=$userId"
                )

                return false
            }

            Log.d(
                "DELETE_HISTORY",
                "Deleting history id=$id, userId=$userId"
            )

            val response =
                api.deleteSearchHistory(
                    id,
                    userId
                )

            Log.d(
                "DELETE_HISTORY",
                "HTTP code=${response.code()}"
            )

            if (response.isSuccessful) {

                val result =
                    response.body()

                Log.d(
                    "DELETE_HISTORY",
                    "Response=$result"
                )

                result?.success == true

            } else {

                Log.e(
                    "DELETE_HISTORY",
                    "API error=${
                        response.errorBody()?.string()
                    }"
                )

                false
            }

        } catch (e: Exception) {

            Log.e(
                "DELETE_HISTORY",
                "Exception deleting history",
                e
            )

            false
        }
    }
    suspend fun deleteMultipleSearchHistory(
        ids: List<Long>
    ): Boolean {

        return try {

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {
                return false
            }

            val request =
                BulkDeleteSearchHistoryRequest(ids)

            val response =
                api.deleteMultipleSearchHistory(
                    userId,
                    request
                )

            android.util.Log.d(
                "SEARCH_DELETE",
                "HTTP CODE: ${response.code()}"
            )

            android.util.Log.d(
                "SEARCH_DELETE",
                "BODY: ${response.body()}"
            )

            android.util.Log.d(
                "SEARCH_DELETE",
                "ERROR: ${response.errorBody()?.string()}"
            )

            response.isSuccessful

        } catch (e: Exception) {

            android.util.Log.e(
                "SEARCH_DELETE",
                "Exception",
                e
            )

            false
        }
    }

// =========================================================
// DELETE SINGLE VEHICLE
// =========================================================

    suspend fun deleteVehicle(
        vehicleNumber: String
    ): Boolean {

        return try {

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {

                Log.e(
                    "DELETE_VEHICLE",
                    "Invalid userId=$userId"
                )

                return false
            }

            Log.d(
                "DELETE_VEHICLE",
                "Deleting vehicle=$vehicleNumber userId=$userId"
            )

            val response =
                api.deleteVehicle(
                    vehicleNumber,
                    userId
                )

            Log.d(
                "DELETE_VEHICLE",
                "HTTP=${response.code()}"
            )

            if (response.isSuccessful) {

                // Remove from Room also
                try {

                    val agencyId =
                        getCurrentAgencyId()

                    if (agencyId.isNotBlank()) {

                        vehicleDao.deleteVehicle(
                            vehicleNumber,
                            agencyId
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        "DELETE_VEHICLE",
                        "Room delete failed",
                        e
                    )
                }

                true

            } else {

                Log.e(
                    "DELETE_VEHICLE",
                    "API error=${
                        response.errorBody()?.string()
                    }"
                )

                false
            }

        } catch (e: Exception) {

            Log.e(
                "DELETE_VEHICLE",
                "Exception",
                e
            )

            false
        }
    }


// =========================================================
// DELETE MULTIPLE VEHICLES
// =========================================================

    suspend fun deleteMultipleVehicles(
        vehicleNumbers: List<String>
    ): Boolean {

        return try {

            if (vehicleNumbers.isEmpty()) {

                return false
            }

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {

                Log.e(
                    "BULK_DELETE",
                    "Invalid userId=$userId"
                )

                return false
            }

            val request =
                com.example.repomaster.models
                    .BulkDeleteVehicleRequest(
                        vehicleNumbers
                    )

            Log.d(
                "BULK_DELETE",
                "Deleting ${vehicleNumbers.size} vehicles"
            )

            val response =
                api.deleteMultipleVehicles(
                    userId,
                    request
                )

            Log.d(
                "BULK_DELETE",
                "HTTP=${response.code()}"
            )

            if (response.isSuccessful) {

                // Remove from Room
                try {

                    val agencyId =
                        getCurrentAgencyId()

                    if (agencyId.isNotBlank()) {

                        vehicleNumbers.forEach {

                            vehicleDao.deleteVehicle(
                                it,
                                agencyId
                            )
                        }
                    }

                } catch (e: Exception) {

                    Log.e(
                        "BULK_DELETE",
                        "Room cleanup failed",
                        e
                    )
                }

                true

            } else {

                Log.e(
                    "BULK_DELETE",
                    "API error=${
                        response.errorBody()?.string()
                    }"
                )

                false
            }

        } catch (e: Exception) {

            Log.e(
                "BULK_DELETE",
                "Exception",
                e
            )

            false
        }
    }


// =========================================================
// DELETE ALL VEHICLES BY UPLOAD DATE
// =========================================================

    suspend fun deleteVehiclesByUploadDate(
        date: String
    ): Boolean {

        return try {

            val userId =
                sessionManager.getUserId()

            if (userId <= 0) {

                Log.e(
                    "DATE_DELETE",
                    "Invalid userId=$userId"
                )

                return false
            }

            Log.d(
                "DATE_DELETE",
                "Deleting date=$date userId=$userId"
            )

            val response =
                api.deleteVehiclesByDate(
                    date,
                    userId
                )

            Log.d(
                "DATE_DELETE",
                "HTTP=${response.code()}"
            )

            if (response.isSuccessful) {

                // Refresh from server after deletion.
                // This is safer than trying to guess Room rows.

                Log.d(
                    "DATE_DELETE",
                    "Date-wise delete successful"
                )

                true

            } else {

                Log.e(
                    "DATE_DELETE",
                    "API error=${
                        response.errorBody()?.string()
                    }"
                )

                false
            }

        } catch (e: Exception) {

            Log.e(
                "DATE_DELETE",
                "Exception",
                e
            )

            false
        }
    }
    suspend fun searchVehicle(
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

        if (number.isBlank()) {
            return null
        }

        Log.d(
            "VEHICLE_SEARCH",
            "Searching: $number"
        )

        // =========================================================
        // 1. ONLINE SEARCH
        // =========================================================

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

                        // IMPORTANT
                        // Save every search locally first.
                        saveSearchHistoryLocally(
                            number
                        )

                        // Immediately try to send it to PHP.
                        syncPendingSearchHistory()

                        Log.d(
                            "VEHICLE_SEARCH",
                            "Online search history saved"
                        )

                        return vehicle
                    }
                }

                Log.w(
                    "VEHICLE_SEARCH",
                    "API search unsuccessful: ${response.code()}"
                )

            } catch (e: Exception) {

                Log.e(
                    "VEHICLE_SEARCH",
                    "API failed - trying Room",
                    e
                )
            }
        }

        // =========================================================
        // 2. OFFLINE / API FAILED
        // =========================================================

        val agencyId =
            sessionManager.getAgencyId().trim()

        if (agencyId.isBlank()) {

            Log.e(
                "VEHICLE_SEARCH",
                "Agency ID missing"
            )

            return null
        }

        Log.d(
            "VEHICLE_SEARCH",
            "Searching Room: $number"
        )

        val localVehicle =
            vehicleDao.getVehicle(
                number,
                agencyId
            )

        if (localVehicle != null) {

            // =====================================================
            // THIS IS THE IMPORTANT FIX
            // =====================================================

            saveSearchHistoryLocally(
                number
            )

            Log.d(
                "VEHICLE_SEARCH",
                "Offline search recorded locally: $number"
            )

            return localVehicle.toVehicle()
        }

        Log.d(
            "VEHICLE_SEARCH",
            "Vehicle not found offline"
        )

        return null
    }


    private fun getCurrentAgencyId(): String {
        return sessionManager.getAgencyId().trim()
    }
}