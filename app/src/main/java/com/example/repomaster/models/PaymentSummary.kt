package com.example.repomaster.models



data class PaymentSummary(
    val user_id: Int,
    val completed_work: Int,
    val total_due: String,
    val total_paid: String,
    val remaining: String
)