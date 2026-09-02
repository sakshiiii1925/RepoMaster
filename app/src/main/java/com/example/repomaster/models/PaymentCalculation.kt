package com.example.repomaster.models

data class PaymentCalculation(
    val user_id: Int,
    val repo_year: String,
    val repo_month: String,
    val loan_number: String,
    val vehicle_number: String,
    val vehicle_type: String?,
    val agency_id: String?,
    val work_type: String,
    val total_amount: String,
    val paid_amount: String,
    val remaining_amount: String,
    val already_paid: Boolean,
    val completed_at: String?,
    val payment_history: List<PaymentHistoryItem>?
)