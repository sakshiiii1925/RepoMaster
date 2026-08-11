package com.example.repomaster.models



data class Yard(
    val id: Long? = null,
    val yardName: String,
    val yardAddress: String?,
    val yardManagerName: String?,
    val yardContactNo: String?,
    val agencyId: String
)