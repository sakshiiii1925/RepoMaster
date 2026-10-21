package com.example.repomaster.models

data class UserReport(
    val totalVehicles: Int = 0,
    val repoMarked: Int = 0,
    val parked: Int = 0,
    val released: Int = 0
)