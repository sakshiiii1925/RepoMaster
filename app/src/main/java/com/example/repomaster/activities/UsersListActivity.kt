
package com.example.repomaster.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.UsersAdapter
import com.example.repomaster.models.User
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputEditText

class UsersListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userViewModel: UserViewModel
    private lateinit var searchView: TextInputEditText
    private lateinit var toolbar: Toolbar
    private lateinit var usersAdapter: UsersAdapter

    private var agencyId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_users_list)

        // ============================================================
        // TOOLBAR
        // ============================================================

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )

        supportActionBar?.title = "View AgentList"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ============================================================
        // RECYCLER VIEW
        // ============================================================

        recyclerView = findViewById(R.id.recyclerUsers)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        // ============================================================
        // VIEW MODEL
        // ============================================================

        userViewModel =
            ViewModelProvider(this)[
                UserViewModel::class.java
            ]

        // ============================================================
        // SESSION
        // ============================================================

        val sessionManager =
            SessionManager(this)

        agencyId =
            sessionManager.getAgencyId()

        // ============================================================
        // SEARCH
        // ============================================================

        searchView =
            findViewById(R.id.etSearchUser)

        // ============================================================
        // CREATE ADAPTER ONCE
        // ============================================================

        usersAdapter =
            UsersAdapter(
                list = emptyList(),

                // DELETE
                onDeleteClick = { user ->

                    showDeleteDialog(user)
                },

                // ACTIVE / INACTIVE
                onStatusClick = { user ->

                    showStatusDialog(user)
                }
            )

        recyclerView.adapter =
            usersAdapter

        // ============================================================
        // LOAD USERS
        // ============================================================

        loadUsers()

        // ============================================================
        // SEARCH
        // ============================================================

        setupSearch()
    }

    // ============================================================
    // LOAD ALL USERS
    // ============================================================

    private fun loadUsers() {

        if (agencyId.isBlank()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        userViewModel
            .getUsersByAdmin(agencyId)
            .observe(this) { response ->

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    usersAdapter.updateData(
                        response.body()!!
                    )

                } else {

                    Toast.makeText(
                        this,
                        "No Users Found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // ============================================================
    // DELETE USER DIALOG
    // ============================================================

    private fun showDeleteDialog(
        user: User
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage(
                "Delete ${user.fullName}?"
            )
            .setPositiveButton("Yes") { _, _ ->

                val id = user.id

                if (id != null) {

                    deleteUser(id)
                }
            }
            .setNegativeButton(
                "No",
                null
            )
            .show()
    }

    // ============================================================
    // DELETE USER
    // ============================================================

    private fun deleteUser(
        id: Long
    ) {

        userViewModel
            .deleteUser(id)
            .observe(this) { response ->

                if (response.isSuccessful) {

                    Toast.makeText(
                        this,
                        "User Deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadUsers()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to delete user",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // ============================================================
    // STATUS CONFIRMATION DIALOG
    // ============================================================

    private fun showStatusDialog(
        user: User
    ) {

        val currentStatus =
            user.status ?: ""

        val newStatus =
            if (
                currentStatus.equals(
                    "ACTIVE",
                    ignoreCase = true
                )
            ) {
                "INACTIVE"
            } else {
                "ACTIVE"
            }

        val action =
            if (newStatus == "ACTIVE") {
                "activate"
            } else {
                "deactivate"
            }

        val title =
            if (newStatus == "ACTIVE") {
                "Activate User"
            } else {
                "Deactivate User"
            }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(
                "Do you want to $action ${user.fullName}?"
            )
            .setPositiveButton("Yes") { _, _ ->

                val id = user.id

                if (id != null) {

                    updateUserStatus(
                        user,
                        id,
                        newStatus
                    )

                } else {

                    Toast.makeText(
                        this,
                        "User ID not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(
                "No",
                null
            )
            .show()
    }

    // ============================================================
    // UPDATE USER STATUS
    // ============================================================
    private fun updateUserStatus(
        user: User,
        id: Long,
        newStatus: String
    ) {
        userViewModel
            .updateUserStatus(id, newStatus)
            .observe(this) { response ->

                if (response.isSuccessful) {

                    val updatedUser = response.body()

                    if (updatedUser != null) {
                        usersAdapter.updateUser(updatedUser)
                    }

                    Toast.makeText(
                        this,
                        if (newStatus == "ACTIVE") {
                            "User Activated"
                        } else {
                            "User Deactivated"
                        },
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to update user status",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }




    // ============================================================
    // SEARCH
    // ============================================================

    private fun setupSearch() {

        searchView.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    searchUsers(
                        s?.toString() ?: ""
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    // ============================================================
    // SEARCH USERS
    // ============================================================

    private fun searchUsers(
        search: String
    ) {

        if (agencyId.isBlank()) {
            return
        }

        if (search.trim().isEmpty()) {

            loadUsers()

            return
        }

        userViewModel
            .searchUsers(
                agencyId,
                search
            )
            .observe(this) { response ->

                if (response.isSuccessful) {

                    usersAdapter.updateData(
                        response.body()
                            ?: emptyList()
                    )
                }
            }
    }

    // ============================================================
    // BACK BUTTON
    // ============================================================

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}

