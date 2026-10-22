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
        status: String,
        loanNumber: String = ""
    ) {
        viewModelScope.launch {
            // Use vehicle number first; otherwise use loan number.
            val identifier = vehicleNumber.trim()
                .ifBlank { loanNumber.trim() }

            if (identifier.isBlank()) {
                _statusSaveResult.value = StatusSaveResult.FAILED
                return@launch
            }

            val result = repository.updateStatus(
                identifier,
                status
            )

            if (
                status == "Repo init and Parked" ||
                status == "Parked"
            ) {
                repository.markImageUploadPending(
                    identifier,
                    status
                )
            }

            _statusSaveResult.value = result

            // Reload using whichever identifier is available.
            _vehicle.value = if (vehicleNumber.isNotBlank()) {
                repository.getVehicle(vehicleNumber.trim())
            } else {
                repository.getVehicleByLoanNumber(loanNumber.trim())
            }
        }
    }




    fun getVehicleByLoanNumber(loanNumber: String) {
        viewModelScope.launch {
            try {
                val result =
                    repository.getVehicleByLoanNumber(loanNumber)

                _vehicle.value = result
            } catch (e: Exception) {
                _vehicle.value = null
            }
        }
    }
}