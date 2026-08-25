package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.UploadedImage
import com.example.repomaster.repository.VehicleRepository
import kotlinx.coroutines.launch

class UploadedImagesViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uploadedImages =
        MutableLiveData<List<UploadedImage>>()

    val uploadedImages: LiveData<List<UploadedImage>> =
        _uploadedImages

    private val _error =
        MutableLiveData<String>()

    val error: LiveData<String> =
        _error

    fun loadUploadedImages() {

        viewModelScope.launch {

            try {

                val result =
                    repository.getUploadedImages()

                _uploadedImages.postValue(result)

            } catch (e: Exception) {

                _error.postValue(
                    e.message
                        ?: "Failed to load uploaded images"
                )
            }
        }
    }

    fun deleteUploadedImage(id: Int) {

        viewModelScope.launch {

            try {

                val success =
                    repository.deleteUploadedImage(id)

                if (success) {

                    loadUploadedImages()

                    _error.postValue(
                        "Uploaded images deleted successfully"
                    )
                }

            } catch (e: Exception) {

                _error.postValue(
                    e.message
                        ?: "Failed to delete uploaded images"
                )
            }
        }
    }
}