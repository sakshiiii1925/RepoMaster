package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.Vehicle
import com.example.repomaster.repository.StatusSaveResult
import com.example.repomaster.repository.VehicleRepository
import kotlinx.coroutines.launch

class VehicleDetailsViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _vehicle =
        MutableLiveData<Vehicle?>()

    val vehicle: LiveData<Vehicle?> =
        _vehicle

    private val _statusSaveResult =
        MutableLiveData<StatusSaveResult>()

    val statusSaveResult: LiveData<StatusSaveResult> =
        _statusSaveResult

    fun getVehicle(vehicleNumber: String) {

        viewModelScope.launch {

            _vehicle.value =
                repository.getVehicle(vehicleNumber)
        }
    }

    fun updateRepoStatus(
        vehicleNumber: String,
        status: String
    ) {

        viewModelScope.launch {

            val result =
                repository.updateStatus(
                    vehicleNumber,
                    status
                )

            _statusSaveResult.value = result

            // Reload vehicle from Room/API
            getVehicle(vehicleNumber)
        }
    }
}