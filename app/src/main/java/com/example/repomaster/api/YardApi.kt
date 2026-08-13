package com.example.repomaster.api


import com.example.repomaster.models.Yard
import retrofit2.Response
import retrofit2.http.*
import okhttp3.ResponseBody
import com.example.repomaster.models.Vehicle
interface YardApi {

    // Get all yards for an agency
    @GET("api/yards")
    suspend fun getYards(
        @Query("agencyId") agencyId: String
    ): Response<List<Yard>>

    // Get one yard
    @GET("api/yards/{id}")
    suspend fun getYard(
        @Path("id") id: Long,
        @Query("agencyId") agencyId: String
    ): Response<Yard>

    // Add new yard
    @POST("api/yards")
    suspend fun addYard(
        @Body yard: Yard
    ): Response<Yard>

    // Update yard
    @PUT("api/yards/{id}")
    suspend fun updateYard(
        @Path("id") id: Long,
        @Query("agencyId") agencyId: String,
        @Body yard: Yard
    ): Response<Yard>

    // Delete yard
    @DELETE("api/yards/{id}")
    suspend fun deleteYard(
        @Path("id") id: Long,
        @Query("agencyId") agencyId: String
    ): Response<Void>
    // Assign vehicle to yard


    @PUT("api/vehicles/{vehicleNumber}/assign-yard")
    suspend fun assignVehicleToYard(
        @Path("vehicleNumber") vehicleNumber: String,
        @Query("yardId") yardId: Long
    ): Response<ResponseBody>
    @GET("api/vehicles/yard/{yardId}")
    suspend fun getVehiclesByYard(
        @Path("yardId") yardId: Long,
        @Query("agencyId") agencyId: String
    ): Response<List<Vehicle>>
    @PUT("api/vehicles/{vehicleNumber}/remove-yard")
    suspend fun removeVehicleFromYard(
        @Path("vehicleNumber") vehicleNumber: String
    ): Response<ResponseBody>
    @GET("api/reports/yard/excel/{yardId}")
    suspend fun downloadYardExcel(
        @Path("yardId") yardId: Long,
        @Query("agencyId") agencyId: String,
        @Query("status") status: String
    ): Response<ResponseBody>
}