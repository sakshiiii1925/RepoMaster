package com.example.repomaster.repository

import com.example.repomaster.models.StatusUpdateRequest
import com.example.repomaster.models.Vehicle
import com.example.repomaster.network.RetrofitClient
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import com.example.repomaster.network.RetrofitClient.api
import android.util.Log
import com.example.repomaster.models.UploadResponse
import okhttp3.RequestBody


class VehicleRepository {

        private val api =
            RetrofitClient.api



        suspend fun getVehicle(
            vehicleNumber:String
        ):Vehicle?{


            val response =
                api.getVehicle(vehicleNumber)


            if(response.isSuccessful){

                return response.body()

            }


            return null

        }


    suspend fun searchVehicle(vehicleNumber: String): Vehicle? {

        val response = RetrofitClient.api.getVehicle(vehicleNumber)

        Log.d("API", "Code = ${response.code()}")

        return when {
            response.isSuccessful -> response.body()
            response.code() == 404 -> null
            else -> null
        }
    }
    suspend fun updateStatus(
        vehicleNumber:String,
        status:String
    ):Boolean {

        val response = RetrofitClient.api.updateStatus(
            vehicleNumber,
            StatusUpdateRequest(status)
        )

        return response.isSuccessful
    }
    suspend fun addVehicle(vehicle: Vehicle): Response<Vehicle> {
        return RetrofitClient.api.addVehicle(vehicle)
    }
    suspend fun getAllVehicles(
        agencyId: String
    ): List<Vehicle> {

        val response = RetrofitClient.api.getAllVehicles(agencyId)

        return if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            emptyList()
        }
    }

    suspend fun deleteVehicle(vehicleNumber: String): Boolean {

        val response = RetrofitClient.api.deleteVehicle(vehicleNumber)

        return response.isSuccessful
    }


    suspend fun uploadExcel(
        file: MultipartBody.Part,
        agencyId: RequestBody
    ): Response<UploadResponse> {

        return api.uploadExcel(file, agencyId)
    }
    suspend fun searchVehicles(keyword: String): Response<List<Vehicle>> {
        return RetrofitClient.api.searchVehicles(keyword)
    }
    suspend fun saveSearchHistory(
        vehicleNumber: String,
        userEmail: String,
        userName: String,
        agencyId: String
    ) =
        RetrofitClient.api.saveSearchHistory(
            vehicleNumber,
            userEmail,
            userName,
            agencyId
        )
    suspend fun getSearchHistory(
        userEmail: String
    ) = RetrofitClient.api.getSearchHistory(userEmail)
    suspend fun getAllSearchHistory() =
        RetrofitClient.api.getAllSearchHistory()
    suspend fun searchHistoryByVehicle(
        agencyId: String,
        vehicleNumber: String
    ) =
        RetrofitClient.api.searchHistoryByVehicle(
            agencyId,
            vehicleNumber
        )

    suspend fun sortSearchHistory(
        agencyId: String,
        order: String
    ) =
        RetrofitClient.api.sortSearchHistory(
            agencyId,
            order
        )

    suspend fun filterByUser(
        agencyId: String,
        userName: String
    ) =
        RetrofitClient.api.filterByUser(
            agencyId,
            userName
        )

    suspend fun filterByDate(
        agencyId: String,
        date: String
    ) =
        RetrofitClient.api.filterByDate(
            agencyId,
            date
        )
}