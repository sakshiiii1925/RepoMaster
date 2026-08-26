package com.example.repomaster.models

data class AdminNotification(
    val id: Int,
    val vehicle_number: String,
    val status: String,
    val user_name: String,
    val user_email: String,
    val message: String,
    val is_read: Int,
    val created_at: String
)