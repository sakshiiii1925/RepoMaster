package com.example.repomaster.repository



import com.example.repomaster.api.YardApi
import com.example.repomaster.models.Yard
import retrofit2.Response
import okhttp3.ResponseBody
import com.example.repomaster.models.Vehicle
class YardRepository(
    private val yardApi: YardApi
) {

    suspend fun getYards(
        agencyId: String
    ): Response<List<Yard>> {

        return yardApi.getYards(agencyId)
    }

    suspend fun getYard(
        id: Long,
        agencyId: String
    ): Response<Yard> {

        return yardApi.getYard(
            id,
            agencyId
        )
    }

    suspend fun addYard(
        yard: Yard
    ): Response<Yard> {

        return yardApi.addYard(yard)
    }

    suspend fun updateYard(
        id: Long,
        agencyId: String,
        yard: Yard
    ): Response<Yard> {

        return yardApi.updateYard(
            id,
            agencyId,
            yard
        )
    }

    suspend fun deleteYard(
        id: Long,
        agencyId: String
    ): Response<Void> {

        return yardApi.deleteYard(
            id,
            agencyId
        )
    }
    // Assign vehicle to yard
    suspend fun assignVehicleToYard(
        vehicleNumber: String,
        yardId: Long
    ): Response<ResponseBody> {

        return yardApi.assignVehicleToYard(
            vehicleNumber,
            yardId
        )
    }
    // Get vehicles assigned to a yard
    suspend fun getVehiclesByYard(
        yardId: Long,
        agencyId: String
    ): Response<List<Vehicle>> {

        return yardApi.getVehiclesByYard(
            yardId,
            agencyId
        )
    }
    suspend fun removeVehicleFromYard(
        vehicleNumber: String
    ): Response<ResponseBody> {

        return yardApi.removeVehicleFromYard(
            vehicleNumber
        )
    }
    suspend fun downloadYardExcel(
        yardId: Long,
        agencyId: String
    ): Response<ResponseBody> {

        return yardApi.downloadYardExcel(
            yardId,
            agencyId
        )
    }
}