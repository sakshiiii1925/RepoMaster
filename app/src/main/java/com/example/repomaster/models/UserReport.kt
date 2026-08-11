package com.example.repomaster.models



data class UserReport(
    val totalVehicles: Int,
    val repoMarked: Int,
    val parked: Int,
    val released: Int
)