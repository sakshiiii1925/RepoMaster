package com.example.repomaster.repository


import com.example.repomaster.api.InvoiceApi
import com.example.repomaster.models.Invoice

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
}