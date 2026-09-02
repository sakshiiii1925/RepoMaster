package com.example.repomaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.repository.UserPaymentRepository

class UserPaymentHistoryViewModelFactory(
    private val repository: UserPaymentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                UserPaymentHistoryViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")

            return UserPaymentHistoryViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}