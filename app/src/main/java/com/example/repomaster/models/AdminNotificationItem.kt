package com.example.repomaster.models

data class AdminNotificationItem(
    val type: String,
    val title: String,
    val message: String,
    val date: String,
    val isRead: Boolean,
    val notificationId: Int? = null,
    val vehicleNumber: String? = null
)