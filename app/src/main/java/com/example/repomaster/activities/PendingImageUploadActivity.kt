package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.PendingImageUploadAdapter
import com.example.repomaster.repository.VehicleRepository
import com.example.repomaster.viewmodel.PendingImageUploadViewModel
import com.example.repomaster.viewmodel.PendingImageUploadViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.example.repomaster.data.local.PendingImageUploadEntity
class PendingImageUploadActivity : AppCompatActivity() {

    private lateinit var viewModel: PendingImageUploadViewModel

    private lateinit var adapter: PendingImageUploadAdapter

    private lateinit var recyclerView: RecyclerView

    private lateinit var toolbar: MaterialToolbar


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_pending_image_upload
        )


        // =====================================================
        // TOOLBAR
        // =====================================================

        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )

        supportActionBar?.title =
            "Pending Image Uploads"

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )


        toolbar.setNavigationOnClickListener {

            finish()
        }


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerView =
            findViewById(
                R.id.recyclerPendingImages
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)


        // =====================================================
        // ADAPTER
        // =====================================================

        adapter =
            PendingImageUploadAdapter(
                emptyList()
            ) { pending ->

                openImageUpload(pending)
            }

        recyclerView.adapter =
            adapter


        // =====================================================
        // REPOSITORY
        // =====================================================

        val repository =
            VehicleRepository(
                applicationContext
            )


        // =====================================================
        // VIEWMODEL FACTORY
        // =====================================================

        val factory =
            PendingImageUploadViewModelFactory(
                repository
            )


        // =====================================================
        // VIEWMODEL
        // =====================================================

        viewModel =
            ViewModelProvider(
                this,
                factory
            ).get(
                PendingImageUploadViewModel::class.java
            )


        // =====================================================
        // OBSERVE PENDING VEHICLES
        // =====================================================

        viewModel.pendingUploads.observe(
            this
        ) { uploads ->

            adapter.updateList(
                uploads
            )
        }


        // =====================================================
        // LOAD DATA
        // =====================================================

        viewModel.loadPendingUploads()
    }


    // =========================================================
    // OPEN IMAGE UPLOAD
    // =========================================================

    private fun openImageUpload(
        pending: PendingImageUploadEntity
    ) {

        val intent =
            Intent(
                this,
                RepoImageUploadActivity::class.java
            )

        intent.putExtra(
            "vehicleNumber",
            pending.vehicleNumber
        )

        intent.putExtra(
            "status",
            pending.status
        )

        startActivity(intent)
    }


    // =========================================================
    // REFRESH WHEN RETURNING FROM IMAGE UPLOAD
    // =========================================================

    override fun onResume() {

        super.onResume()

        if (::viewModel.isInitialized) {

            viewModel.loadPendingUploads()
        }
    }


    // =========================================================
    // BACK BUTTON
    // =========================================================

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}