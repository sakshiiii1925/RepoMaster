package com.example.repomaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.repository.VehicleRepository

class UploadedImagesViewModelFactory(
    private val repository: VehicleRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                UploadedImagesViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return UploadedImagesViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}