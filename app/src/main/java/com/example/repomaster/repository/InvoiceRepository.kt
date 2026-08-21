package com.example.repomaster.repository

import com.example.repomaster.models.PaymentCreateRequest
import com.example.repomaster.api.InvoiceApi
import com.example.repomaster.models.Invoice
import com.example.repomaster.models.PaymentUpdateRequest
class InvoiceRepository(
    private val invoiceApi: InvoiceApi
) {

    suspend fun createInvoice(
        invoice: Invoice
    ) = invoiceApi.createInvoice(invoice)

    suspend fun getInvoiceById(
        id: Long
    ) = invoiceApi.getInvoiceById(id)

    suspend fun getInvoicesByAgency(
        agencyId: String
    ) = invoiceApi.getInvoicesByAgency(agencyId)

    suspend fun deleteInvoice(
        id: Long
    ) = invoiceApi.deleteInvoice(id)
    suspend fun updatePayment(
        id: Long,
        request: PaymentUpdateRequest
    ) =
        invoiceApi.updatePayment(
            id,
            request
        )
    suspend fun addPayment(
        id: Long,
        request: PaymentCreateRequest
    ) =
        invoiceApi.addPayment(
            id,
            request
        )
    suspend fun getPaymentHistory(
        id: Long
    ) =
        invoiceApi.getPaymentHistory(
            id
        )
    suspend fun deletePayment(
        id: Long
    ) =
        invoiceApi.deletePayment(id)
}