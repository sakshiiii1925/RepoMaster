package com.example.repomaster.models


data class UploadResponse(

    val totalRows: Int,

    val inserted: Int,

    val updated: Int,

    val failed: Int,

    val errors: List<String>

)