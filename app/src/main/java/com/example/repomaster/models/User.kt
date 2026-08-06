package com.example.repomaster.models


data class User(

    val id: Long? = null,

    val fullName: String,

    val email: String,

    val mobile: String,

    val password: String,

    val address: String,

    val referenceAdminEmail: String,

    val role: String? = null,

    val status: String? = null,
    val agencyId: String?

)