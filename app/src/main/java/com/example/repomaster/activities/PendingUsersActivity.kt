package com.example.repomaster.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.repomaster.R
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.adapters.PendingUserAdapter
import com.example.repomaster.viewmodel.UserViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.repomaster.models.User
import com.example.repomaster.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar

private lateinit var recyclerView: RecyclerView
private lateinit var adapter: PendingUserAdapter
private lateinit var userViewModel: UserViewModel
private lateinit var toolbar: MaterialToolbar
class PendingUsersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pending_users)
        //toolbar
        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "Pending Requests"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById(R.id.recyclerPendingUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        loadPendingUsers()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loadPendingUsers() {

        val agencyId = SessionManager(this).getAgencyId()

        if (agencyId.isEmpty()) {
            Toast.makeText(this, "Agency ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Observe only once
        userViewModel.pendingUsers().observe(this) { response ->

            if (response.isSuccessful && response.body() != null) {

                adapter = PendingUserAdapter(
                    response.body()!!,

                    onApproveClick = { user ->

                        AlertDialog.Builder(this)
                            .setTitle("Approve User")
                            .setMessage("Are you sure you want to approve this user?")
                            .setPositiveButton("Yes") { _, _ ->

                                user.id?.let { id ->

                                    userViewModel.approveUser(id).observe(this) {

                                        Toast.makeText(
                                            this,
                                            "User Approved Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        userViewModel.loadPendingUsers(agencyId)
                                    }
                                }
                            }
                            .setNegativeButton("No", null)
                            .show()
                    },

                    onRejectClick = { user ->
                        rejectUser(user)
                    }
                )

                recyclerView.adapter = adapter

            } else {

                Toast.makeText(
                    this,
                    "No Pending Users",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Load data
        userViewModel.loadPendingUsers(agencyId)
    }
    private fun rejectUser(user: User) {

        AlertDialog.Builder(this)
            .setTitle("Reject User")
            .setMessage("Are you sure you want to reject this user?")
            .setPositiveButton("Yes") { _, _ ->

                user.id?.let { id ->

                    userViewModel.rejectUser(id).observe(this) { response ->

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this,
                                "User Rejected Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            val agencyId = SessionManager(this).getAgencyId()
                            userViewModel.loadPendingUsers(agencyId)

                        } else {

                            Toast.makeText(
                                this,
                                "Reject Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}