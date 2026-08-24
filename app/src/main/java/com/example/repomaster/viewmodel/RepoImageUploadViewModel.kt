package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.RepoImageUploadResponse
import com.example.repomaster.repository.RepoImageRepository
import kotlinx.coroutines.launch

class RepoImageUploadViewModel(
    private val repository: RepoImageRepository
) : ViewModel() {

    private val _uploadResult =
        MutableLiveData<RepoImageUploadResponse?>()

    val uploadResult: LiveData<RepoImageUploadResponse?> =
        _uploadResult

    private val _isLoading =
        MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean> =
        _isLoading

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?> =
        _error


    fun uploadImages(
        vehicleNumber: String,
        status: String,
        userEmail: String,
        userName: String,
        inventoryImage1: java.io.File,
        inventoryImage2: java.io.File,
        vehicleImage1: java.io.File,
        vehicleImage2: java.io.File,
        vehicleImage3: java.io.File,
        vehicleImage4: java.io.File,
        vehicleImage5: java.io.File
    ) {

        viewModelScope.launch {

            try {

                _isLoading.value = true
                _error.value = null

                val result =
                    repository.uploadRepoImages(
                        vehicleNumber = vehicleNumber,
                        status = status,
                        userEmail = userEmail,
                        userName = userName,
                        inventoryImage1 = inventoryImage1,
                        inventoryImage2 = inventoryImage2,
                        vehicleImage1 = vehicleImage1,
                        vehicleImage2 = vehicleImage2,
                        vehicleImage3 = vehicleImage3,
                        vehicleImage4 = vehicleImage4,
                        vehicleImage5 = vehicleImage5
                    )

                if (result.isSuccessful) {

                    val body = result.body()

                    if (body?.success == true) {

                        _uploadResult.value = body

                    } else {

                        _error.value =
                            body?.message
                                ?: "Upload failed"
                    }

                } else {

                    _error.value =
                        "Server error: ${result.code()}"

                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Upload failed"

            } finally {

                _isLoading.value = false

            }
        }
    }
}