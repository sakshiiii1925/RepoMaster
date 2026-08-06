package com.example.repomaster.models



data class UserActivityReport(
    val userName: String,
    val userEmail: String,
    val agencyId: String,
    val totalSearches: Long,
    val repoMarkedCount: Long,
    val parkedCount: Long,
    val releasedCount: Long,
    val lastSearchTime: String
)