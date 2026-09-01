package com.example.repomaster.models



data class AdminPaymentVehicle(
    val repo_year: String,
    val repo_month: String,
    val loan_number: String,
    val vehicle_number: String,
    val vehicle_type: String?,
    val repo_status: String?,
    val repo_marked_by: Int?,
    val repo_marked_at: String?,
    val parked_by: Int?,
    val parked_at: String?,
    val total_charges: String?
)

