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
import androidx.appcompat.app.AlertDialog
import com.google.android.material.appbar.MaterialToolbar

class UserVehicleDetails : AppCompatActivity() {


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

    private lateinit var txtAgency: TextView
    private lateinit var txtAgencyManager: TextView
    private lateinit var txtAgencyMobile: TextView
    private lateinit var txtAgencyMobile2: TextView
    private lateinit var txtAgencyId: TextView
    private lateinit var txtFianceAgencyId: TextView


    private lateinit var btnCallAgency: TextView
    private lateinit var btnCallAgency2: TextView
    private lateinit var autoStatus: MaterialAutoCompleteTextView
    private lateinit var btnSaveStatus: MaterialButton

private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_vehicle_details)
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

        val repository = VehicleRepository()

        val factory =
            VehicleDetailsViewModelFactory(repository)


        viewModel =
            ViewModelProvider(
                this,
                factory
            )[VehicleDetailsViewModel::class.java]



        initializeViews()


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


                txtAgency.text = vehicle.agencyName
                txtAgencyManager.text = vehicle.agencyManager
                txtAgencyMobile.text = vehicle.agencyMobile
                txtAgencyMobile2.text = vehicle.agencyMobile2
                txtAgencyId.text = vehicle.agencyId
                txtFianceAgencyId.text=vehicle.agencyIdGiveByFinance




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



        txtCompany=findViewById(R.id.txtCompany)
        txtVehicleMake=findViewById(R.id.txtVehicleMake)
        txtModel=findViewById(R.id.txtModel)
        txtColor=findViewById(R.id.txtColor)
        txtEngine=findViewById(R.id.txtEngine)
        txtChassis=findViewById(R.id.txtChassis)
        txtVehicleType=findViewById(R.id.txtVehicleType)


        txtAgency=findViewById(R.id.txtAgency)
        txtAgencyManager=findViewById(R.id.txtAgencyManager)
        txtAgencyMobile=findViewById(R.id.txtAgencyMobile)
        txtAgencyMobile2=findViewById(R.id.txtAgencyMobile2)
        txtAgencyId=findViewById(R.id.txtAgencyId)
        txtFianceAgencyId=findViewById(R.id.txtfifAgencyId)


        btnCallAgency=findViewById(R.id.btncall)
        btnCallAgency2=findViewById(R.id.btncall2)
        autoStatus=findViewById(R.id.autoStatus)
        btnSaveStatus=findViewById(R.id.btnSaveStatus)

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