package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import com.example.repomaster.viewmodel.UploadedImagesViewModelFactory
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import androidx.appcompat.app.AlertDialog
import com.example.repomaster.models.UploadedImage
import android.content.Intent
import com.example.repomaster.adapters.UploadedImageAdapter
import com.example.repomaster.viewmodel.UploadedImagesViewModel
import com.example.repomaster.repository.VehicleRepository
import com.google.android.material.appbar.MaterialToolbar

class UploadedImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtEmpty: TextView

    private lateinit var adapter: UploadedImageAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewModel: UploadedImagesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_uploaded_images
        )
        //toolbar

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "Add Images"

        // -----------------------------------------
        // Views
        // -----------------------------------------

        recyclerView =
            findViewById(R.id.recyclerUploadedImages)

        progressBar =
            findViewById(R.id.progressUploadedImages)

        txtEmpty =
            findViewById(R.id.txtEmptyUploadedImages)

        // -----------------------------------------
        // RecyclerView
        // -----------------------------------------

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        adapter =
            UploadedImageAdapter(

                // -----------------------------------------
                // VIEW
                // -----------------------------------------

                onViewClick = { uploadedImage ->

                    val intent =
                        Intent(
                            this,
                            UploadedImageDetailsActivity::class.java
                        )

                    intent.putExtra(
                        "uploaded_image_id",
                        uploadedImage.id
                    )

                    startActivity(intent)
                },

                // -----------------------------------------
                // DELETE
                // -----------------------------------------

                onDeleteClick = { uploadedImage ->

                    showDeleteConfirmation(
                        uploadedImage
                    )
                }
            )
        recyclerView.adapter = adapter

        // -----------------------------------------
        // ViewModel
        // -----------------------------------------

        val repository =
            VehicleRepository(applicationContext)

        val factory =
            UploadedImagesViewModelFactory(
                repository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[UploadedImagesViewModel::class.java]
        observeUploadedImages()

        // -----------------------------------------
        // Load data
        // -----------------------------------------

        viewModel.loadUploadedImages()
    }

    private fun observeUploadedImages() {

        viewModel.uploadedImages.observe(
            this
        ) { images ->

            progressBar.visibility =
                View.GONE

            if (images.isNullOrEmpty()) {

                recyclerView.visibility =
                    View.GONE

                txtEmpty.visibility =
                    View.VISIBLE

            } else {

                recyclerView.visibility =
                    View.VISIBLE

                txtEmpty.visibility =
                    View.GONE

                adapter.submitList(images)
            }
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
    private fun showDeleteConfirmation(
        uploadedImage: UploadedImage
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Uploaded Images")
            .setMessage(
                "Are you sure you want to delete images for vehicle " +
                        uploadedImage.vehicle_number +
                        "?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                viewModel.deleteUploadedImage(
                    uploadedImage.id
                )
            }
            .show()
    }
    override fun onResume() {
        super.onResume()

        if (::viewModel.isInitialized) {

            progressBar.visibility = View.VISIBLE

            viewModel.loadUploadedImages()
        }
    }
    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}