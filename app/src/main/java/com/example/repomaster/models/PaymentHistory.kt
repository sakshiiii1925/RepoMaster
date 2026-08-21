package com.example.repomaster.models

data class PaymentHistory(
    val id: Long?,
    val invoiceId: Long?,
    val paymentDate: String?,
    val amount: Double?,
    val paymentStatus: String?,
    val remarks: String?,
    val createdBy: String?,
    val createdDate: String?
)