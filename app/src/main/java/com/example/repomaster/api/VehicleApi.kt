package com.example.repomaster.api

import com.example.repomaster.models.Vehicle
import com.example.repomaster.models.StatusUpdateRequest
import com.example.repomaster.models.SearchHistory
import com.example.repomaster.models.UploadResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @GET("api/vehicles/{vehicleNumber}")
    suspend fun getVehicle(
        @Path("vehicleNumber") vehicleNumber: String
    ): Response<Vehicle>

    @PUT("api/vehicles/{vehicleNumber}/status")
    suspend fun updateStatus(
        @Path("vehicleNumber") vehicleNumber: String,
        @Body request: StatusUpdateRequest
    ): Response<Vehicle>

    @POST("api/vehicles")
    suspend fun addVehicle(
        @Body vehicle: Vehicle
    ): Response<Vehicle>

    @GET("api/vehicles")
    suspend fun getAllVehicles(
        @Query("agencyId") agencyId: String
    ): Response<List<Vehicle>>

    @PUT("api/vehicles/{vehicleNumber}")
    suspend fun updateVehicle(
        @Path("vehicleNumber") vehicleNumber: String,
        @Body vehicle: Vehicle
    ): Response<Vehicle>

    @DELETE("api/vehicles/{vehicleNumber}")
    suspend fun deleteVehicle(
        @Path("vehicleNumber") vehicleNumber: String
    ): Response<String>

    @Multipart
    @POST("api/vehicles/upload-excel")
    suspend fun uploadExcel(
        @Query("agencyId") agencyId: String,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("api/vehicles/search")
    suspend fun searchVehicles(
        @Query("keyword") keyword: String
    ): Response<List<Vehicle>>

    @POST("api/search-history/save")
    suspend fun saveSearchHistory(
        @Query("vehicleNumber") vehicleNumber: String,
        @Query("userEmail") userEmail: String,
        @Query("userName") userName: String,
        @Query("agencyId") agencyId: String
    ): Response<SearchHistory>

    @GET("api/search-history")
    suspend fun getSearchHistory(
        @Query("agencyId") agencyId: String
    ): Response<List<SearchHistory>>

    @GET("api/search-history/admin/all")
    suspend fun getAllSearchHistory(): Response<List<SearchHistory>>

    @GET("api/search-history/search")
    suspend fun searchHistoryByVehicle(
        @Query("agencyId") agencyId: String,
        @Query("vehicleNumber") vehicleNumber: String
    ): Response<List<SearchHistory>>

    @GET("api/search-history/sort")
    suspend fun sortSearchHistory(
        @Query("agencyId") agencyId: String,
        @Query("order") order: String
    ): Response<List<SearchHistory>>

    @GET("api/search-history/filter/user")
    suspend fun filterByUser(
        @Query("agencyId") agencyId: String,
        @Query("userName") userName: String
    ): Response<List<SearchHistory>>

    @GET("api/search-history/filter/date")
    suspend fun filterByDate(
        @Query("agencyId") agencyId: String,
        @Query("date") date: String
    ): Response<List<SearchHistory>>

    @GET("api/admin/download-template")
    fun downloadTemplate(): Call<ResponseBody>

}