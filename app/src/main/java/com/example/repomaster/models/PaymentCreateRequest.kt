package com.example.repomaster.models


data class PaymentCreateRequest(
    val paymentDate: String?,
    val paymentAmount: Double,
    val remarks: String?,
    val createdBy: String?
)