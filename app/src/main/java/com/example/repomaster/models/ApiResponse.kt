package com.example.repomaster.models


data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

