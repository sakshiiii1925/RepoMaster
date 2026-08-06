package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.repomaster.R
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import com.example.repomaster.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.models.User
import androidx.core.widget.doAfterTextChanged
//declare Variables
private lateinit var toolbar: androidx.appcompat.widget.Toolbar

private lateinit var etFullName: EditText
private lateinit var etEmail: EditText
private lateinit var etMobile: EditText
private lateinit var etAddress: EditText
private lateinit var etPassword: EditText
private lateinit var etConfirmPassword: EditText
private lateinit var etrefAgencyId: EditText
private lateinit var btnRegister: Button

private lateinit var userViewModel: UserViewModel

class UserRegistration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_registration)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        supportActionBar?.title = "Create Account"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//initialize variable
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etAddress = findViewById(R.id.etAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etrefAgencyId=findViewById(R.id.etAgencyId)
val login=findViewById<TextView>(R.id.login)
        btnRegister = findViewById(R.id.btnRegister)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        //live email check
        etEmail.doAfterTextChanged { text ->

            val email = text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email is required"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter a valid email"
            } else {
                etEmail.error = null
            }
        }

        //live password validation
        val passwordPattern =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")

        etPassword.doAfterTextChanged { text ->

            val password = text.toString()

            if (password.isEmpty()) {
                etPassword.error = "Password is required"
            } else if (!password.matches(passwordPattern)) {
                etPassword.error =
                    "Min 8 chars, uppercase, lowercase, number & special character"
            } else {
                etPassword.error = null
            }
        }
        //live confirm password validation
        etConfirmPassword.doAfterTextChanged {

            if (it.toString() != etPassword.text.toString()) {
                etConfirmPassword.error = "Passwords do not match"
            } else {
                etConfirmPassword.error = null
            }
        }
        //login click code
        login.setOnClickListener {
            val  intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        //registration logic
        btnRegister.setOnClickListener {

            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val refagencyId = etrefAgencyId.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            //validate fields
            if (fullName.isEmpty()) {
                etFullName.error = "Enter Full Name"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Enter Email"
                return@setOnClickListener
            }

            if (mobile.isEmpty()) {
                etMobile.error = "Enter Mobile Number"
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                etAddress.error = "Enter Address"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Enter Password"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                etConfirmPassword.error = "Confirm Password"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (refagencyId.isEmpty()) {
                etrefAgencyId.error = "Enter Email"
                return@setOnClickListener
            }
            val request = User(
                fullName = fullName,
                email = email,
                mobile = mobile,
                address = address,
                password = password,
                referenceAdminEmail = "",
                agencyId = refagencyId
            )

            userViewModel.registerUser(request).observe(this) { response ->

                if (response.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Registration Successful.\nWait for Admin Approval.",
                        Toast.LENGTH_LONG
                    ).show()
val  intent= Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                else {

                    Toast.makeText(
                        this,
                        "Error ${response.code()}: ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
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