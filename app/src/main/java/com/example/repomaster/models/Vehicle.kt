package com.example.repomaster.models

import java.time.LocalDate


data class Vehicle(

    val id: VehicleId?,

    val vehicleNumber:String?,

    val agencyMobile:String?,
   val agencyIdGiveByFinance:String?,
    val agencyMobile2:String?,
    val agencyName:String?,
    val agencyManager:String?,

    val chassisNumber:String?,
    val color:String?,
    val manufactureName:String?,
    val vehicleMake:String?,
    val engineNumber:String?,
    val model:String?,

    val ownerMobile:String?,
    val ownerName:String?,
    val customerAddress:String?,
    val customerArea:String?,

    val branch:String?,
    val area:String?,

    val vehicleType:String?,

    val repoStatus:String?,

    val allocationDpd:String?,


    val executiveName:String?,


    val areaManagerName:String?,
    val areaManagerMobileNo:String?,
    val areaManagerEmailId:String?,


    val contactName2:String?,
    val contactName2Designation:String?,
    val contactName2MobileNo:String?,


    val regionManagerName:String?,
    val regionManagerMobileNo:String?,
    val regionManagerEmailId:String?,


    val finance:String?,


    val agencyId:String?,


    val refLetter:String?,


    val totalCharges:Double?,


    val uploadBy:String?,
    val uploadDate:String?

)



data class VehicleId(

    val repoYear:String?,

    val repoMonth:String?,

    val loanNumber:String?

)

