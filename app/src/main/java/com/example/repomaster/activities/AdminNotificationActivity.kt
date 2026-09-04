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
import com.example.repomaster.models.AdminNotificationItem
import com.example.repomaster.repository.AdminNotificationRepository
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.AdminNotificationViewModel
import com.example.repomaster.viewmodel.AdminNotificationViewModelFactory
import com.example.repomaster.viewmodel.UserViewModel
import com.example.repomaster.network.RetrofitClient
import com.google.android.material.appbar.MaterialToolbar

class AdminNotificationActivity : AppCompatActivity() {

    private lateinit var recyclerNotifications: RecyclerView
    private lateinit var adapter: AdminNotificationAdapter

    private lateinit var notificationViewModel:
            AdminNotificationViewModel

    private lateinit var userViewModel:
            UserViewModel

    private lateinit var sessionManager:
            SessionManager

    private var vehicleNotifications =
        emptyList<AdminNotificationItem>()

    private var pendingUserNotifications =
        emptyList<AdminNotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_notification
        )

        val toolbar =
            findViewById<MaterialToolbar>(
                R.id.toolbar
            )

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerNotifications =
            findViewById(
                R.id.recyclerNotifications
            )

        recyclerNotifications.layoutManager =
            LinearLayoutManager(this)

        sessionManager =
            SessionManager(this)

        setupViewModels()
        setupAdapter()
        observeNotifications()
        loadNotifications()
    }

    private fun setupViewModels() {

        val repository =
            AdminNotificationRepository(
                RetrofitClient.userApi
            )

        val factory =
            AdminNotificationViewModelFactory(
                repository
            )

        notificationViewModel =
            ViewModelProvider(
                this,
                factory
            )[AdminNotificationViewModel::class.java]

        userViewModel =
            ViewModelProvider(this)[
                UserViewModel::class.java
            ]
    }

    private fun setupAdapter() {

        adapter =
            AdminNotificationAdapter(
                emptyList()
            ) { notification ->

                /*
                 * USER REQUEST
                 */
                if (notification.type == "USER") {

                    startActivity(
                        Intent(
                            this,
                            PendingUsersActivity::class.java
                        )
                    )

                    return@AdminNotificationAdapter
                }

                /*
                 * VEHICLE NOTIFICATION
                 */

                val notificationId =
                    notification.notificationId

                if (notificationId != null) {

                    /*
                     * Remove immediately from UI.
                     */
                    adapter.removeNotification(
                        notification
                    )

                    /*
                     * Mark as read in database.
                     */
                    notificationViewModel.markAsRead(
                        notificationId,
                        sessionManager.getAgencyId()
                    )
                }

                /*
                 * Open vehicle details.
                 */
                val vehicleNumber =
                    notification.vehicleNumber

                if (!vehicleNumber.isNullOrBlank()) {

                    val intent =
                        Intent(
                            this,
                            VehicleDetailsActivity::class.java
                        )

                    intent.putExtra(
                        "vehicleNumber",
                        vehicleNumber
                    )

                    startActivity(intent)

                } else {

                    Toast.makeText(
                        this,
                        "Vehicle number not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        recyclerNotifications.adapter =
            adapter
    }




    private fun observeNotifications() {

        /*
         * Vehicle notifications
         */
        notificationViewModel.notifications
            .observe(this) { notifications ->

                vehicleNotifications =
                    notifications.map { notification ->

                        AdminNotificationItem(

                            type = "VEHICLE",

                            title =
                                notification.vehicle_number,

                            message =
                                notification.message,

                            date =
                                notification.created_at,

                            isRead =
                                notification.is_read == 1,

                            notificationId =
                                notification.id,

                            vehicleNumber =
                                notification.vehicle_number
                        )
                    }

                updateCombinedNotifications()
            }

        /*
         * Pending users
         */
        userViewModel.pendingUsers()
            .observe(this) { response ->

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    pendingUserNotifications =
                        response.body()!!.map { user ->

                            AdminNotificationItem(

                                type = "USER",

                                title =
                                    "New User Request",

                                message =
                                    "${user.fullName} wants to join your agency",

                                date = "",

                                isRead = false,

                                notificationId =
                                    user.id?.toInt()
                            )
                        }

                    updateCombinedNotifications()
                }
            }

        /*
         * Notification errors
         */
        notificationViewModel.error
            .observe(this) { error ->

                if (
                    !error.isNullOrEmpty()
                ) {

                    Toast.makeText(
                        this,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateCombinedNotifications() {

        val combined =
            vehicleNotifications +
                    pendingUserNotifications

        adapter.updateList(
            combined
        )
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

            finish()

            return
        }

        /*
         * Load vehicle notifications
         */
        notificationViewModel
            .loadNotifications(
                agencyId
            )

        /*
         * Load pending user requests
         */
        userViewModel
            .loadPendingUsers(
                agencyId
            )
    }

    override fun onResume() {

        super.onResume()

        if (::sessionManager.isInitialized) {

            val agencyId =
                sessionManager.getAgencyId()

            if (agencyId.isNotEmpty()) {

                notificationViewModel
                    .loadNotifications(
                        agencyId
                    )

                userViewModel
                    .loadPendingUsers(
                        agencyId
                    )
            }
        }
    }
}