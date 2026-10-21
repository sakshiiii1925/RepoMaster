
package com.example.repomaster.models

import com.google.gson.annotations.SerializedName

data class UserActivityReport(
    val userName: String,
    val userEmail: String,
    val agencyId: String,
    val totalSearches: Long,

    @SerializedName("repoInitAndParkedCount")
    val RepoInitAndParkedCount: Long,

    val contactedCount: Long = 0,
    val onTheWayCount: Long = 0,
    val releasedCount: Long,
    val lastSearchTime: String?
)
