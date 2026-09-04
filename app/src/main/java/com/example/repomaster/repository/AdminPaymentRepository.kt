package com.example.repomaster.repository

import com.example.repomaster.api.AdminPaymentApi
import com.example.repomaster.models.*
import okhttp3.Response
class AdminPaymentRepository(
    private val api: AdminPaymentApi
) {

    // =========================================================
    // USERS
    // =========================================================

    suspend fun getUsers(
        agencyId: String
    ) =
        api.getUsers(agencyId)


    // =========================================================
    // USER VEHICLES
    // =========================================================

    suspend fun getUserVehicles(
        userId: Int,
        agencyId: String
    ) =
        api.getUserVehicles(
            userId = userId,
            agencyId = agencyId
        )


    // =========================================================
// CALCULATE PAYMENT
// =========================================================

    suspend fun calculatePayment(
        userId: Int,
        repoYear: String,
        repoMonth: String,
        loanNumber: String,
        workType: String
    ) =
        api.calculatePayment(
            userId = userId,
            repoYear = repoYear,
            repoMonth = repoMonth,
            loanNumber = loanNumber,
            workType = workType
        )


    // =========================================================
    // CREATE PAYMENT
    // =========================================================

    suspend fun createPayment(
        request: CreatePaymentRequest
    ) =
        api.createPayment(request)


    // =========================================================
    // PAYMENT SUMMARY
    // =========================================================

    suspend fun getSummary(
        userId: Int
    ) =
        api.getSummary(userId)


    // =========================================================
    // PAYMENT RATES
    // =========================================================

    suspend fun getRates(
        agencyId: String
    ) =
        api.getRates(agencyId)


    // =========================================================
    // SAVE / UPDATE RATE
    // =========================================================

    suspend fun saveRate(
        request: SavePaymentRateRequest
    ) =
        api.saveRate(request)


    // =========================================================
    // DELETE RATE
    // =========================================================

    suspend fun deleteRate(
        id: Long,
        agencyId: String
    ) =
        api.deleteRate(
            id = id,
            agencyId = agencyId
        )
    // =========================================================
// PAYMENT HISTORY
// =========================================================

    suspend fun getPaymentHistory(userId: Int) =
        api.getPaymentHistory(userId)
    suspend fun deletePayment(
        paymentId: Int
    )=

         api.deletePayment(paymentId)

}

