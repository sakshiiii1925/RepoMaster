package com.example.repomaster.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.Yard
import com.example.repomaster.network.RetrofitClient
import com.example.repomaster.repository.YardRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import okhttp3.ResponseBody
import com.example.repomaster.models.Vehicle
class YardViewModel : ViewModel() {

    private val repository =
        YardRepository(RetrofitClient.yardApi)

    // =========================
    // Get All Yards
    // =========================

    private val _yards =
        MutableLiveData<Response<List<Yard>>>()

    val yards: LiveData<Response<List<Yard>>> =
        _yards

    fun getYards(agencyId: String) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getYards(agencyId)

                _yards.postValue(response)

            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }

    // =========================
    // Get Single Yard
    // =========================

    private val _yard =
        MutableLiveData<Response<Yard>>()

    val yard: LiveData<Response<Yard>> =
        _yard

    fun getYard(
        id: Long,
        agencyId: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getYard(
                        id,
                        agencyId
                    )

                _yard.postValue(response)

            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }

    // =========================
    // Add Yard
    // =========================

    private val _addYard =
        MutableLiveData<Response<Yard>>()

    val addYardResponse:
            LiveData<Response<Yard>> =
        _addYard

    fun addYard(yard: Yard) {

        viewModelScope.launch {

            try {

                val response =
                    repository.addYard(yard)

                _addYard.postValue(response)

            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }

    // =========================
    // Update Yard
    // =========================

    private val _updateYard =
        MutableLiveData<Response<Yard>>()

    val updateYardResponse:
            LiveData<Response<Yard>> =
        _updateYard

    fun updateYard(
        id: Long,
        agencyId: String,
        yard: Yard
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.updateYard(
                        id,
                        agencyId,
                        yard
                    )

                _updateYard.postValue(response)

            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }

    // =========================
    // Delete Yard
    // =========================

    // =========================
// Delete Yard
// =========================

    private val _deleteYard =
        MutableLiveData<Response<Void>>()

    val deleteYardResponse:
            LiveData<Response<Void>> =
        _deleteYard

    fun deleteYard(
        id: Long,
        agencyId: String
    ) {
        viewModelScope.launch {

            try {

                val response =
                    repository.deleteYard(
                        id,
                        agencyId
                    )

                _deleteYard.postValue(response)

            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }
    // =========================
// Assign Vehicle to Yard
// =========================

    // =========================
// Assign Vehicle to Yard
// =========================

    private val _assignVehicleToYard =
        MutableLiveData<Response<ResponseBody>>()

    val assignVehicleToYardResponse:
            LiveData<Response<ResponseBody>> =
        _assignVehicleToYard

    private val _assignYardError =
        MutableLiveData<String>()

    val assignYardError:
            LiveData<String> =
        _assignYardError

    fun assignVehicleToYard(
        vehicleNumber: String,
        yardId: Long
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.assignVehicleToYard(
                        vehicleNumber,
                        yardId
                    )

                _assignVehicleToYard.postValue(response)

            } catch (e: Exception) {

                e.printStackTrace()

                _assignYardError.postValue(
                    e.message ?: "Assignment failed"
                )
            }
        }
    }
// =========================
// Get Vehicles By Yard
// =========================

    private val _yardVehicles =
        MutableLiveData<Response<List<Vehicle>>>()

    val yardVehicles:
            LiveData<Response<List<Vehicle>>> =
        _yardVehicles

    private val _yardVehiclesError =
        MutableLiveData<String>()

    val yardVehiclesError:
            LiveData<String> =
        _yardVehiclesError

    fun getVehiclesByYard(
        yardId: Long,
        agencyId: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getVehiclesByYard(
                        yardId,
                        agencyId
                    )

                _yardVehicles.postValue(response)

            } catch (e: Exception) {

                e.printStackTrace()

                _yardVehiclesError.postValue(
                    e.message ?: "Failed to load yard vehicles"
                )
            }
        }
    }
    private val _removeVehicleFromYardResponse =
        MutableLiveData<Response<ResponseBody>>()

    val removeVehicleFromYardResponse:
            LiveData<Response<ResponseBody>>
        get() = _removeVehicleFromYardResponse

    private val _removeVehicleFromYardError =
        MutableLiveData<String>()

    val removeVehicleFromYardError:
            LiveData<String>
        get() = _removeVehicleFromYardError


    fun removeVehicleFromYard(
        vehicleNumber: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.removeVehicleFromYard(
                        vehicleNumber
                    )

                _removeVehicleFromYardResponse.value =
                    response

            } catch (e: Exception) {

                _removeVehicleFromYardError.value =
                    e.message ?: "Failed to remove vehicle from yard"
            }
        }
    }
    private val _yardExcelResponse =
        MutableLiveData<Response<ResponseBody>>()

    val yardExcelResponse:
            LiveData<Response<ResponseBody>>
        get() = _yardExcelResponse

    private val _yardExcelError =
        MutableLiveData<String>()

    val yardExcelError:
            LiveData<String>
        get() = _yardExcelError

    fun downloadYardExcel(
        yardId: Long,
        agencyId: String,
        status: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    repository.downloadYardExcel(
                        yardId,
                        agencyId,
                        status
                    )

                _yardExcelResponse.postValue(
                    response
                )

            } catch (e: Exception) {

                _yardExcelError.postValue(
                    e.message
                        ?: "Failed to download yard Excel"
                )
            }
        }
    }
}