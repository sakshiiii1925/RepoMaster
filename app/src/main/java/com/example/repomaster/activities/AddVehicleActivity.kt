package com.example.repomaster.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.models.Vehicle
import com.example.repomaster.models.VehicleId
import com.example.repomaster.viewmodel.HomeViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText


class AddVehicleActivity : AppCompatActivity() {


    private lateinit var viewModel: HomeViewModel


    private lateinit var autoStatus: MaterialAutoCompleteTextView


    private lateinit var etVehicleNumber: TextInputEditText
    private lateinit var etOwnerName: TextInputEditText
    private lateinit var etOwnerMobile: TextInputEditText
    private lateinit var etOwnerAddress: TextInputEditText
    private lateinit var etOwnerArea: TextInputEditText
    private lateinit var etCompany: TextInputEditText
    private lateinit var etVehicleMake: TextInputEditText
    private lateinit var etModel: TextInputEditText
    private lateinit var etColor: TextInputEditText

    private lateinit var etEngineNumber: TextInputEditText
    private lateinit var etChassisNumber: TextInputEditText

    private lateinit var etAgencyName: TextInputEditText
    private lateinit var etAgencyManager: TextInputEditText
    private lateinit var etAgencyMobile: TextInputEditText
    private lateinit var etAgencyMobile2: TextInputEditText


    private lateinit var etFinanceCompany: TextInputEditText
    private lateinit var etBranch: TextInputEditText
    private lateinit var etArea: TextInputEditText


    private lateinit var etVehicleType: TextInputEditText
    private lateinit var etAllocationDpd: TextInputEditText

    private lateinit var etExecutiveName: TextInputEditText


    private lateinit var etAreaManagerName: TextInputEditText
    private lateinit var etAreaManagerMobile: TextInputEditText
    private lateinit var etAreaManagerEmail: TextInputEditText


    private lateinit var etRegionManagerName: TextInputEditText
    private lateinit var etRegionManagerMobile: TextInputEditText
    private lateinit var etRegionManagerEmail: TextInputEditText
    private lateinit var etContactName2: TextInputEditText
    private lateinit var etContactName2Designation: TextInputEditText
    private lateinit var etContactName2MobileNo: TextInputEditText

    private lateinit var etAgencyId: TextInputEditText
    private lateinit var etRefLetter: TextInputEditText
    private lateinit var etTotalCharges: TextInputEditText

    private lateinit var etUploadBy: TextInputEditText
    private lateinit var etUploadDate: TextInputEditText


    private lateinit var btnSaveVehicle: MaterialButton

private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)
//toolbar
        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "Add Vehicle"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        // Basic Details

        etVehicleNumber=findViewById(R.id.etVehicleNumber)
        etOwnerName=findViewById(R.id.etOwnerName)
        etOwnerMobile=findViewById(R.id.etOwnerMobile)
        etOwnerAddress=findViewById(R.id.etOwnerAddress)
        etOwnerArea=findViewById(R.id.etOwnerarea)

        etCompany=findViewById(R.id.etCompany)
        etVehicleMake=findViewById(R.id.etVehicleMake)
        etModel=findViewById(R.id.etModel)
        etColor=findViewById(R.id.etColor)

        etEngineNumber=findViewById(R.id.etEngineNumber)
        etChassisNumber=findViewById(R.id.etChassisNumber)



        // Agency

        etAgencyName=findViewById(R.id.etAgencyName)
        etAgencyManager=findViewById(R.id.etAgencyManager)
        etAgencyMobile=findViewById(R.id.etAgencyMobile)
        etAgencyMobile2=findViewById(R.id.etAgencyMobile2)



        // Finance

        etFinanceCompany=findViewById(R.id.etFinanceCompany)
        etBranch=findViewById(R.id.etBranch)
        etArea=findViewById(R.id.etArea)


        // Allocation

        etVehicleType=findViewById(R.id.etVehicleType)
        etAllocationDpd=findViewById(R.id.etAllocationDpd)

        etExecutiveName=findViewById(R.id.etExecutiveName)



        // Managers

        etAreaManagerName=findViewById(R.id.etAreaManagerName)
        etAreaManagerMobile=findViewById(R.id.etAreaManagerMobile)
        etAreaManagerEmail=findViewById(R.id.etAreaManagerEmail)
//second person
        etContactName2 = findViewById(R.id.etContactName2)

        etContactName2Designation =
            findViewById(R.id.etContactName2Designation)

        etContactName2MobileNo =
            findViewById(R.id.etContactName2MobileNo)

        etRegionManagerName=findViewById(R.id.etRegionManagerName)
        etRegionManagerMobile=findViewById(R.id.etRegionManagerMobile)
        etRegionManagerEmail=findViewById(R.id.etRegionManagerEmail)



        // Other

        etAgencyId=findViewById(R.id.etAgencyId)
        etRefLetter=findViewById(R.id.etRefLetter)
        etTotalCharges=findViewById(R.id.etTotalCharges)

        etUploadBy=findViewById(R.id.etUploadBy)
        etUploadDate=findViewById(R.id.etUploadDate)



        autoStatus=findViewById(R.id.autoStatus)



        btnSaveVehicle=findViewById(R.id.btnSaveVehicle)



        val statusList=listOf(
            "Open List",
            "Contacted",
            "On the Way",
            "Parked in Godown",
            "Released"
        )


        autoStatus.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                statusList
            )
        )



        btnSaveVehicle.setOnClickListener {


            val vehicle = Vehicle(

                id = VehicleId(
                    repoYear = null,
                    repoMonth = null,
                    loanNumber = null
                ),

                vehicleNumber =
                    etVehicleNumber.text.toString().trim(),

                ownerName =
                    etOwnerName.text.toString().trim(),

                ownerMobile =
                    etOwnerMobile.text.toString().trim(),
                customerAddress = etOwnerAddress.text.toString().trim(),
                customerArea = etOwnerArea.text.toString().trim(),

                manufactureName =
                    etCompany.text.toString().trim(),

                vehicleMake =
                    etVehicleMake.text.toString().trim(),

                model =
                    etModel.text.toString().trim(),

                color =
                    etColor.text.toString().trim(),


                engineNumber =
                    etEngineNumber.text.toString().trim(),

                chassisNumber =
                    etChassisNumber.text.toString().trim(),



                agencyName =
                    etAgencyName.text.toString().trim(),

                agencyManager =
                    etAgencyManager.text.toString().trim(),

                agencyMobile =
                    etAgencyMobile.text.toString().trim(),

                agencyMobile2 =
                    etAgencyMobile2.text.toString().trim(),



                finance =
                    etFinanceCompany.text.toString().trim(),

                branch =
                    etBranch.text.toString().trim(),

                area =
                    etArea.text.toString().trim(),



                vehicleType =
                    etVehicleType.text.toString().trim(),


                repoStatus =
                    autoStatus.text.toString().trim(),


                allocationDpd =
                    etAllocationDpd.text.toString().trim(),



                executiveName =
                    etExecutiveName.text.toString().trim(),



                areaManagerName =
                    etAreaManagerName.text.toString().trim(),

                areaManagerMobileNo =
                    etAreaManagerMobile.text.toString().trim(),

                areaManagerEmailId =
                    etAreaManagerEmail.text.toString().trim(),



                contactName2 =
                    etContactName2.text.toString().trim(),

                contactName2Designation =
                    etContactName2Designation.text.toString().trim(),

                contactName2MobileNo =
                    etContactName2MobileNo.text.toString().trim(),



                regionManagerName =
                    etRegionManagerName.text.toString().trim(),

                regionManagerMobileNo =
                    etRegionManagerMobile.text.toString().trim(),

                regionManagerEmailId =
                    etRegionManagerEmail.text.toString().trim(),



                agencyId =
                    etAgencyId.text.toString().trim(),


                refLetter =
                    etRefLetter.text.toString().trim(),



                totalCharges =
                    etTotalCharges.text.toString()
                        .toDoubleOrNull(),



                uploadBy =
                    etUploadBy.text.toString().trim(),

agencyIdGiveByFinance = "",
                uploadDate =
                    etUploadDate.text.toString().trim()

            )


            viewModel.addVehicle(vehicle)
                .observe(this){ response ->


                    if(response.isSuccessful){

                        Toast.makeText(
                            this,
                            "Vehicle Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    else{

                        Toast.makeText(
                            this,
                            "Failed To Add Vehicle",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }


        }

    }
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}