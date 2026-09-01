package com.example.repomaster.models

data class ApiMessageResponse(
    val success: Boolean,
    val message: String?,
    val data: Any?
)

