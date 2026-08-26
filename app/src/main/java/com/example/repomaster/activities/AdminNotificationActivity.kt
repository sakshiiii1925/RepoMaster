package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.AdminNotificationAdapter
import com.example.repomaster.repository.AdminNotificationRepository
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.AdminNotificationViewModel
import com.example.repomaster.viewmodel.AdminNotificationViewModelFactory
import com.example.repomaster.network.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar

class AdminNotificationActivity : AppCompatActivity() {

    private lateinit var recyclerNotifications: RecyclerView

    private lateinit var adapter: AdminNotificationAdapter

    private lateinit var viewModel: AdminNotificationViewModel

    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_notification
        )


        val toolbar =
            findViewById<androidx.appcompat.widget.Toolbar>(
                R.id.toolbar
            )

        toolbar.setNavigationOnClickListener {

            finish()

        }


        recyclerNotifications =
            findViewById(
                R.id.recyclerNotifications
            )


        sessionManager =
            SessionManager(this)


        recyclerNotifications.layoutManager =
            LinearLayoutManager(this)


        setupViewModel()

        setupAdapter()

        observeNotifications()

        loadNotifications()
    }


    private fun setupViewModel() {

        val repository =
            AdminNotificationRepository(
                RetrofitClient.userApi
            )

        val factory =
            AdminNotificationViewModelFactory(
                repository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[AdminNotificationViewModel::class.java]
    }


    private fun setupAdapter() {

        adapter =
            AdminNotificationAdapter(
                emptyList()
            ) { notification ->


                // Mark notification as read
                viewModel.markAsRead(
                    notification.id,
                    sessionManager.getAgencyId()
                )


                // Open vehicle details
                val intent =
                    Intent(
                        this,
                        VehicleDetailsActivity::class.java
                    )

                intent.putExtra(
                    "vehicleNumber",
                    notification.vehicle_number
                )

                startActivity(intent)
            }


        recyclerNotifications.adapter =
            adapter
    }


    private fun observeNotifications() {

        viewModel.notifications.observe(
            this
        ) { notifications ->

            adapter.updateList(
                notifications
            )
        }


        viewModel.error.observe(
            this
        ) { error ->

            if (!error.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun loadNotifications() {

        val agencyId =
            sessionManager.getAgencyId()


        if (agencyId.isEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        viewModel.loadNotifications(
            agencyId
        )
    }
}