package com.example.repomaster.repository

import com.example.repomaster.api.UserPaymentApi

class UserPaymentRepository(
    private val api: UserPaymentApi
) {

    suspend fun getPaymentHistory(
        userId: Int
    ) =
        api.getPaymentHistory(userId)


    suspend fun getPaymentSummary(
        userId: Int
    ) =
        api.getPaymentSummary(userId)
}