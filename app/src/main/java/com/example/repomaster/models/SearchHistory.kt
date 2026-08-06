package com.example.repomaster.models



data class SearchHistory(

    val id: Long? = null,

    val vehicleNumber: String,

    val userEmail: String,
    val agencyId: String,
    val userName: String,

    val searchTime: String
)