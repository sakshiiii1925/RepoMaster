package com.example.repomaster.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.viewmodel.VehicleDetailsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.example.repomaster.repository.VehicleRepository
import com.example.repomaster.viewmodel.VehicleDetailsViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.repomaster.models.Yard
import com.example.repomaster.viewmodel.YardViewModel
import com.example.repomaster.utils.SessionManager

class VehicleDetailsActivity : AppCompatActivity() {


    private lateinit var viewModel: VehicleDetailsViewModel

    private lateinit var txtVehicleNo: TextView
    private lateinit var txtRepoStatus: TextView

    private lateinit var txtOwnerName: TextView
    private lateinit var txtOwnerMobile: TextView
    private lateinit var txtOwnerAddress: TextView
    private lateinit var txtOwnerarea: TextView

    private lateinit var txtCompany: TextView
    private lateinit var txtVehicleMake: TextView
    private lateinit var txtModel: TextView
    private lateinit var txtColor: TextView
    private lateinit var txtEngine: TextView
    private lateinit var txtChassis: TextView
    private lateinit var txtVehicleType: TextView


    private lateinit var txtLoanNumber: TextView
    private lateinit var txtRepoYear: TextView
    private lateinit var txtRepoMonth: TextView
    private lateinit var txtAllocationDpd: TextView


    private lateinit var txtFinanceCompany: TextView

    private lateinit var txtBranch: TextView
    private lateinit var txtArea: TextView


    private lateinit var txtAgency: TextView
    private lateinit var txtAgencyManager: TextView
    private lateinit var txtAgencyMobile: TextView
    private lateinit var txtAgencyMobile2: TextView
    private lateinit var txtAgencyId: TextView
    private lateinit var txtFinanceAgencyId: TextView


    private lateinit var txtExecutiveName: TextView

    private lateinit var txtAreaManagerName: TextView
    private lateinit var txtAreaManagerMobile: TextView
    private lateinit var txtAreaManagerEmail: TextView


    private lateinit var txtContactName: TextView
    private lateinit var txtContactDesignation: TextView
    private lateinit var txtContactMobile: TextView


    private lateinit var txtRegionManagerName: TextView
    private lateinit var txtRegionManagerMobile: TextView
    private lateinit var txtRegionManagerEmail: TextView


    private lateinit var txtTotalCharges: TextView
    private lateinit var txtRefLetter: TextView

    private lateinit var txtUploadBy: TextView
    private lateinit var txtUploadDate: TextView


    private lateinit var callregionmanager: TextView
    private lateinit var callareamanager: TextView
    private lateinit var callcontactmanager: TextView
    private lateinit var autoStatus: MaterialAutoCompleteTextView
    private lateinit var btnSaveStatus: MaterialButton
    private lateinit var autoYard: AutoCompleteTextView

    private lateinit var yardViewModel: YardViewModel

    private var yardList: List<Yard> = emptyList()

    private lateinit var btnAssignYard: MaterialButton
    private var selectedYard: Yard? = null
    private var currentYard: Yard? = null
    private lateinit var txtAssignedYard: TextView
private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_vehicle_details)
        initializeViews()
        autoYard = findViewById(R.id.autoYard)

        yardViewModel =
            ViewModelProvider(this)[YardViewModel::class.java]
        yardViewModel.assignVehicleToYardResponse.observe(this) { response ->

            if (response.isSuccessful) {

                val assignedYardName =
                    selectedYard?.yardName ?: "Selected Yard"

                val wasReassignment =
                    currentYard != null

                txtAssignedYard.text =
                    "Current Yard: $assignedYardName"

                Toast.makeText(
                    this,
                    if (wasReassignment)
                        "Vehicle reassigned to $assignedYardName"
                    else
                        "Vehicle assigned to $assignedYardName",
                    Toast.LENGTH_LONG
                ).show()

                // Update current yard
                currentYard = selectedYard

                // Clear selection
                selectedYard = null
                autoYard.setText("")

            } else {

                Toast.makeText(
                    this,
                    "Assignment failed: ${response.code()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        yardViewModel.assignYardError.observe(this) { error ->

            Toast.makeText(
                this,
                "Assignment error: $error",
                Toast.LENGTH_LONG
            ).show()
        }
        //load yards
        val agencyId = SessionManager(this).getAgencyId()

        if (!agencyId.isNullOrEmpty()) {

            yardViewModel.getYards(agencyId)

        } else {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()
        }
        //observe Yard List
        yardViewModel.yards.observe(this) { response ->

            if (response.isSuccessful) {

                yardList = response.body() ?: emptyList()

                val yardNames =
                    yardList.map { it.yardName }

                val adapter =
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        yardNames
                    )

                autoYard.setAdapter(adapter)

            } else {

                Toast.makeText(
                    this,
                    "Failed to load yards",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //assign Yard
        yardViewModel.yard.observe(this) { response ->

            if (response.isSuccessful) {

                val yard = response.body()

                if (yard != null) {

                    currentYard = yard

                    txtAssignedYard.text =
                        "Current Yard: ${yard.yardName}"

                } else {

                    currentYard = null

                    txtAssignedYard.text =
                        "Current Yard: Not Assigned"
                }

            } else {

                currentYard = null

                txtAssignedYard.text =
                    "Current Yard: Not Assigned"
            }
        }
        //detect selected yard
        autoYard.setOnItemClickListener { _, _, position, _ ->

            selectedYard = yardList[position]

            Toast.makeText(
                this,
                "Selected Yard: ${selectedYard?.yardName}",
                Toast.LENGTH_SHORT
            ).show()
        }
        btnAssignYard.setOnClickListener {

            val vehicleNumber =
                txtVehicleNo.text.toString().trim()

            if (vehicleNumber.isEmpty()) {

                Toast.makeText(
                    this,
                    "Vehicle number not found",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val yard = selectedYard

            if (yard == null) {

                Toast.makeText(
                    this,
                    "Please select a yard",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val newYardId = yard.id

            if (newYardId == null) {

                Toast.makeText(
                    this,
                    "Invalid yard ID",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // -----------------------------------
            // Check if same yard is selected
            // -----------------------------------

            if (currentYard?.id == newYardId) {

                Toast.makeText(
                    this,
                    "Vehicle is already assigned to ${yard.yardName}",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // -----------------------------------
            // Determine Assign or Reassign
            // -----------------------------------

            val isReassignment =
                currentYard != null

            val dialogTitle =
                if (isReassignment)
                    "Reassign Vehicle"
                else
                    "Assign Vehicle"

            val dialogMessage =
                if (isReassignment) {

                    "Are you sure you want to move vehicle " +
                            "$vehicleNumber from " +
                            "\"${currentYard?.yardName}\" to " +
                            "\"${yard.yardName}\"?"

                } else {

                    "Are you sure you want to assign vehicle " +
                            "$vehicleNumber to " +
                            "\"${yard.yardName}\"?"
                }

            // -----------------------------------
            // Confirmation Dialog
            // -----------------------------------

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton(
                    if (isReassignment) "Reassign" else "Assign"
                ) { _, _ ->

                    yardViewModel.assignVehicleToYard(
                        vehicleNumber,
                        newYardId
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "Vehicle Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val repository = VehicleRepository(applicationContext)

        val factory =
            VehicleDetailsViewModelFactory(repository)


        viewModel =
            ViewModelProvider(
                this,
                factory
            )[VehicleDetailsViewModel::class.java]






        val vehicleNumber =
            intent.getStringExtra("vehicleNumber") ?: ""


        viewModel.getVehicle(vehicleNumber)



        viewModel.vehicle.observe(this){ vehicle ->


            if(vehicle != null){

                txtVehicleNo.text = vehicle.vehicleNumber

                txtRepoStatus.text = vehicle.repoStatus


                txtOwnerName.text = vehicle.ownerName
                txtOwnerMobile.text = vehicle.ownerMobile
                txtOwnerAddress.text=vehicle.customerAddress
                txtOwnerarea.text=vehicle.customerArea


                txtCompany.text = vehicle.manufactureName
                txtVehicleMake.text = vehicle.vehicleMake
                txtModel.text = vehicle.model
                txtColor.text = vehicle.color
                txtEngine.text = vehicle.engineNumber
                txtChassis.text = vehicle.chassisNumber
                txtVehicleType.text = vehicle.vehicleType



                txtLoanNumber.text =
                    vehicle.id?.loanNumber ?: ""


                txtRepoYear.text =
                    vehicle.id?.repoYear?.toString() ?: ""


                txtRepoMonth.text =
                    vehicle.id?.repoMonth?.toString() ?: ""
                txtAllocationDpd.text = vehicle.allocationDpd



                txtFinanceCompany.text = vehicle.finance
                txtBranch.text = vehicle.branch
                txtArea.text = vehicle.area



                txtAgency.text = vehicle.agencyName
                txtAgencyManager.text = vehicle.agencyManager
                txtAgencyMobile.text = vehicle.agencyMobile
                txtAgencyMobile2.text = vehicle.agencyMobile2
                txtAgencyId.text = vehicle.agencyId
                txtFinanceAgencyId.text=vehicle.agencyIdGiveByFinance



                txtExecutiveName.text = vehicle.executiveName

                txtAreaManagerName.text = vehicle.areaManagerName
                txtAreaManagerMobile.text = vehicle.areaManagerMobileNo
                txtAreaManagerEmail.text = vehicle.areaManagerEmailId



                txtContactName.text = vehicle.contactName2
                txtContactDesignation.text = vehicle.contactName2Designation
                txtContactMobile.text = vehicle.contactName2MobileNo



                txtRegionManagerName.text = vehicle.regionManagerName
                txtRegionManagerMobile.text = vehicle.regionManagerMobileNo
                txtRegionManagerEmail.text = vehicle.regionManagerEmailId



                txtTotalCharges.text =
                    vehicle.totalCharges.toString()

                txtRefLetter.text = vehicle.refLetter


                txtUploadBy.text = vehicle.uploadBy
                txtUploadDate.text =
                    vehicle.uploadDate.toString()
                val yardId = vehicle.yardId

                if (yardId != null) {

                    val agencyId =
                        SessionManager(this).getAgencyId()

                    if (!agencyId.isNullOrEmpty()) {

                        yardViewModel.getYard(
                            yardId,
                            agencyId
                        )

                    } else {

                        txtAssignedYard.text =
                            "Assigned Yard: Agency ID not found"
                    }

                } else {

                    txtAssignedYard.text =
                        "Assigned Yard: Not Assigned"
                }

                updateStatusColor(vehicle.repoStatus ?: "Pending")

            }

        }



        setupStatusDropdown()
        viewModel.statusUpdated.observe(this){ success ->

            if(success){

                Toast.makeText(
                    this,
                    "Status Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()

            }else{

                Toast.makeText(
                    this,
                    "Status Update Failed",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

//call region manager
        callregionmanager.setOnClickListener {


            val number =
                txtRegionManagerMobile.text.toString()


            if(number.isNotEmpty()){

                val callIntent =
                    Intent(Intent.ACTION_DIAL)

                callIntent.data =
                    Uri.parse("tel:$number")

                startActivity(callIntent)

            }

        }
        //call area manager
        callareamanager.setOnClickListener {


            val number =
                txtAreaManagerMobile.text.toString()


            if(number.isNotEmpty()){

                val callIntent =
                    Intent(Intent.ACTION_DIAL)

                callIntent.data =
                    Uri.parse("tel:$number")

                startActivity(callIntent)

            }

        }
        //call contact person2
        callcontactmanager.setOnClickListener {


            val number =
                txtContactMobile.text.toString()


            if(number.isNotEmpty()){

                val callIntent =
                    Intent(Intent.ACTION_DIAL)

                callIntent.data =
                    Uri.parse("tel:$number")

                startActivity(callIntent)

            }

        }


        btnSaveStatus.setOnClickListener {


            val status =
                autoStatus.text.toString()


            if(status.isEmpty()){

                autoStatus.error="Select Status"
                return@setOnClickListener

            }


            viewModel.updateRepoStatus(
                vehicleNumber,
                status
            )


        }


    }



    private fun initializeViews(){


        txtVehicleNo=findViewById(R.id.txtVehicleNo)
        txtRepoStatus=findViewById(R.id.txtRepoStatus)


        txtOwnerName=findViewById(R.id.txtOwnerName)
        txtOwnerMobile=findViewById(R.id.txtOwnerMobile)
        txtOwnerAddress=findViewById(R.id.txtOwnerAdd)
        txtOwnerarea=findViewById(R.id.txtOwnerarea)


        txtCompany=findViewById(R.id.txtCompany)
        txtVehicleMake=findViewById(R.id.txtVehicleMake)
        txtModel=findViewById(R.id.txtModel)
        txtColor=findViewById(R.id.txtColor)
        txtEngine=findViewById(R.id.txtEngine)
        txtChassis=findViewById(R.id.txtChassis)
        txtVehicleType=findViewById(R.id.txtVehicleType)


        txtLoanNumber=findViewById(R.id.txtLoanNumber)
        txtRepoYear=findViewById(R.id.txtRepoYear)
        txtRepoMonth=findViewById(R.id.txtRepoMonth)
        txtAllocationDpd=findViewById(R.id.txtAllocationDpd)


        txtFinanceCompany=findViewById(R.id.txtFinanceCompany)

        txtBranch=findViewById(R.id.txtBranch)
        txtArea=findViewById(R.id.txtArea)


        txtAgency=findViewById(R.id.txtAgency)
        txtAgencyManager=findViewById(R.id.txtAgencyManager)
        txtAgencyMobile=findViewById(R.id.txtAgencyMobile)
        txtAgencyMobile2=findViewById(R.id.txtAgencyMobile2)
        txtAgencyId=findViewById(R.id.txtAgencyId)
        txtFinanceAgencyId=findViewById(R.id.txtfifAgencyId)


        txtExecutiveName=findViewById(R.id.txtExecutiveName)

        txtAreaManagerName=findViewById(R.id.txtAreaManagerName)
        txtAreaManagerMobile=findViewById(R.id.txtAreaManagerMobile)
        callareamanager=findViewById(R.id.areamancall)
        txtAreaManagerEmail=findViewById(R.id.txtAreaManagerEmail)


        txtContactName=findViewById(R.id.txtContactName2)
        txtContactDesignation=findViewById(R.id.txtContactDesignation)
        txtContactMobile=findViewById(R.id.txtContactMobile)
        callcontactmanager=findViewById(R.id.contcall)


        txtRegionManagerName=findViewById(R.id.txtRegionManagerName)
        txtRegionManagerMobile=findViewById(R.id.txtRegionManagerMobile)
        callregionmanager=findViewById(R.id.rgncall)
        txtRegionManagerEmail=findViewById(R.id.txtRegionManagerEmail)



        txtTotalCharges=findViewById(R.id.txtTotalCharges)
        txtRefLetter=findViewById(R.id.txtRefLetter)


        txtUploadBy=findViewById(R.id.txtUploadBy)
        txtUploadDate=findViewById(R.id.txtUploadDate)



        autoStatus=findViewById(R.id.autoStatus)
        btnSaveStatus=findViewById(R.id.btnSaveStatus)
        btnAssignYard =
            findViewById(R.id.btnAssignYard)
        txtAssignedYard =
            findViewById(R.id.txtAssignedYard)
    }



    private fun setupStatusDropdown(){

        val statusList=listOf(
            "open list",
            "Contacted",
            "repo mark",
            "Parked",
            "Released"
        )


        autoStatus.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                statusList
            )
        )

    }



    private fun updateStatusColor(status: String) {

        txtRepoStatus.text = status


        when(status) {

            "open list" -> {

                txtRepoStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_pending
                    )
                )

            }


            "Contacted" -> {

                txtRepoStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_contacted
                    )
                )

            }


            "repo mark" -> {

                txtRepoStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_on_way
                    )
                )

            }


            "Parked" -> {

                txtRepoStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_parked
                    )
                )

            }


            "Released" -> {

                txtRepoStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_released
                    )
                )

            }

        }
    }
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }

}