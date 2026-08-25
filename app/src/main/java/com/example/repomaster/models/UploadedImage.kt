package com.example.repomaster.models

data class UploadedImage(
    val id: Int,
    val vehicle_number: String,
    val user_id: Int?,
    val user_name: String?,
    val user_email: String?,
    val status: String,
    val created_at: String?,
    val updated_at: String?,
    val uploaded_at: String?
)