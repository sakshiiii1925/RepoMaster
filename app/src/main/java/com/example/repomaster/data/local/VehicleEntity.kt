package com.example.repomaster.data.local



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(

    @PrimaryKey
    val vehicleNumber: String,

    val repoYear: String?,
    val repoMonth: String?,
    val loanNumber: String?,

    val agencyMobile: String?,
    val agencyIdGiveByFinance: String?,
    val agencyMobile2: String?,
    val agencyName: String?,
    val agencyManager: String?,

    val chassisNumber: String?,
    val color: String?,
    val manufactureName: String?,
    val vehicleMake: String?,
    val engineNumber: String?,
    val model: String?,

    val ownerMobile: String?,
    val ownerName: String?,
    val customerAddress: String?,
    val customerArea: String?,

    val branch: String?,
    val area: String?,

    val vehicleType: String?,
    val repoStatus: String?,
    val allocationDpd: String?,

    val executiveName: String?,

    val areaManagerName: String?,
    val areaManagerMobileNo: String?,
    val areaManagerEmailId: String?,

    val contactName2: String?,
    val contactName2Designation: String?,
    val contactName2MobileNo: String?,

    val regionManagerName: String?,
    val regionManagerMobileNo: String?,
    val regionManagerEmailId: String?,

    val finance: String?,
    val agencyId: String?,
    val refLetter: String?,

    val totalCharges: Double?,

    val uploadBy: String?,
    val uploadDate: String?,

    val yardId: Long?,
    val yardName: String?
)