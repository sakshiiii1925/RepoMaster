package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.models.User
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.example.repomaster.R
import com.google.android.material.appbar.MaterialToolbar


private lateinit var etFullName: TextInputEditText
private lateinit var etEmail: TextInputEditText
private lateinit var etMobile: TextInputEditText
private lateinit var etAddress: TextInputEditText
private lateinit var btnUpdate: Button

private lateinit var userViewModel: UserViewModel
private lateinit var toolbar: MaterialToolbar

private var userId: Long = 0


class EditUserProfile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_user_profile)

//toolbar
        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            " Edit Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        userViewModel =
            ViewModelProvider(this)[UserViewModel::class.java]


        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etAddress = findViewById(R.id.etAddress)
        btnUpdate = findViewById(R.id.btnUpdate)



        // Load Profile

        val email =
            SessionManager(this).getUserEmail()


        userViewModel.getProfile(email)
            .observe(this){ response ->


                if(response.isSuccessful &&
                    response.body()!=null){


                    val user =
                        response.body()!!


                    userId = user.id ?: 0


                    etFullName.setText(user.fullName)
                    etEmail.setText(user.email)
                    etMobile.setText(user.mobile)
                    etAddress.setText(user.address)


                }
                else{

                    Toast.makeText(
                        this,
                        "Unable to load profile",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }



        // Update Button

        btnUpdate.setOnClickListener {


            val updatedUser = User(

                id = userId,

                fullName =
                    etFullName.text.toString(),

                email =
                    etEmail.text.toString(),

                mobile =
                    etMobile.text.toString(),

                password = "",

                address =
                    etAddress.text.toString(),
                referenceAdminEmail = "",
                agencyId = ""
            )


            userViewModel.updateUser(
                userId,
                updatedUser
            )
                .observe(this){ response ->


                    if(response.isSuccessful){


                        Toast.makeText(
                            this,
                            "Profile Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent= Intent(this, AdminDashboardActivity::class.java)
                        startActivity(intent)

                    }
                    else{


                        Toast.makeText(
                            this,
                            "Update Failed",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }

        }

    }
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}