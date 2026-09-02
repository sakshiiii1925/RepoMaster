package com.example.repomaster.models

data class PaymentHistoryItem(
    val id: Long,
    val amount: String,
    val payment_method: String,
    val payment_date: String?,
    val remarks: String?,
    val created_at: String?
)