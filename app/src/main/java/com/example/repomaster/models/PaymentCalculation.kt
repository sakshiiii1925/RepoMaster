package com.example.repomaster.models


data class PaymentCalculation(
    val user_id: Int,
    val repo_year: String,
    val repo_month: String,
    val loan_number: String,
    val vehicle_number: String,
    val vehicle_type: String,
    val repo_status: String,
    val agency_id: String,
    val amount: String
)

