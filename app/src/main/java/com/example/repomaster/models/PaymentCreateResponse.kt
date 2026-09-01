package com.example.repomaster.models

data class PaymentCreateResponse(
    val payment: AdminPayment,
    val summary: PaymentSummary
)