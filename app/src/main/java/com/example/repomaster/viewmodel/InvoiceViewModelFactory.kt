package com.example.repomaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.repository.InvoiceRepository

class InvoiceViewModelFactory(
    private val repository: InvoiceRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                InvoiceViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return InvoiceViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}