package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.UploadedImage
import com.example.repomaster.repository.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UploadedImagesViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uploadedImages =
        MutableLiveData<List<UploadedImage>>()

    val uploadedImages: LiveData<List<UploadedImage>> =
        _uploadedImages

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?> =
        _error

    private var loadJob: Job? = null

    // =========================================================
    // LOAD UPLOADED IMAGES
    // =========================================================

    fun loadUploadedImages() {

        loadJob?.cancel()

        loadJob = viewModelScope.launch {

            try {

                val result =
                    repository.getUploadedImages()

                _uploadedImages.value = result

            } catch (e: Exception) {

                _error.value =
                    e.message
                        ?: "Failed to load uploaded images"
            }
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    fun deleteUploadedImage(id: Int) {

        viewModelScope.launch {

            try {

                val success =
                    repository.deleteUploadedImage(id)

                if (success) {

                    loadUploadedImages()

                } else {

                    _error.value =
                        "Failed to delete uploaded images"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message
                        ?: "Failed to delete uploaded images"
            }
        }
    }
}