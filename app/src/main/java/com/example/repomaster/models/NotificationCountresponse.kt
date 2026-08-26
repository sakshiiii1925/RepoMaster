package com.example.repomaster.models




data class NotificationCountresponse(
    val success: Boolean,
    val data: NotificationCount? = null,
    val message: String? = null
)

data class NotificationCount(
    val count: Int
)