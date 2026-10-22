
package com.example.repomaster.models

data class VehicleDeleteKey(
    val repoYear: String,
    val repoMonth: String,
    val loanNumber: String
)

data class BulkDeleteVehicleRequest(
    val records: List<VehicleDeleteKey>
)
