
package com.example.repomaster.api

import com.example.repomaster.models.Vehicle
import com.example.repomaster.models.StatusUpdateRequest
import com.example.repomaster.models.SearchHistory
import com.example.repomaster.models.UploadResponse
import com.example.repomaster.models.ApiResponse1
import com.example.repomaster.models.VehicleCountResponse
import com.example.repomaster.models.BulkDeleteSearchHistoryRequest
import com.example.repomaster.models.BulkDeleteVehicleRequest

import okhttp3.MultipartBody
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part

interface VehicleApi {

    // =========================================================
    // GET VEHICLE
    // =========================================================

    @GET("api/vehicles/{vehicleNumber}")
    suspend fun getVehicle(
        @Path("vehicleNumber")
        vehicleNumber: String,

        @Query("userId")
        userId: Int
    ): Response<Vehicle>


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @PUT("api/vehicles/{vehicleNumber}/status")
    suspend fun updateStatus(
        @Path("vehicleNumber")
        vehicleNumber: String,

        @Query("userId")
        userId: Int,

        @Body
        request: StatusUpdateRequest
    ): Response<Vehicle>


    // =========================================================
    // ADD VEHICLE
    // =========================================================

    @POST("api/vehicles")
    suspend fun addVehicle(
        @Body vehicle: Vehicle
    ): Response<Vehicle>


    // =========================================================
    // GET ALL VEHICLES
    // =========================================================

    @GET("api/vehicles")
    suspend fun getAllVehicles(
        @Query("userId")
        userId: Int
    ): Response<List<Vehicle>>


    // =========================================================
    // UPDATE VEHICLE
    // =========================================================

    @PUT("api/vehicles/{vehicleNumber}")
    suspend fun updateVehicle(
        @Path("vehicleNumber")
        vehicleNumber: String,

        @Body vehicle: Vehicle
    ): Response<Vehicle>


    // =========================================================
    // DELETE SINGLE VEHICLE
    // =========================================================

    @DELETE("api/vehicles/{vehicleNumber}")
    suspend fun deleteVehicle(
        @Path("vehicleNumber")
        vehicleNumber: String,

        @Query("userId")
        userId: Int
    ): Response<String>


    // =========================================================
    // DELETE MULTIPLE VEHICLES
    // =========================================================

    @POST("api/vehicles/bulk-delete")
    suspend fun deleteMultipleVehicles(
        @Query("userId")
        userId: Int,

        @Body
        request: BulkDeleteVehicleRequest
    ): Response<ApiResponse1>


    // =========================================================
    // DELETE ALL VEHICLES BY UPLOAD DATE
    // =========================================================

    @DELETE("api/vehicles/date/{date}")
    suspend fun deleteVehiclesByDate(
        @Path("date")
        date: String,

        @Query("userId")
        userId: Int
    ): Response<ApiResponse1>


    // =========================================================
    // EXCEL UPLOAD
    // =========================================================

    @Multipart
    @POST("api/vehicles/upload-excel")
    suspend fun uploadExcel(
        @Query("agencyId")
        agencyId: String,

        @Part
        file: MultipartBody.Part
    ): Response<UploadResponse>


    // =========================================================
    // SEARCH VEHICLES
    // =========================================================

    @GET("api/vehicles/search")
    suspend fun searchVehicles(
        @Query("keyword")
        keyword: String,

        @Query("userId")
        userId: Int
    ): Response<List<Vehicle>>


    // =========================================================
    // SEARCH HISTORY
    // =========================================================

    @POST("api/search-history/save")
    suspend fun saveSearchHistory(
        @Query("vehicleNumber")
        vehicleNumber: String,

        @Query("userEmail")
        userEmail: String,

        @Query("userName")
        userName: String,

        @Query("agencyId")
        agencyId: String
    ): Response<SearchHistory>


    @GET("api/search-history")
    suspend fun getSearchHistory(
        @Query("agencyId")
        agencyId: String
    ): Response<List<SearchHistory>>


    @GET("api/search-history/admin/all")
    suspend fun getAllSearchHistory():
            Response<List<SearchHistory>>


    @GET("api/search-history/search")
    suspend fun searchHistoryByVehicle(
        @Query("agencyId")
        agencyId: String,

        @Query("vehicleNumber")
        vehicleNumber: String
    ): Response<List<SearchHistory>>


    @GET("api/search-history/sort")
    suspend fun sortSearchHistory(
        @Query("agencyId")
        agencyId: String,

        @Query("order")
        order: String
    ): Response<List<SearchHistory>>


    @GET("api/search-history/filter/user")
    suspend fun filterByUser(
        @Query("agencyId")
        agencyId: String,

        @Query("userName")
        userName: String
    ): Response<List<SearchHistory>>


    @GET("api/search-history/filter/date")
    suspend fun filterByDate(
        @Query("agencyId")
        agencyId: String,

        @Query("date")
        date: String
    ): Response<List<SearchHistory>>


    // =========================================================
    // TEMPLATE
    // =========================================================

    @GET("api/admin/download-template")
    fun downloadTemplate():
            Call<ResponseBody>


    // =========================================================
    // VEHICLE COUNT
    // =========================================================

    @GET("api/vehicles/count")
    suspend fun getVehicleCount(
        @Query("userId")
        userId: Int
    ): Response<VehicleCountResponse>


    // =========================================================
    // DELETE SEARCH HISTORY
    // =========================================================

    @DELETE("api/search-history/{id}")
    suspend fun deleteSearchHistory(
        @Path("id")
        id: Long,

        @Query("userId")
        userId: Int
    ): Response<ApiResponse1>


    @POST("api/search-history/bulk-delete")
    suspend fun deleteMultipleSearchHistory(
        @Query("userId")
        userId: Int,

        @Body
        request: BulkDeleteSearchHistoryRequest
    ): Response<ApiResponse1>
}

