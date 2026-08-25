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

    fun getVehicle(
        vehicleNumber: String
    ) {

        viewModelScope.launch {

            _vehicle.value =
                repository.getVehicle(
                    vehicleNumber
                )
        }
    }

    fun updateRepoStatus(
        vehicleNumber: String,
        status: String
    ) {

        viewModelScope.launch {

            // =============================================
            // SAVE STATUS
            // =============================================

            val result =
                repository.updateStatus(
                    vehicleNumber,
                    status
                )

            // =============================================
            // IMAGE UPLOAD REQUIRED
            // =============================================

            if (
                status == "repo mark" ||
                status == "Parked"
            ) {

                repository.markImageUploadPending(
                    vehicleNumber,
                    status
                )
            }

            // =============================================
            // RESULT
            // =============================================

            _statusSaveResult.value =
                result

            // =============================================
            // RELOAD VEHICLE
            // =============================================

            _vehicle.value =
                repository.getVehicle(
                    vehicleNumber
                )
        }
    }
}