package com.example.repomaster.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.repomaster.R
import android.widget.*
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModelProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import com.example.repomaster.viewmodel.HomeViewModel
import android.view.View
import com.google.android.material.appbar.MaterialToolbar
import com.example.repomaster.models.UploadResponse
import androidx.appcompat.app.AlertDialog
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.repomaster.network.RetrofitClient
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.repomaster.utils.SessionManager
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType

class BulkUploadActivity : AppCompatActivity() {
    private lateinit var btnChooseFile: Button
    private lateinit var btnUpload: Button
    private lateinit var txtFileName: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var homeViewModel: HomeViewModel
    private var selectedFileUri: Uri? = null
    private lateinit var toolbar: MaterialToolbar
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {

                selectedFileUri = uri

                txtFileName.text = "Selected: ${getFileName(uri)}"

            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //call notification permission
        createNotificationChannel()
        setContentView(R.layout.activity_bulk_upload)
        //toolbar
        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "Upload File"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        btnChooseFile = findViewById(R.id.btnChooseFile)
        btnUpload = findViewById(R.id.btnUpload)
        txtFileName = findViewById(R.id.txtFileName)
        progressBar = findViewById(R.id.progressBar)
        val btnDownload = findViewById<Button>(R.id.btnDownloadTemplate)

        btnDownload.setOnClickListener {
            downloadTemplate()
        }
        btnChooseFile.setOnClickListener {

            filePickerLauncher.launch(
                arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            )
        }
        btnUpload.setOnClickListener {

            if (selectedFileUri == null) {

                Toast.makeText(
                    this,
                    "Please select an Excel file",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            uploadExcel(selectedFileUri!!)
        }

    }

    private fun downloadTemplate() {

        RetrofitClient.api.downloadTemplate()
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        saveFile(response.body()!!)

                    } else {

                        Toast.makeText(
                            this@BulkUploadActivity,
                            "Download Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@BulkUploadActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "download_channel",
                "Downloads",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }
    //save file
    private fun saveFile(body: ResponseBody) {

        val resolver = contentResolver

        val values = ContentValues().apply {
            put(
                MediaStore.Downloads.DISPLAY_NAME,
                "Vehicle_Template.xlsx"
            )
            put(
                MediaStore.Downloads.MIME_TYPE,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
            }
        }

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Files.getContentUri("external")
            }

        val uri = resolver.insert(collection, values)

        if (uri != null) {

            resolver.openOutputStream(uri)?.use { output ->

                body.byteStream().copyTo(output)

            }
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            showDownloadNotification(uri)

            AlertDialog.Builder(this)
                .setTitle("Download Completed")
                .setMessage(
                    "Excel template has been downloaded successfully.\n\n" +
                            "Location:\nDownloads/Vehicle_Template.xlsx"
                )
                .setPositiveButton("OK", null)
                .show()

        } else {

            Toast.makeText(
                this,
                "Unable to save file",
                Toast.LENGTH_LONG
            ).show()
        }
    }
   //show download notification
   private fun showDownloadNotification(fileUri: Uri) {

       val openFileIntent = Intent(Intent.ACTION_VIEW).apply {

           setDataAndType(
               fileUri,
               "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
           )

           addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
       }


       val pendingIntent = PendingIntent.getActivity(
           this,
           0,
           openFileIntent,
           PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
       )


       val notification =
           NotificationCompat.Builder(
               this,
               "download_channel"
           )
               .setSmallIcon(R.drawable.baseline_download_24)
               .setContentTitle("RepoMaster")
               .setContentText(
                   "Excel Template downloaded"
               )
               .setStyle(
                   NotificationCompat.BigTextStyle()
                       .bigText(
                           "Vehicle_Template.xlsx\nTap to open file"
                       )
               )
               .setContentIntent(pendingIntent)
               .setAutoCancel(true)
               .build()


       val notificationManager =
           getSystemService(
               NotificationManager::class.java
           )

       notificationManager.notify(
           101,
           notification
       )
   }




    private fun getFileName(uri: Uri): String {

        var name = "Excel File"

        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {

            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (it.moveToFirst() && index != -1) {

                name = it.getString(index)
            }
        }

        return name
    }
    private fun uploadExcel(uri: Uri) {

        btnUpload.isEnabled = false
        progressBar.visibility = View.VISIBLE
        val inputStream = contentResolver.openInputStream(uri)

        val tempFile = File(cacheDir, "vehicles.xlsx")

        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)

        outputStream.close()
        inputStream?.close()

        val requestFile = tempFile.asRequestBody(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                .toMediaTypeOrNull()
        )

        val body = MultipartBody.Part.createFormData(
            "file",
            tempFile.name,
            requestFile
        )
        val agencyId = SessionManager(this).getAgencyId()

        if (agencyId.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Agency ID not found. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val agencyIdBody = agencyId.toRequestBody(
            "text/plain".toMediaType()
        )

        homeViewModel.uploadExcel(
            body,
            agencyIdBody
        ).observe(this) { response ->

            btnUpload.isEnabled = true
            progressBar.visibility = View.GONE
            tempFile.delete()

            if (response.isSuccessful) {

                val result = response.body()

                if (result == null) {

                    Toast.makeText(
                        this,
                        "Empty response from server",
                        Toast.LENGTH_LONG
                    ).show()

                    return@observe
                }

                if (result.errors.isNotEmpty()) {

                    AlertDialog.Builder(this)
                        .setTitle("Upload Failed")
                        .setMessage(result.errors.joinToString("\n"))
                        .setPositiveButton("OK", null)
                        .show()

                } else {

                    AlertDialog.Builder(this)
                        .setTitle("Upload Successful")
                        .setMessage(
                            """
                Total Records : ${result.totalRows}

                Inserted : ${result.inserted}

                Updated : ${result.updated}

                Failed : ${result.failed}
                """.trimIndent()
                        )
                        .setPositiveButton("OK", null)
                        .show()
                }

            } else {

                Toast.makeText(
                    this,
                    "Upload failed (${response.code()})",
                    Toast.LENGTH_LONG
                ).show()
            }



        }
    }
        override fun onSupportNavigateUp(): Boolean {


            finish()


            return true

        }
    }