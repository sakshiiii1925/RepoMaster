
package com.example.repomaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.repomaster.repository.AdminPaymentRepository


class AdminPaymentViewModelFactory(
    private val repository: AdminPaymentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                AdminPaymentViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")

            return AdminPaymentViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}

