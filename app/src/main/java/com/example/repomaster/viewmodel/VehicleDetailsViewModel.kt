package com.example.repomaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.Vehicle
import com.example.repomaster.repository.VehicleRepository
import kotlinx.coroutines.launch

class VehicleDetailsViewModel(
    private val repository: VehicleRepository
) : ViewModel() {



    private val _vehicle = MutableLiveData<Vehicle?>()
    val vehicle: LiveData<Vehicle?> = _vehicle
    val statusUpdated = MutableLiveData<Boolean>()
    fun getVehicle(vehicleNumber: String) {

        viewModelScope.launch {

            _vehicle.value = repository.getVehicle(vehicleNumber)

        }
    }

    fun updateRepoStatus(
        vehicleNumber: String,
        status: String
    ) {

        viewModelScope.launch {

            val result = repository.updateStatus(
                vehicleNumber,
                status
            )

            statusUpdated.value = result

            if(result){
                getVehicle(vehicleNumber) // refresh vehicle details
            }
        }
    }

}