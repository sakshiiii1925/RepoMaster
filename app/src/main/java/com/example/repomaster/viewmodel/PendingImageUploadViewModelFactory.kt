package com.example.repomaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.repository.VehicleRepository

class PendingImageUploadViewModelFactory(
    private val repository: VehicleRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return PendingImageUploadViewModel(
            repository
        ) as T
    }
}