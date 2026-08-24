package com.example.repomaster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.repository.RepoImageRepository

class RepoImageUploadViewModelFactory(
    private val repository: RepoImageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                RepoImageUploadViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return RepoImageUploadViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}