package com.example.repomaster.models





data class LoginResponse(
    val success: Boolean,
    val message: String,
    val fullName: String?,
    val email: String?,
    val role: String?,
    val status: String?,
    val agencyId: String?
)