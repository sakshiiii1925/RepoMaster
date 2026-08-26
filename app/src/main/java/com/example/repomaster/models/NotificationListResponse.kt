package com.example.repomaster.models



data class NotificationListResponse(
    val success: Boolean,
    val data: List<AdminNotification>? = null,
    val message: String? = null
)