
package com.example.repomaster.models

import com.google.gson.annotations.SerializedName

data class financeReport(
    val finance: String,
    val branch: String,
    val totalVehicles: Long,

    @SerializedName("repoInitAndParkedCount")
    val RepoInitAndParkedCount: Long,

    val releasedCount: Long
)
