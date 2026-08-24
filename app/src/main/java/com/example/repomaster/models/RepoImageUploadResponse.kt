package com.example.repomaster.models

data class RepoImageUploadResponse(
    val success: Boolean,
    val message: String?,
    val vehicle_number: String?,
    val status: String?,
    val images: RepoImagePaths?
)


data class RepoImagePaths(

    val inventory_image_1: String?,
    val inventory_image_2: String?,

    val vehicle_image_1: String?,
    val vehicle_image_2: String?,
    val vehicle_image_3: String?,
    val vehicle_image_4: String?,
    val vehicle_image_5: String?
)