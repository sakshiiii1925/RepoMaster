package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.data.local.PendingImageUploadEntity
import com.example.repomaster.repository.VehicleRepository
import kotlinx.coroutines.launch

class PendingImageUploadViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _pendingUploads =
        MutableLiveData<List<PendingImageUploadEntity>>()

    val pendingUploads:
            LiveData<List<PendingImageUploadEntity>> =
        _pendingUploads

    // =========================================================
    // LOAD CURRENT USER'S PENDING UPLOADS
    // =========================================================

    fun loadPendingUploads(
        agencyId: String,
        userEmail: String
    ) {

        viewModelScope.launch {

            val uploads =
                repository.getPendingImageUploads(
                    agencyId = agencyId,
                    userEmail = userEmail
                )

            _pendingUploads.postValue(
                uploads
            )
        }
    }
}