package com.example.repomaster.models

data class financeReport(
    val finance: String,
    val branch: String,
    val totalVehicles: Long,
    val repoMarkedCount: Long,
    val parkedCount: Long,
    val releasedCount: Long,
)