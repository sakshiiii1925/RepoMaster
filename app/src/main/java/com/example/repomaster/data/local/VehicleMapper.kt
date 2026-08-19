package com.example.repomaster.data.local


import com.example.repomaster.models.Vehicle
import com.example.repomaster.models.VehicleId

fun Vehicle.toEntity(): VehicleEntity {

    return VehicleEntity(

        vehicleNumber = vehicleNumber ?: "",

        repoYear = id?.repoYear,
        repoMonth = id?.repoMonth,
        loanNumber = id?.loanNumber,

        agencyMobile = agencyMobile,
        agencyIdGiveByFinance = agencyIdGiveByFinance,
        agencyMobile2 = agencyMobile2,
        agencyName = agencyName,
        agencyManager = agencyManager,

        chassisNumber = chassisNumber,
        color = color,
        manufactureName = manufactureName,
        vehicleMake = vehicleMake,
        engineNumber = engineNumber,
        model = model,

        ownerMobile = ownerMobile,
        ownerName = ownerName,
        customerAddress = customerAddress,
        customerArea = customerArea,

        branch = branch,
        area = area,

        vehicleType = vehicleType,
        repoStatus = repoStatus,
        allocationDpd = allocationDpd,

        executiveName = executiveName,

        areaManagerName = areaManagerName,
        areaManagerMobileNo = areaManagerMobileNo,
        areaManagerEmailId = areaManagerEmailId,

        contactName2 = contactName2,
        contactName2Designation = contactName2Designation,
        contactName2MobileNo = contactName2MobileNo,

        regionManagerName = regionManagerName,
        regionManagerMobileNo = regionManagerMobileNo,
        regionManagerEmailId = regionManagerEmailId,

        finance = finance,
        agencyId = agencyId,
        refLetter = refLetter,

        totalCharges = totalCharges,

        uploadBy = uploadBy,
        uploadDate = uploadDate,

        yardId = yardId,
        yardName = yardName
    )
}
    fun VehicleEntity.toVehicle(): Vehicle {

        return Vehicle(

            id = VehicleId(
                repoYear = repoYear,
                repoMonth = repoMonth,
                loanNumber = loanNumber
            ),

            vehicleNumber = vehicleNumber,

            agencyMobile = agencyMobile,
            agencyIdGiveByFinance = agencyIdGiveByFinance,
            agencyMobile2 = agencyMobile2,
            agencyName = agencyName,
            agencyManager = agencyManager,

            chassisNumber = chassisNumber,
            color = color,
            manufactureName = manufactureName,
            vehicleMake = vehicleMake,
            engineNumber = engineNumber,
            model = model,

            ownerMobile = ownerMobile,
            ownerName = ownerName,
            customerAddress = customerAddress,
            customerArea = customerArea,

            branch = branch,
            area = area,

            vehicleType = vehicleType,
            repoStatus = repoStatus,
            allocationDpd = allocationDpd,

            executiveName = executiveName,

            areaManagerName = areaManagerName,
            areaManagerMobileNo = areaManagerMobileNo,
            areaManagerEmailId = areaManagerEmailId,

            contactName2 = contactName2,
            contactName2Designation = contactName2Designation,
            contactName2MobileNo = contactName2MobileNo,

            regionManagerName = regionManagerName,
            regionManagerMobileNo = regionManagerMobileNo,
            regionManagerEmailId = regionManagerEmailId,

            finance = finance,
            agencyId = agencyId,
            refLetter = refLetter,

            totalCharges = totalCharges,

            uploadBy = uploadBy,
            uploadDate = uploadDate,

            yardId = yardId,
            yardName = yardName
        )
    }
