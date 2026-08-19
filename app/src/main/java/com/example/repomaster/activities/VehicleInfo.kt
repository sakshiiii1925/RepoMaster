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

class VehicleInfo : AppCompatActivity() {


    private lateinit var viewModel: VehicleDetailsViewModel
    private lateinit var txtVehicleNo: TextView
    private lateinit var txtOwnerName: TextView
    private lateinit var txtCompany: TextView
    private lateinit var txtVehicleMake: TextView
    private lateinit var txtModel: TextView
    private lateinit var txtColor: TextView
    private lateinit var txtEngine: TextView
    private lateinit var txtChassis: TextView
    private lateinit var txtVehicleType: TextView

    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_vehicle_info)
//toolbar
        toolbar =
            findViewById(R.id.toolbar1)

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
        val vehicleNumber =
            intent.getStringExtra("vehicleNumber") ?: ""


        viewModel.getVehicle(vehicleNumber)



        viewModel.vehicle.observe(this) { vehicle ->


            if (vehicle != null) {

                txtVehicleNo.text = vehicle.vehicleNumber

                txtOwnerName.text = vehicle.ownerName



                txtCompany.text = vehicle.manufactureName
                txtVehicleMake.text = vehicle.vehicleMake
                txtModel.text = vehicle.model
                txtColor.text = vehicle.color
                txtEngine.text = vehicle.engineNumber
                txtChassis.text = vehicle.chassisNumber
                txtVehicleType.text = vehicle.vehicleType


            }


        }
    }

        private fun initializeViews() {


            txtVehicleNo = findViewById(R.id.txtVehicleNo)


            txtOwnerName = findViewById(R.id.txtOwnerName)



            txtCompany = findViewById(R.id.txtCompany)
            txtVehicleMake = findViewById(R.id.txtVehicleMake)
            txtModel = findViewById(R.id.txtModel)
            txtColor = findViewById(R.id.txtColor)
            txtEngine = findViewById(R.id.txtEngine)
            txtChassis = findViewById(R.id.txtChassis)
            txtVehicleType = findViewById(R.id.txtVehicleType)


        }


        override fun onSupportNavigateUp(): Boolean {


            finish()


            return true

        }
    }
