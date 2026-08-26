package com.example.repomaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.repository.AdminNotificationRepository

class AdminNotificationViewModelFactory(
    private val repository: AdminNotificationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                AdminNotificationViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return AdminNotificationViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}