package com.example.repomaster.api

import com.example.repomaster.models.AdminPayment
import com.example.repomaster.models.ApiResponse
import com.example.repomaster.models.PaymentSummary
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserPaymentApi {

    @GET("api/user/payment/history")
    suspend fun getPaymentHistory(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<List<AdminPayment>>>


    @GET("api/user/payment/summary")
    suspend fun getPaymentSummary(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<PaymentSummary>>
}