package com.example.repomaster.models
import com.google.gson.annotations.SerializedName

data class MonthlyReport(
    val repoYear: String,
    val repoMonth: String,
    val totalVehicles: Long,

    @SerializedName("repoInitAndParkedCount")
    val RepoInitAndParkedCount: Long,

    val releasedCount: Long
)
