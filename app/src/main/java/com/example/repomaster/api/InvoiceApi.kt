package com.example.repomaster.api



import com.example.repomaster.models.Invoice
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InvoiceApi {

    @POST("api/invoices")
    suspend fun createInvoice(
        @Body invoice: Invoice
    ): Response<Invoice>

    @GET("api/invoices/{id}")
    suspend fun getInvoiceById(
        @Path("id") id: Long
    ): Response<Invoice>

    @GET("api/invoices")
    suspend fun getInvoicesByAgency(
        @Query("agencyId") agencyId: String
    ): Response<List<Invoice>>

    @DELETE("api/invoices/{id}")
    suspend fun deleteInvoice(
        @Path("id") id: Long
    ): Response<String>
}