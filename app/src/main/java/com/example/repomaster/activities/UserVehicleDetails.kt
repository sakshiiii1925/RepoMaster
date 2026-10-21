package com.example.repomaster.activities
import com.example.repomaster.utils.NetworkUtils
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import com.example.repomaster.repository.StatusSaveResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.viewmodel.VehicleDetailsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.example.repomaster.repository.VehicleRepository
import com.example.repomaster.viewmodel.VehicleDetailsViewModelFactory
import androidx.appcompat.app.AlertDialog
import com.google.android.material.appbar.MaterialToolbar
import androidx.activity.result.contract.ActivityResultContracts
import com.example.repomaster.models.Yard
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.YardViewModel
import com.google.android.material.card.MaterialCardView

class UserVehicleDetails : AppCompatActivity() {
    private val repoImageUploadLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val status =
                    result.data?.getStringExtra("status")

                if (!status.isNullOrEmpty()) {

                    // Update displayed status
                    updateStatusColor(status)

                    // Set dropdown value
                    autoStatus.setText(
                        status,
                        false
                    )

                    Toast.makeText(
                        this,
                        "Status updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Refresh vehicle details
                    val vehicleNumber =
                        intent.getStringExtra(
                            "vehicleNumber"
                        ) ?: ""

                    if (vehicleNumber.isNotEmpty()) {

                        viewModel.getVehicle(
                            vehicleNumber
                        )
                    }
                }

            } else {

                // User came back without completing upload

                autoStatus.setText(
                    "",
                    false
                )
            }
        }

    private lateinit var viewModel: VehicleDetailsViewModel

    private lateinit var txtVehicleNo: TextView
    private lateinit var txtRepoStatus: TextView

    private lateinit var txtOwnerName: TextView



    private lateinit var txtCompany: TextView
    private lateinit var txtVehicleMake: TextView
    private lateinit var txtModel: TextView
    private lateinit var txtColor: TextView
    private lateinit var txtEngine: TextView
    private lateinit var txtChassis: TextView
    private lateinit var txtVehicleType: TextView


    private lateinit var txtAgencyMobile: TextView
    private lateinit var txtAgencyMobile2: TextView
    private lateinit var txtAgencyId: TextView


private lateinit var callcard: MaterialCardView
    private lateinit var btnCallAgency: TextView
    private lateinit var btnCallAgency2: TextView
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

        setContentView(R.layout.activity_user_vehicle_details)
        //yard
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



//toolbar
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



        initializeViews()
callcard=findViewById(R.id.callcard)
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

                    val userId = SessionManager(this).getUserId()

                    if (userId <= 0) {
                        Toast.makeText(
                            this,
                            "User ID not found. Please log in again.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@setPositiveButton
                    }

                    yardViewModel.assignVehicleToYard(
                        vehicleNumber,
                        newYardId,
                        userId.toLong()
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        val vehicleNumber =
            intent.getStringExtra("vehicleNumber") ?: ""


        viewModel.getVehicle(vehicleNumber)



        viewModel.vehicle.observe(this){ vehicle ->


            if(vehicle != null){

                txtVehicleNo.text = vehicle.vehicleNumber

                txtRepoStatus.text = vehicle.repoStatus


                txtOwnerName.text = vehicle.ownerName



                txtCompany.text = vehicle.manufactureName
                txtVehicleMake.text = vehicle.vehicleMake
                txtModel.text = vehicle.model
                txtColor.text = vehicle.color
                txtEngine.text = vehicle.engineNumber
                txtChassis.text = vehicle.chassisNumber
                txtVehicleType.text = vehicle.vehicleType



                txtAgencyMobile.text = vehicle.agencyMobile
                txtAgencyMobile2.text = vehicle.agencyMobile2
                txtAgencyId.text = vehicle.agencyId

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
        viewModel.statusSaveResult.observe(this) { result ->

            when (result) {

                // =========================================
                // ONLINE
                // =========================================
                StatusSaveResult.SAVED_AND_SYNCED -> {

                    val status =
                        autoStatus.text.toString().trim()

                    updateStatusColor(status)

                    if (
                        status == "Repo init and Parked" ||
                        status == "Parked"
                    ) {

                        openRepoImageUpload(
                            vehicleNumber,
                            status
                        )

                    } else {

                        Toast.makeText(
                            this,
                            "Status Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        viewModel.getVehicle(vehicleNumber)
                    }
                }




                // =========================================
                // OFFLINE
                // =========================================

                StatusSaveResult.SAVED_OFFLINE -> {

                    Toast.makeText(
                        this,
                        "Status saved offline. Images can be uploaded when you are online.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // =========================================
                // FAILED
                // =========================================

                StatusSaveResult.FAILED -> {

                    Toast.makeText(
                        this,
                        "Unable to save status",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                null -> Unit
            }
        }


        btnCallAgency2.setOnClickListener {
            val mobile1 = txtAgencyMobile.text.toString().trim()
            val mobile2 = txtAgencyMobile2.text.toString().trim()

            val numbers = mutableListOf<String>()

            if (mobile1.isNotEmpty()) numbers.add(mobile1)
            if (mobile2.isNotEmpty()) numbers.add(mobile2)

            when (numbers.size) {

                0 -> {
                    Toast.makeText(this, "No agency mobile number available", Toast.LENGTH_SHORT).show()
                }

                1 -> {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${numbers[0]}")
                    startActivity(intent)
                }

                else -> {
                    AlertDialog.Builder(this)
                        .setTitle("Choose Agency Number")
                        .setItems(numbers.toTypedArray()) { _, which ->

                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${numbers[which]}")
                            startActivity(intent)

                        }
                        .show()
                }
            }

        }

        btnCallAgency.setOnClickListener {

            val mobile1 = txtAgencyMobile.text.toString().trim()
            val mobile2 = txtAgencyMobile2.text.toString().trim()

            val numbers = mutableListOf<String>()

            if (mobile1.isNotEmpty()) numbers.add(mobile1)
            if (mobile2.isNotEmpty()) numbers.add(mobile2)

            when (numbers.size) {

                0 -> {
                    Toast.makeText(this, "No agency mobile number available", Toast.LENGTH_SHORT).show()
                }

                1 -> {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${numbers[0]}")
                    startActivity(intent)
                }

                else -> {
                    AlertDialog.Builder(this)
                        .setTitle("Choose Agency Number")
                        .setItems(numbers.toTypedArray()) { _, which ->

                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${numbers[which]}")
                            startActivity(intent)

                        }
                        .show()
                }
            }

    }
        callcard.setOnClickListener {
            val mobile1 = txtAgencyMobile.text.toString().trim()
            val mobile2 = txtAgencyMobile2.text.toString().trim()

            val numbers = mutableListOf<String>()

            if (mobile1.isNotEmpty()) numbers.add(mobile1)
            if (mobile2.isNotEmpty()) numbers.add(mobile2)

            when (numbers.size) {

                0 -> {
                    Toast.makeText(this, "No agency mobile number available", Toast.LENGTH_SHORT).show()
                }

                1 -> {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${numbers[0]}")
                    startActivity(intent)
                }

                else -> {
                    AlertDialog.Builder(this)
                        .setTitle("Choose Agency Number")
                        .setItems(numbers.toTypedArray()) { _, which ->

                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${numbers[which]}")
                            startActivity(intent)

                        }
                        .show()
                }
            }
        }
        btnSaveStatus.setOnClickListener {

            val status =
                autoStatus.text.toString().trim()

            if (status.isEmpty()) {

                autoStatus.error =
                    "Select Status"

                return@setOnClickListener
            }

            viewModel.updateRepoStatus(
                vehicleNumber,
                status
            )
        }



    }

    private fun openRepoImageUpload(
        vehicleNumber: String,
        status: String
    ) {

        val intent =
            Intent(
                this,
                RepoImageUploadActivity::class.java
            )

        intent.putExtra(
            "vehicleNumber",
            vehicleNumber
        )

        intent.putExtra(
            "status",
            status
        )

        repoImageUploadLauncher.launch(intent)
    }

    private fun initializeViews(){


        txtVehicleNo=findViewById(R.id.txtVehicleNo)
        txtRepoStatus=findViewById(R.id.txtRepoStatus)


        txtOwnerName=findViewById(R.id.txtOwnerName)



        txtCompany=findViewById(R.id.txtCompany)
        txtVehicleMake=findViewById(R.id.txtVehicleMake)
        txtModel=findViewById(R.id.txtModel)
        txtColor=findViewById(R.id.txtColor)
        txtEngine=findViewById(R.id.txtEngine)
        txtChassis=findViewById(R.id.txtChassis)
        txtVehicleType=findViewById(R.id.txtVehicleType)



        txtAgencyMobile=findViewById(R.id.txtAgencyMobile)
        txtAgencyMobile2=findViewById(R.id.txtAgencyMobile2)
        txtAgencyId=findViewById(R.id.txtAgencyId)



        btnCallAgency=findViewById(R.id.btncall)
        btnCallAgency2=findViewById(R.id.btncall2)
        autoStatus=findViewById(R.id.autoStatus)
        btnSaveStatus=findViewById(R.id.btnSaveStatus)
        btnAssignYard =
            findViewById(R.id.btnAssignYard)
        txtAssignedYard =
            findViewById(R.id.txtAssignedYard)
    }
    private fun setupStatusDropdown() {

        val statusList = listOf(
            "open list",
            "Repo init and Parked",
            "Released"
        )

        autoStatus.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                statusList
            )
        )

        autoStatus.setOnItemClickListener { _, _, position, _ ->

            val selectedStatus =
                statusList[position]

            val vehicleNumber =
                intent.getStringExtra("vehicleNumber")
                    ?: ""

            if (vehicleNumber.isEmpty()) {

                Toast.makeText(
                    this,
                    "Vehicle number not found",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnItemClickListener
            }

            // =========================================
            // Repo init and Parked / PARKED
            // =========================================

            if (
                selectedStatus == "Repo init and Parked" ||
                selectedStatus == "Parked"
            ) {

                if (NetworkUtils.isInternetAvailable(this)) {

                    // -------------------------------------
                    // ONLINE
                    // -------------------------------------
                    // Save status first.
                    // After successful status save,
                    // open image upload screen.
                    // -------------------------------------

                    viewModel.updateRepoStatus(
                        vehicleNumber,
                        selectedStatus
                    )

                } else {

                    // -------------------------------------
                    // OFFLINE
                    // -------------------------------------
                    // Save status locally.
                    // Do NOT open image upload screen.
                    // Add vehicle to pending image list.
                    // -------------------------------------

                    viewModel.updateRepoStatus(
                        vehicleNumber,
                        selectedStatus
                    )
                }

            } else {

                // =========================================
                // NORMAL STATUS
                // =========================================

                viewModel.updateRepoStatus(
                    vehicleNumber,
                    selectedStatus
                )
            }
        }
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




            "Repo init and Parked" -> {

                txtRepoStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_on_way
                    )
                )

            }





            "Released" -> {

                txtRepoStatus.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_parked
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