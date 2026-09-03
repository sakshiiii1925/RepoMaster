package com.example.repomaster.repository

import android.content.Context
import com.example.repomaster.api.RepoImageApi
import com.example.repomaster.models.RepoImageUploadResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import com.example.repomaster.utils.SessionManager
import android.util.Log
import com.example.repomaster.data.local.DatabaseProvider
import com.example.repomaster.network.RetrofitClient
class RepoImageRepository(
    private val context: Context
) {

    private val api: RepoImageApi =
        RetrofitClient.repoImageApi


    suspend fun uploadRepoImages(
        vehicleNumber: String,
        status: String,
        userEmail: String,
        userName: String,
        inventoryImage1: File,
        inventoryImage2: File,

        vehicleImage1: File,
        vehicleImage2: File,
        vehicleImage3: File,
        vehicleImage4: File,
        vehicleImage5: File

    ): Response<RepoImageUploadResponse> {

        val textType =
            "text/plain".toMediaType()

        val statusBody =
            status.toRequestBody(textType)
        val userEmailBody =
            userEmail.toRequestBody(textType)
        val userNameBody =
            userName.toRequestBody(textType)

        val inventory1 =
            createImagePart(
                "inventory_image_1",
                inventoryImage1
            )

        val inventory2 =
            createImagePart(
                "inventory_image_2",
                inventoryImage2
            )

        val vehicle1 =
            createImagePart(
                "vehicle_image_1",
                vehicleImage1
            )

        val vehicle2 =
            createImagePart(
                "vehicle_image_2",
                vehicleImage2
            )

        val vehicle3 =
            createImagePart(
                "vehicle_image_3",
                vehicleImage3
            )

        val vehicle4 =
            createImagePart(
                "vehicle_image_4",
                vehicleImage4
            )

        val vehicle5 =
            createImagePart(
                "vehicle_image_5",
                vehicleImage5
            )


        return api.uploadRepoImages(

            vehicleNumber,

            statusBody,
            userEmailBody,
            userNameBody,
            inventory1,
            inventory2,

            vehicle1,
            vehicle2,
            vehicle3,
            vehicle4,
            vehicle5
        )
    }

    private fun createImagePart(
        partName: String,
        file: File
    ): MultipartBody.Part {

        val requestBody =
            file.asRequestBody(
                "image/*".toMediaType()
            )

        return MultipartBody.Part.createFormData(
            partName,
            file.name,
            requestBody
        )
    }
    private val pendingImageUploadDao =
        DatabaseProvider
            .getDatabase(context)
            .pendingImageUploadDao()
    private val sessionManager = SessionManager(context)
    suspend fun markImageUploadCompleted(
        vehicleNumber: String
    ) {

        val number =
            vehicleNumber
                .trim()
                .replace("-", "")
                .replace("/", "")
                .replace(".", "")
                .replace(" ", "")
                .uppercase()
        val agencyId =
            sessionManager
                .getAgencyId()
                .trim()
        if (agencyId.isEmpty()) {
            Log.e(
                "IMAGE_UPLOAD",
                "Cannot mark upload completed: agencyId is empty"
            )
            return
        }

        pendingImageUploadDao.markUploadedByVehicle(
            number,
            agencyId
        )

        Log.d(
            "IMAGE_UPLOAD",
            "Pending upload completed: $number, agencyId=$agencyId"
        )
    }

}