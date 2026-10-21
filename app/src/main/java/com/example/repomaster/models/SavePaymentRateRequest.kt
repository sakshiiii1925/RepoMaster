
package com.example.repomaster.models

import com.google.gson.annotations.SerializedName

data class SavePaymentRateRequest(
    val agency_id: String,
    val vehicle_type: String,

    @SerializedName("Repo_init_and_Parked_rate")
    val repo_mark_rate: Double
)
