package com.example.repomaster.api

import com.example.repomaster.models.*
import retrofit2.Response
import retrofit2.http.*

interface AdminPaymentApi {

    // =========================================================
    // GET USERS
    // GET /api/admin/payment/users
    // =========================================================

    @GET("api/admin/payment/users")
    suspend fun getUsers():
            Response<ApiResponse<List<AdminPaymentUser>>>


    // =========================================================
    // GET VEHICLES COMPLETED BY USER
    // GET /api/admin/payment/user-vehicles?user_id=2
    // =========================================================

    @GET("api/admin/payment/user-vehicles")
    suspend fun getUserVehicles(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<List<AdminPaymentVehicle>>>


    // =========================================================
    // CALCULATE PAYMENT
    // GET /api/admin/payment/calculate
    // =========================================================

    @GET("api/admin/payment/calculate")
    suspend fun calculatePayment(
        @Query("user_id") userId: Int,
        @Query("repo_year") repoYear: String,
        @Query("repo_month") repoMonth: String,
        @Query("loan_number") loanNumber: String,
        @Query("work_type") workType: String
    ): Response<ApiResponse<PaymentCalculation>>


    // =========================================================
    // CREATE PAYMENT
    // POST /api/admin/payment
    // =========================================================

    @POST("api/admin/payment")
    suspend fun createPayment(
        @Body request: CreatePaymentRequest
    ): Response<ApiResponse<PaymentCreateResponse>>


    // =========================================================
    // PAYMENT SUMMARY
    // GET /api/admin/payment/summary?user_id=2
    // =========================================================

    @GET("api/admin/payment/summary")
    suspend fun getSummary(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<PaymentSummary>>


    // =========================================================
    // GET PAYMENT RATES
    // GET /api/admin/payment/rates?agency_id=1
    // =========================================================

    @GET("api/admin/payment/rates")
    suspend fun getRates(
        @Query("agency_id") agencyId: String
    ): Response<ApiResponse<List<PaymentRate>>>


    // =========================================================
    // ADD / UPDATE PAYMENT RATE
    // POST /api/admin/payment/rates
    // =========================================================

    @POST("api/admin/payment/rates")
    suspend fun saveRate(
        @Body request: SavePaymentRateRequest
    ): Response<ApiResponse<PaymentRate>>


    // =========================================================
    // DELETE PAYMENT RATE
    // DELETE /api/admin/payment/rates/{id}
    // =========================================================

    @DELETE("api/admin/payment/rates/{id}")
    suspend fun deleteRate(
        @Path("id") id: Long,
        @Query("agency_id") agencyId: String
    ): Response<ApiMessageResponse>
    // =========================================================
// PAYMENT HISTORY FOR PARTICULAR USER
// GET /api/admin/payment/history?user_id=2
// =========================================================

    @GET("api/admin/payment/history")
    suspend fun getPaymentHistory(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<List<AdminPayment>>>
}

