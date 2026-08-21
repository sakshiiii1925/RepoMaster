package com.example.repomaster.models


data class Payment(
    val id: Long?,
    val invoiceId: Long?,
    val paymentDate: String?,
    val paymentAmount: Double?,
    val remarks: String?,
    val createdBy: String?,
    val createdDate: String?
)