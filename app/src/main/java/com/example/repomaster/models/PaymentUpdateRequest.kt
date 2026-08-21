package com.example.repomaster.models


data class PaymentUpdateRequest(
    val paymentReceived: Double,
    val paymentDate: String?,
    val paymentStatus: String
)