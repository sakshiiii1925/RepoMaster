package com.example.repomaster.api

import com.example.repomaster.models.Invoice
import com.example.repomaster.models.Payment
import com.example.repomaster.models.PaymentCreateRequest
import com.example.repomaster.models.PaymentUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface InvoiceApi {

    // ------------------------------------------------
    // CREATE INVOICE
    // ------------------------------------------------

    @POST("api/invoices")
    suspend fun createInvoice(
        @Body invoice: Invoice
    ): Response<Invoice>


    // ------------------------------------------------
    // GET INVOICE
    // ------------------------------------------------

    @GET("api/invoices/{id}")
    suspend fun getInvoiceById(
        @Path("id") id: Long
    ): Response<Invoice>


    // ------------------------------------------------
    // GET INVOICES BY AGENCY
    // ------------------------------------------------

    @GET("api/invoices")
    suspend fun getInvoicesByAgency(
        @Query("agencyId") agencyId: String
    ): Response<List<Invoice>>


    // ------------------------------------------------
    // DELETE INVOICE
    // ------------------------------------------------

    @DELETE("api/invoices/{id}")
    suspend fun deleteInvoice(
        @Path("id") id: Long
    ): Response<String>


    // ------------------------------------------------
    // UPDATE INVOICE PAYMENT SUMMARY
    // ------------------------------------------------

    @PUT("api/invoices/{id}/payment")
    suspend fun updatePayment(
        @Path("id") id: Long,
        @Body request: PaymentUpdateRequest
    ): Response<Invoice>


    // =================================================
    // PAYMENT HISTORY
    // =================================================

    // Add payment
    @POST("api/invoices/{id}/payments")
    suspend fun addPayment(
        @Path("id") id: Long,
        @Body request: PaymentCreateRequest
    ): Response<Payment>


    // Get payment history
    @GET("api/invoices/{id}/payments")
    suspend fun getPaymentHistory(
        @Path("id") id: Long
    ): Response<List<Payment>>


    // Get individual payment
    @GET("api/invoice-payments/{id}")
    suspend fun getPayment(
        @Path("id") id: Long
    ): Response<Payment>


    // Delete individual payment
    @DELETE("api/invoice-payments/{id}")
    suspend fun deletePayment(
        @Path("id") id: Long
    ): Response<String>
}