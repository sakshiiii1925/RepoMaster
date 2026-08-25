package com.example.repomaster.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.UploadedImageDetails
import com.example.repomaster.repository.VehicleRepository
import kotlinx.coroutines.launch

class UploadedImageDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        VehicleRepository(application)

    private val _details =
        MutableLiveData<UploadedImageDetails?>()

    val details:
            LiveData<UploadedImageDetails?> =
        _details

    private val _error =
        MutableLiveData<String>()

    val error:
            LiveData<String> =
        _error

    fun loadDetails(id: Int) {

        viewModelScope.launch {

            try {

                val result =
                    repository.getUploadedImage(id)

                _details.postValue(
                    result
                )

            } catch (e: Exception) {

                _error.postValue(
                    e.message
                        ?: "Failed to load image details"
                )
            }
        }
    }
}