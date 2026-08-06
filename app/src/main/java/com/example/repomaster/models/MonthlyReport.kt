package com.example.repomaster.models

data class MonthlyReport(
    val repoYear: String,
    val repoMonth: String,
    val totalVehicles: Long,
    val repoMarkedCount: Long,
    val parkedCount: Long,
    val releasedCount: Long,
)