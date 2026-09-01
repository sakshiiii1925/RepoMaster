package com.example.repomaster.models

data class AdminPaymentUser(
    val id: Int,
    val full_name: String,
    val email: String,
    val mobile: String?,
    val address: String?,
    val role: String?,
    val status: String?
)


