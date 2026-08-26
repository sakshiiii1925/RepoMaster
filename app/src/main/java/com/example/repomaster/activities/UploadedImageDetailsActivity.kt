package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.repomaster.R
import android.util.Log
import com.example.repomaster.utils.Constants
import com.example.repomaster.viewmodel.UploadedImageDetailsViewModel
import com.example.repomaster.repository.VehicleRepository
import com.google.android.material.appbar.MaterialToolbar

class UploadedImageDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: UploadedImageDetailsViewModel

    private lateinit var progressBar: ProgressBar

    private lateinit var txtVehicleNumber: TextView
    private lateinit var txtUserName: TextView
    private lateinit var txtUserEmail: TextView
    private lateinit var txtStatus: TextView

    private lateinit var imageInventory1: ImageView
    private lateinit var imageInventory2: ImageView

    private lateinit var imageVehicle1: ImageView
    private lateinit var imageVehicle2: ImageView
    private lateinit var imageVehicle3: ImageView
    private lateinit var imageVehicle4: ImageView
    private lateinit var imageVehicle5: ImageView
    private lateinit var toolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_uploaded_image_details
        )

        // -----------------------------------------
        // toolbar
        // -----------------------------------------
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "View Images"


        // -----------------------------------------
        // Views
        // -----------------------------------------

        progressBar =
            findViewById(R.id.progressDetails)

        txtVehicleNumber =
            findViewById(R.id.txtVehicleNumber)

        txtUserName =
            findViewById(R.id.txtUserName)

        txtUserEmail =
            findViewById(R.id.txtUserEmail)

        txtStatus =
            findViewById(R.id.txtStatus)

        imageInventory1 =
            findViewById(R.id.imageInventory1)

        imageInventory2 =
            findViewById(R.id.imageInventory2)

        imageVehicle1 =
            findViewById(R.id.imageVehicle1)

        imageVehicle2 =
            findViewById(R.id.imageVehicle2)

        imageVehicle3 =
            findViewById(R.id.imageVehicle3)

        imageVehicle4 =
            findViewById(R.id.imageVehicle4)

        imageVehicle5 =
            findViewById(R.id.imageVehicle5)

        // -----------------------------------------
        // Get ID
        // -----------------------------------------

        val id =
            intent.getIntExtra(
                "uploaded_image_id",
                -1
            )

        if (id == -1) {

            Toast.makeText(
                this,
                "Invalid uploaded image ID",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        // -----------------------------------------
        // ViewModel
        // -----------------------------------------



        viewModel =
            ViewModelProvider(
                this,

            )[UploadedImageDetailsViewModel::class.java]

        observeDetails()

        // -----------------------------------------
        // Load details
        // -----------------------------------------

        viewModel.loadDetails(id)
    }

    private fun observeDetails() {

        viewModel.details.observe(
            this
        ) { details ->

            progressBar.visibility =
                View.GONE

            if (details == null) {
                return@observe
            }

            txtVehicleNumber.text =
                details.vehicle_number

            txtUserName.text =
                "User: ${
                    details.user_name ?: "Unknown"
                }"

            txtUserEmail.text =
                "Email: ${
                    details.user_email ?: "Unknown"
                }"

            txtStatus.text =
                "Status: ${details.status}"

            // -----------------------------------------
            // Inventory images
            // -----------------------------------------

            loadImage(
                details.inventory_image_1,
                imageInventory1
            )

            loadImage(
                details.inventory_image_2,
                imageInventory2
            )

            // -----------------------------------------
            // Vehicle images
            // -----------------------------------------

            loadImage(
                details.vehicle_image_1,
                imageVehicle1
            )

            loadImage(
                details.vehicle_image_2,
                imageVehicle2
            )

            loadImage(
                details.vehicle_image_3,
                imageVehicle3
            )

            loadImage(
                details.vehicle_image_4,
                imageVehicle4
            )

            loadImage(
                details.vehicle_image_5,
                imageVehicle5
            )
        }

        viewModel.error.observe(
            this
        ) { message ->

            if (!message.isNullOrEmpty()) {

                progressBar.visibility =
                    View.GONE

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadImage(
        imagePath: String?,
        imageView: ImageView
    ) {

        if (imagePath.isNullOrBlank()) {

            imageView.visibility =
                View.GONE

            return
        }

        imageView.visibility =
            View.VISIBLE

        val baseUrl =
            Constants.BASE_URL

        val imageUrl =
            baseUrl + imagePath

        Log.d(
            "IMAGE_URL",
            "Loading image: $imageUrl"
        )

        Glide.with(this)
            .load(imageUrl)
            .placeholder(
                android.R.drawable.ic_menu_gallery
            )
            .error(
                android.R.drawable.ic_dialog_alert
            )
            .into(imageView)
    }
    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }

}