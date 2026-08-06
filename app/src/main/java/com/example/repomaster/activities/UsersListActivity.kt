package com.example.repomaster.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.repomaster.R
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.viewmodel.UserViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.adapters.UsersAdapter
import android.widget.*
import androidx.lifecycle.observe
import com.google.android.material.textfield.TextInputEditText
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.app.AlertDialog
class UsersListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userViewModel: UserViewModel
    private lateinit var searchView: TextInputEditText
    private lateinit var toolbar:
            androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        //toolbar
        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "View AgentList"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val sessionManager = SessionManager(this)
        val agencyId = sessionManager.getAgencyId()
       searchView=findViewById<TextInputEditText>(R.id.etSearchUser)
        //initial load
        if (agencyId != null) {

            userViewModel.getUsersByAdmin(agencyId)
                .observe(this) { response ->

                    if (response.isSuccessful && response.body() != null) {
//call delete button
                        recyclerView.adapter =
                            UsersAdapter(
                                response.body()!!,

                                onDeleteClick = { user ->


                                    AlertDialog.Builder(this)

                                        .setTitle("Delete User")

                                        .setMessage(
                                            "Delete ${user.fullName}?"
                                        )

                                        .setPositiveButton("Yes"){_,_->


                                            user.id?.let { id ->


                                                userViewModel.deleteUser(id)
                                                    .observe(this){response ->


                                                        if(response.isSuccessful){

                                                            Toast.makeText(
                                                                this,
                                                                "User Deleted",
                                                                Toast.LENGTH_SHORT
                                                            ).show()


                                                            recreate()

                                                        }


                                                    }

                                            }


                                        }

                                        .setNegativeButton(
                                            "No",
                                            null
                                        )

                                        .show()

                                }
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
        //search
        searchView.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {


                    if(agencyId != null){

                        userViewModel.searchUsers(
                            agencyId,
                            s.toString()
                        )
                            .observe(this@UsersListActivity){ response ->


                                if(response.isSuccessful){

                                    recyclerView.adapter =
                                        UsersAdapter(
                                            response.body() ?: emptyList(),

                                            onDeleteClick = { user ->

                                                AlertDialog.Builder(this@UsersListActivity)
                                                    .setTitle("Delete User")
                                                    .setMessage("Delete ${user.fullName}?")
                                                    .setPositiveButton("Yes") { _, _ ->

                                                        user.id?.let { id ->

                                                            userViewModel.deleteUser(id)
                                                                .observe(this@UsersListActivity) { result ->

                                                                    if (result.isSuccessful) {

                                                                        Toast.makeText(
                                                                            this@UsersListActivity,
                                                                            "User Deleted",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()

                                                                        recreate()
                                                                    }
                                                                }
                                                        }

                                                    }
                                                    .setNegativeButton("No", null)
                                                    .show()
                                            }
                                        )

                                }

                            }

                    }


                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}

            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}