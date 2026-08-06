package com.example.repomaster.models

data class ReportSummary(

    val totalVehicles: Long,

    val openlist: Long,

    val contacted: Long,

    val repoMark: Long,

    val parked: Long,

    val released: Long

)