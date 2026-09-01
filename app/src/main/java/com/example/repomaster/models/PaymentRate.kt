
package com.example.repomaster.models

data class PaymentRate(
    val id: Long,
    val agency_id: String,
    val vehicle_type: String,
    val repo_mark_rate: String,
    val parked_rate: String,
    val created_at: String?,
    val updated_at: String?
)

