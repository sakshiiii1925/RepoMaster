package com.example.repomaster.models

data class AdminPayment(
    val id: Int,
    val user_id: Int,
    val repo_year: String,
    val repo_month: String,
    val loan_number: String,
    val vehicle_number: String,
    val vehicle_type: String?,
    val repo_status: String?,
    val amount: String,
    val payment_method: String,
    val payment_date: String,
    val remarks: String?,
    val created_at: String,
    val updated_at: String
)