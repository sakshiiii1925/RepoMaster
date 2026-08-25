package com.example.repomaster.models

data class UploadedImageDetails(
    val id: Int,
    val vehicle_number: String,
    val user_id: Int?,
    val user_name: String?,
    val user_email: String?,
    val status: String,

    val inventory_image_1: String,
    val inventory_image_2: String,

    val vehicle_image_1: String,
    val vehicle_image_2: String,
    val vehicle_image_3: String,
    val vehicle_image_4: String,
    val vehicle_image_5: String,

    val created_at: String?,
    val updated_at: String?,
    val uploaded_at: String?
)