package com.example.repomaster.models

data class UploadedImageListResponse(
    val success: Boolean,
    val data: List<UploadedImage>
)