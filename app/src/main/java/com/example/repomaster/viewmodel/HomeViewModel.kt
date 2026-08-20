package com.example.repomaster.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repomaster.models.Vehicle
import com.example.repomaster.repository.VehicleRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.liveData
import okhttp3.MultipartBody
import com.example.repomaster.repository.StatusSaveResult
import okhttp3.ResponseBody
import retrofit2.Response
import com.example.repomaster.network.RetrofitClient
import okhttp3.RequestBody
class HomeViewModel(
    private val appContext: android.content.Context
) : ViewModel() {

    private val repository =
        VehicleRepository(appContext.applicationContext)
    val vehicle = MutableLiveData<Vehicle?>()
    val statusUpdated = MutableLiveData<Boolean>()

    fun searchVehicle(vehicleNumber: String) {

        viewModelScope.launch {

            vehicle.value = repository.searchVehicle(vehicleNumber)

        }

    }
    private val _statusSaveResult =
        MutableLiveData<StatusSaveResult>()

    val statusSaveResult: LiveData<StatusSaveResult> =
        _statusSaveResult
    fun updateVehicleStatus(
        vehicleNumber: String,
        status: String
    ) {

        viewModelScope.launch {

            _statusSaveResult.value =
                repository.updateStatus(
                    vehicleNumber,
                    status
                )
        }
    }
    fun addVehicle(vehicle: Vehicle) = liveData {
        emit(repository.addVehicle(vehicle))
    }
    val vehicleList = MutableLiveData<List<Vehicle>>()

    fun getAllVehicles(agencyId: String) {

        viewModelScope.launch {

            vehicleList.value = repository.getAllVehicles(agencyId)

        }
    }

    val deleteSuccess = MutableLiveData<Boolean>()
    fun deleteVehicle(vehicleNumber: String) {

        viewModelScope.launch {

            deleteSuccess.value = repository.deleteVehicle(vehicleNumber)

        }
    }
    fun uploadExcel(
        file: MultipartBody.Part,
        agencyId: String
    ) = liveData {

        emit(
            repository.uploadExcel(
                file,
                agencyId
            )
        )
    }
    fun searchVehicles(keyword: String) = liveData {
        emit(repository.searchVehicles(keyword))
    }
    fun saveSearchHistory(
        vehicleNumber: String,
        userEmail: String,
        userName: String,
        agencyId: String
    ) = liveData {
        emit(
            repository.saveSearchHistory(
                vehicleNumber,
                userEmail,
                userName,
                agencyId
            )
        )
    }


    fun getAllSearchHistory() = liveData {
        emit(repository.getAllSearchHistory())
    }
    fun searchHistoryByVehicle(
        agencyId: String,
        vehicleNumber: String
    ) = liveData {
        emit(repository.searchHistoryByVehicle(agencyId, vehicleNumber))
    }

    fun sortSearchHistory(
        agencyId: String,
        order: String
    ) = liveData {
        emit(repository.sortSearchHistory(agencyId, order))
    }

    fun filterByUser(
        agencyId: String,
        userName: String
    ) = liveData {
        emit(repository.filterByUser(agencyId, userName))
    }

    fun filterByDate(
        agencyId: String,
        date: String
    ) = liveData {
        emit(repository.filterByDate(agencyId, date))
    }
    fun getSearchHistory(
        agencyId: String
    ) = liveData {

        emit(repository.getSearchHistory(agencyId))

    }
    fun syncVehicles(agencyId: String) {

        viewModelScope.launch {

            repository.syncAllVehicles(agencyId)

        }
    }
}