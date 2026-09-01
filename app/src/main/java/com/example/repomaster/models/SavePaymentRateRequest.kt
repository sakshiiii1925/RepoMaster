package com.example.repomaster.models

data class SavePaymentRateRequest(
    val agency_id: String,
    val vehicle_type: String,
    val repo_mark_rate: Double,
    val parked_rate: Double
)

