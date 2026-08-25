package com.example.repomaster.api

import com.example.repomaster.models.RepoImageUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.repomaster.models.DeleteResponse
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import com.example.repomaster.models.UploadedImageDetailsResponse
import com.example.repomaster.models.UploadedImageListResponse

interface RepoImageApi {

    @Multipart
    @POST("api/vehicles/{vehicleNumber}/repo-images")
    suspend fun uploadRepoImages(

        @Path("vehicleNumber")
        vehicleNumber: String,

        @Part("status")
        status: RequestBody,
        @Part("user_email")
        userEmail: RequestBody,
        @Part("user_name")
        userName: RequestBody,

        @Part inventory_image_1:
        MultipartBody.Part,

        @Part inventory_image_2:
        MultipartBody.Part,

        @Part vehicle_image_1:
        MultipartBody.Part,

        @Part vehicle_image_2:
        MultipartBody.Part,

        @Part vehicle_image_3:
        MultipartBody.Part,

        @Part vehicle_image_4:
        MultipartBody.Part,

        @Part vehicle_image_5:
        MultipartBody.Part

    ): Response<RepoImageUploadResponse>
    @GET("api/admin/repo-images")
    suspend fun getUploadedImages(): Response<UploadedImageListResponse>

    @GET("api/admin/repo-images/{id}")
    suspend fun getUploadedImage(
        @Path("id") id: Int
    ): Response<UploadedImageDetailsResponse>
    @DELETE("api/admin/repo-images/{id}")
    suspend fun deleteUploadedImage(
        @Path("id") id: Int
    ): Response<DeleteResponse>
}