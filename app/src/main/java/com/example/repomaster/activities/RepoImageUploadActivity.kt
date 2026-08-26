package com.example.repomaster.activities
import android.util.Log
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.repository.RepoImageRepository
import com.example.repomaster.viewmodel.RepoImageUploadViewModel
import com.example.repomaster.viewmodel.RepoImageUploadViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import com.example.repomaster.utils.SessionManager

class RepoImageUploadActivity : AppCompatActivity() {

    // ----------------------------------------------------
    // ViewModel
    // ----------------------------------------------------

    private lateinit var viewModel: RepoImageUploadViewModel

    private lateinit var sessionManager: SessionManager
    // ----------------------------------------------------
    // Toolbar / Text
    // ----------------------------------------------------

    private lateinit var toolbar: MaterialToolbar

    private lateinit var txtVehicleNumber: TextView
    private lateinit var txtSelectedStatus: TextView


    // ----------------------------------------------------
    // ImageViews
    // ----------------------------------------------------

    private lateinit var imgInventory1: ImageView
    private lateinit var imgInventory2: ImageView

    private lateinit var imgVehicle1: ImageView
    private lateinit var imgVehicle2: ImageView
    private lateinit var imgVehicle3: ImageView
    private lateinit var imgVehicle4: ImageView
    private lateinit var imgVehicle5: ImageView


    // ----------------------------------------------------
    // Buttons
    // ----------------------------------------------------

    private lateinit var btnSelectInventory: MaterialButton
    private lateinit var btnSelectVehicle: MaterialButton
    private lateinit var btnSubmit: MaterialButton

    private lateinit var progressBar: ProgressBar


    // ----------------------------------------------------
    // Selected Image URIs
    // ----------------------------------------------------

    private var inventoryImage1Uri: Uri? = null
    private var inventoryImage2Uri: Uri? = null

    private var vehicleImage1Uri: Uri? = null
    private var vehicleImage2Uri: Uri? = null
    private var vehicleImage3Uri: Uri? = null
    private var vehicleImage4Uri: Uri? = null
    private var vehicleImage5Uri: Uri? = null


    // ----------------------------------------------------
    // Camera
    // ----------------------------------------------------

    private var cameraUri: Uri? = null

    private var cameraPosition: Int = 0

    /*
        1 = Inventory 1
        2 = Inventory 2

        3 = Vehicle 1
        4 = Vehicle 2
        5 = Vehicle 3
        6 = Vehicle 4
        7 = Vehicle 5
    */


    // ----------------------------------------------------
    // Camera Launcher
    // ----------------------------------------------------

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            android.util.Log.d(
                "CAMERA_TEST",
                "TakePicture result = $success"
            )

            android.util.Log.d(
                "CAMERA_TEST",
                "cameraUri = $cameraUri"
            )

            if (success && cameraUri != null) {

                Toast.makeText(
                    this,
                    "Camera image captured successfully",
                    Toast.LENGTH_SHORT
                ).show()

                setSelectedImage(
                    cameraPosition,
                    cameraUri!!
                )

            } else {

                Toast.makeText(
                    this,
                    "Camera image was not captured",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                // Permission granted → open camera
                openCamera(cameraPosition)

            } else {

                Toast.makeText(
                    this,
                    "Camera permission is required to take photos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // ----------------------------------------------------
    // Inventory Gallery
    // ----------------------------------------------------

    private val inventoryGalleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            if (uris.isEmpty()) {
                return@registerForActivityResult
            }


            if (uris.size != 2) {

                Toast.makeText(
                    this,
                    "Please select exactly 2 inventory images",
                    Toast.LENGTH_LONG
                ).show()

                return@registerForActivityResult
            }


            inventoryImage1Uri = uris[0]
            inventoryImage2Uri = uris[1]


            imgInventory1.setImageURI(
                inventoryImage1Uri
            )

            imgInventory2.setImageURI(
                inventoryImage2Uri
            )


            updateInventoryButtonText()
        }


    // ----------------------------------------------------
    // Vehicle Gallery
    // ----------------------------------------------------

    private val vehicleGalleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            if (uris.isEmpty()) {
                return@registerForActivityResult
            }


            if (uris.size != 5) {

                Toast.makeText(
                    this,
                    "Please select exactly 5 vehicle images",
                    Toast.LENGTH_LONG
                ).show()

                return@registerForActivityResult
            }


            vehicleImage1Uri = uris[0]
            vehicleImage2Uri = uris[1]
            vehicleImage3Uri = uris[2]
            vehicleImage4Uri = uris[3]
            vehicleImage5Uri = uris[4]


            imgVehicle1.setImageURI(
                vehicleImage1Uri
            )

            imgVehicle2.setImageURI(
                vehicleImage2Uri
            )

            imgVehicle3.setImageURI(
                vehicleImage3Uri
            )

            imgVehicle4.setImageURI(
                vehicleImage4Uri
            )

            imgVehicle5.setImageURI(
                vehicleImage5Uri
            )


            updateVehicleButtonText()
        }


    // ====================================================
    // onCreate
    // ====================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_repo_image_upload
        )

        sessionManager =
            SessionManager(this)
        initializeViews()

        setupToolbar()

        setupViewModel()

        setupImageButtons()

        setupSubmitButton()

        observeViewModel()
    }


    // ====================================================
    // Initialize Views
    // ====================================================

    private fun initializeViews() {

        toolbar =
            findViewById(R.id.toolbar)


        txtVehicleNumber =
            findViewById(R.id.txtVehicleNumber)

        txtSelectedStatus =
            findViewById(R.id.txtSelectedStatus)


        // Inventory

        imgInventory1 =
            findViewById(R.id.imgInventory1)

        imgInventory2 =
            findViewById(R.id.imgInventory2)


        // Vehicle

        imgVehicle1 =
            findViewById(R.id.imgVehicle1)

        imgVehicle2 =
            findViewById(R.id.imgVehicle2)

        imgVehicle3 =
            findViewById(R.id.imgVehicle3)

        imgVehicle4 =
            findViewById(R.id.imgVehicle4)

        imgVehicle5 =
            findViewById(R.id.imgVehicle5)


        // Buttons

        btnSelectInventory =
            findViewById(R.id.btnSelectInventory)

        btnSelectVehicle =
            findViewById(R.id.btnSelectVehicle)

        btnSubmit =
            findViewById(R.id.btnSubmit)


        progressBar =
            findViewById(R.id.progressBar)
    }


    // ====================================================
    // Toolbar
    // ====================================================

    private fun setupToolbar() {

        setSupportActionBar(toolbar)

        supportActionBar?.title =
            "Upload Vehicle Images"

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )


        toolbar.setNavigationOnClickListener {

            finish()
        }
    }


    // ====================================================
    // ViewModel
    // ====================================================

    private fun setupViewModel() {

        val repository =
            RepoImageRepository(
                applicationContext
            )


        val factory =
            RepoImageUploadViewModelFactory(
                repository
            )


        viewModel =
            ViewModelProvider(
                this,
                factory
            )[RepoImageUploadViewModel::class.java]


        val vehicleNumber =
            intent.getStringExtra(
                "vehicleNumber"
            ) ?: ""


        val status =
            intent.getStringExtra(
                "status"
            ) ?: ""

        val userName =
            sessionManager.getUserName()

        val userEmail =
            sessionManager.getUserEmail()
        if (userName.isEmpty()) {

            Toast.makeText(
                this,
                "User name not found. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (userEmail.isEmpty()) {

            Toast.makeText(
                this,
                "User email not found. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }
        txtVehicleNumber.text =
            vehicleNumber


        txtSelectedStatus.text =
            status
    }


    // ====================================================
    // Image Buttons
    // ====================================================

    private fun setupImageButtons() {

        btnSelectInventory.setOnClickListener {

            showImageSourceDialog(
                "inventory"
            )
        }


        btnSelectVehicle.setOnClickListener {

            showImageSourceDialog(
                "vehicle"
            )
        }
    }


    // ====================================================
    // Camera / Gallery Dialog
    // ====================================================

    private fun showImageSourceDialog(
        type: String
    ) {

        AlertDialog.Builder(this)
            .setTitle("Upload Images")
            .setItems(
                arrayOf(
                    "Camera",
                    "Gallery"
                )
            ) { _, which ->

                when (which) {

                    0 -> {

                        if (type == "inventory") {

                            openInventoryCamera()

                        } else {

                            openVehicleCamera()
                        }
                    }


                    1 -> {

                        if (type == "inventory") {

                            inventoryGalleryLauncher.launch(
                                "image/*"
                            )

                        } else {

                            vehicleGalleryLauncher.launch(
                                "image/*"
                            )
                        }
                    }
                }
            }
            .show()
    }


    // ====================================================
    // Inventory Camera
    // ====================================================


    private fun openInventoryCamera() {

        val position: Int

        if (inventoryImage1Uri == null) {

            position = 1

        } else if (inventoryImage2Uri == null) {

            position = 2

        } else {

            Toast.makeText(
                this,
                "2 inventory images already selected",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        cameraPosition = position

        checkCameraPermission()
    }


    // ====================================================
    // Vehicle Camera
    // ====================================================

    private fun openVehicleCamera() {

        val position: Int


        position = when {

            vehicleImage1Uri == null -> 3

            vehicleImage2Uri == null -> 4

            vehicleImage3Uri == null -> 5

            vehicleImage4Uri == null -> 6

            vehicleImage5Uri == null -> 7

            else -> {

                Toast.makeText(
                    this,
                    "5 vehicle images already selected",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }


        cameraPosition = position
        checkCameraPermission()
    }
    private fun checkCameraPermission() {

        if (
            androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {

            // Permission already granted
            openCamera(cameraPosition)

        } else {

            // Ask Android for permission
            cameraPermissionLauncher.launch(
                android.Manifest.permission.CAMERA
            )
        }
    }

    // ====================================================
    // Open Camera
    // ====================================================
    private fun openCamera(position: Int) {

        try {

            cameraPosition = position

            val imageFile = File(
                cacheDir,
                "camera_${System.currentTimeMillis()}.jpg"
            )

            cameraUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                imageFile
            )

            Log.d(
                "CAMERA_TEST",
                "Camera URI = $cameraUri"
            )

            val intent = Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE
            )

            intent.putExtra(
                android.provider.MediaStore.EXTRA_OUTPUT,
                cameraUri
            )

            intent.addFlags(
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val resolvedActivities =
                packageManager.queryIntentActivities(
                    intent,
                    android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
                )

            for (resolveInfo in resolvedActivities) {

                grantUriPermission(
                    resolveInfo.activityInfo.packageName,
                    cameraUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            cameraLauncher.launch(cameraUri!!)

        } catch (e: Exception) {

            Log.e(
                "CAMERA_TEST",
                "Camera launch error",
                e
            )

            Toast.makeText(
                this,
                "Camera error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ====================================================
    // Set Selected Image
    // ====================================================

    private fun setSelectedImage(
        position: Int,
        uri: Uri
    ) {

        when (position) {

            // ------------------------------
            // Inventory 1
            // ------------------------------

            1 -> {

                inventoryImage1Uri =
                    uri

                imgInventory1.setImageURI(
                    uri
                )
            }


            // ------------------------------
            // Inventory 2
            // ------------------------------

            2 -> {

                inventoryImage2Uri =
                    uri

                imgInventory2.setImageURI(
                    uri
                )
            }


            // ------------------------------
            // Vehicle 1
            // ------------------------------

            3 -> {

                vehicleImage1Uri =
                    uri

                imgVehicle1.setImageURI(
                    uri
                )
            }


            // ------------------------------
            // Vehicle 2
            // ------------------------------

            4 -> {

                vehicleImage2Uri =
                    uri

                imgVehicle2.setImageURI(
                    uri
                )
            }


            // ------------------------------
            // Vehicle 3
            // ------------------------------

            5 -> {

                vehicleImage3Uri =
                    uri

                imgVehicle3.setImageURI(
                    uri
                )
            }


            // ------------------------------
            // Vehicle 4
            // ------------------------------

            6 -> {

                vehicleImage4Uri =
                    uri

                imgVehicle4.setImageURI(
                    uri
                )
            }


            // ------------------------------
            // Vehicle 5
            // ------------------------------

            7 -> {

                vehicleImage5Uri =
                    uri

                imgVehicle5.setImageURI(
                    uri
                )
            }
        }


        updateInventoryButtonText()
        updateVehicleButtonText()
    }


    // ====================================================
    // Button Text
    // ====================================================

    private fun updateInventoryButtonText() {

        val count =
            listOf(
                inventoryImage1Uri,
                inventoryImage2Uri
            ).count {
                it != null
            }


        btnSelectInventory.text =
            if (count == 2) {

                "2 INVENTORY IMAGES SELECTED ✓"

            } else {

                "SELECT 2 INVENTORY IMAGES"
            }
    }


    private fun updateVehicleButtonText() {

        val count =
            listOf(
                vehicleImage1Uri,
                vehicleImage2Uri,
                vehicleImage3Uri,
                vehicleImage4Uri,
                vehicleImage5Uri
            ).count {
                it != null
            }


        btnSelectVehicle.text =
            if (count == 5) {

                "5 VEHICLE IMAGES SELECTED ✓"

            } else {

                "SELECT 5 VEHICLE IMAGES ($count/5)"
            }
    }


    // ====================================================
    // Submit
    // ====================================================

    private fun setupSubmitButton() {

        btnSubmit.setOnClickListener {

            uploadImages()
        }
    }


    // ====================================================
    // Upload Images
    // ====================================================

    private fun uploadImages() {

        val vehicleNumber =
            intent.getStringExtra(
                "vehicleNumber"
            ) ?: ""


        val status =
            intent.getStringExtra(
                "status"
            ) ?: ""

        val userEmail =
            sessionManager.getUserEmail()
        val userName =
            sessionManager.getUserName()
        if (userName.isEmpty()) {

            Toast.makeText(
                this,
                "User name not found. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (userEmail.isEmpty()) {

            Toast.makeText(
                this,
                "User email not found. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }
        // -----------------------------------------------
        // Vehicle Number
        // -----------------------------------------------

        if (vehicleNumber.isEmpty()) {

            Toast.makeText(
                this,
                "Vehicle number missing",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // -----------------------------------------------
        // Status
        // -----------------------------------------------

        if (status.isEmpty()) {

            Toast.makeText(
                this,
                "Status missing",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // -----------------------------------------------
        // Inventory
        // -----------------------------------------------

        if (
            inventoryImage1Uri == null ||
            inventoryImage2Uri == null
        ) {

            Toast.makeText(
                this,
                "Please select exactly 2 inventory images",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        // -----------------------------------------------
        // Vehicle
        // -----------------------------------------------

        if (
            vehicleImage1Uri == null ||
            vehicleImage2Uri == null ||
            vehicleImage3Uri == null ||
            vehicleImage4Uri == null ||
            vehicleImage5Uri == null
        ) {

            Toast.makeText(
                this,
                "Please select exactly 5 vehicle images",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        try {

            Toast.makeText(
                this,
                "Preparing images...",
                Toast.LENGTH_SHORT
            ).show()


            // -------------------------------------------
            // Compress Inventory
            // -------------------------------------------

            val inventory1 =
                uriToCompressedFile(
                    inventoryImage1Uri!!,
                    "inventory_1"
                )


            val inventory2 =
                uriToCompressedFile(
                    inventoryImage2Uri!!,
                    "inventory_2"
                )


            // -------------------------------------------
            // Compress Vehicle
            // -------------------------------------------

            val vehicle1 =
                uriToCompressedFile(
                    vehicleImage1Uri!!,
                    "vehicle_1"
                )


            val vehicle2 =
                uriToCompressedFile(
                    vehicleImage2Uri!!,
                    "vehicle_2"
                )


            val vehicle3 =
                uriToCompressedFile(
                    vehicleImage3Uri!!,
                    "vehicle_3"
                )


            val vehicle4 =
                uriToCompressedFile(
                    vehicleImage4Uri!!,
                    "vehicle_4"
                )


            val vehicle5 =
                uriToCompressedFile(
                    vehicleImage5Uri!!,
                    "vehicle_5"
                )


            // -------------------------------------------
            // Final size validation
            // -------------------------------------------

            val files =
                listOf(
                    inventory1,
                    inventory2,
                    vehicle1,
                    vehicle2,
                    vehicle3,
                    vehicle4,
                    vehicle5
                )


            for (file in files) {

                val sizeKB =
                    file.length() / 1024


                if (sizeKB > 200) {

                    Toast.makeText(
                        this,
                        "${file.name} is still larger than 200 KB",
                        Toast.LENGTH_LONG
                    ).show()

                    return
                }
            }


            // -------------------------------------------
            // Upload
            // -------------------------------------------

            viewModel.uploadImages(

                vehicleNumber,

                status,
                userEmail,
                userName,
                inventory1,
                inventory2,

                vehicle1,
                vehicle2,
                vehicle3,
                vehicle4,
                vehicle5
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to prepare images: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    // ====================================================
    // Compress Image
    // ====================================================

    private fun uriToCompressedFile(
        uri: Uri,
        fileName: String
    ): File {

        val inputStream =
            contentResolver.openInputStream(uri)
                ?: throw Exception(
                    "Unable to read selected image"
                )


        val bitmap =
            inputStream.use {

                BitmapFactory.decodeStream(it)
                    ?: throw Exception(
                        "Invalid image"
                    )
            }


        // Resize image first
        val resizedBitmap =
            resizeBitmap(
                bitmap,
                1280
            )


        val file =
            File(
                cacheDir,
                "${fileName}_${System.currentTimeMillis()}.jpg"
            )


        var quality = 90


        while (quality >= 20) {

            val output =
                ByteArrayOutputStream()


            resizedBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                quality,
                output
            )


            val bytes =
                output.toByteArray()


            val sizeKB =
                bytes.size / 1024


            /*
             * Target:
             *
             * 150 KB - 200 KB
             *
             * If <= 200 KB, accept it.
             */

            if (sizeKB <= 200) {

                FileOutputStream(file).use {

                    it.write(bytes)
                }


                output.close()

                break
            }


            output.close()

            quality -= 5
        }


        resizedBitmap.recycle()


        if (!file.exists()) {

            throw Exception(
                "Unable to compress $fileName"
            )
        }


        val finalSizeKB =
            file.length() / 1024


        if (finalSizeKB > 200) {

            throw Exception(
                "$fileName could not be compressed below 200 KB"
            )
        }


        return file
    }


    // ====================================================
    // Resize Bitmap
    // ====================================================

    private fun resizeBitmap(
        bitmap: Bitmap,
        maxSize: Int
    ): Bitmap {

        val width =
            bitmap.width

        val height =
            bitmap.height


        if (
            width <= maxSize &&
            height <= maxSize
        ) {

            return bitmap
        }


        val ratio =
            min(
                maxSize.toFloat() / width,
                maxSize.toFloat() / height
            )


        val newWidth =
            (width * ratio).toInt()


        val newHeight =
            (height * ratio).toInt()


        return Bitmap.createScaledBitmap(
            bitmap,
            newWidth,
            newHeight,
            true
        )
    }


    // ====================================================
    // Observe ViewModel
    // ====================================================

    private fun observeViewModel() {

        viewModel.isLoading.observe(
            this
        ) { loading ->

            if (loading) {

                progressBar.visibility =
                    View.VISIBLE

                btnSubmit.isEnabled =
                    false

                btnSelectInventory.isEnabled =
                    false

                btnSelectVehicle.isEnabled =
                    false

            } else {

                progressBar.visibility =
                    View.GONE

                btnSubmit.isEnabled =
                    true

                btnSelectInventory.isEnabled =
                    true

                btnSelectVehicle.isEnabled =
                    true
            }
        }


        viewModel.uploadResult.observe(
            this
        ) { result ->

            if (result != null) {

                val vehicleNumber =
                    intent.getStringExtra(
                        "vehicleNumber"
                    ) ?: ""

                // -----------------------------------------
                // IMPORTANT:
                // Remove vehicle from pending image queue
                // -----------------------------------------

                viewModel.markUploadCompleted(
                    vehicleNumber
                )

                Toast.makeText(
                    this,
                    result.message
                        ?: "Images uploaded successfully",
                    Toast.LENGTH_LONG
                ).show()

                val resultIntent =
                    Intent()

                resultIntent.putExtra(
                    "status",
                    result.status
                )

                setResult(
                    RESULT_OK,
                    resultIntent
                )

                finish()
            }
        }



        viewModel.error.observe(
            this
        ) { error ->

            if (
                !error.isNullOrEmpty()
            ) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // ====================================================
    // Back
    // ====================================================

    override fun onSupportNavigateUp():
            Boolean {

        finish()

        return true
    }
}