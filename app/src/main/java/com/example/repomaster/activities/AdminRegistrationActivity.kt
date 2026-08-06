package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.repomaster.R
import android.widget.*
import com.example.repomaster.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.models.User
import androidx.core.widget.doAfterTextChanged
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var etFullName: EditText
private lateinit var etEmail: EditText
private lateinit var etMobile: EditText
private lateinit var etPassword: EditText
private lateinit var etConfirmPassword: EditText
private lateinit var etAgencyId: EditText
private lateinit var btnRegister: Button

private lateinit var userViewModel: UserViewModel

class AdminRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_registration)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        supportActionBar?.title = "Create Admin Account"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etAgencyId = findViewById(R.id.etAdencyId)
        btnRegister = findViewById(R.id.btnRegister)
        val login=findViewById<TextView>(R.id.login)

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
        //login clicked code
        login.setOnClickListener {
            val  intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        //btnclick code
        btnRegister.setOnClickListener {

            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val agencyId = etAgencyId.text.toString().trim()
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
            if (agencyId.isEmpty()) {
                etAgencyId.error = "Enter Agency ID"
                return@setOnClickListener
            }
            val user = User(
                fullName = fullName,
                email = email,
                mobile = mobile,
                password = password,
                address = "",
                referenceAdminEmail = "",
                agencyId = agencyId
            )
            userViewModel.registerAdmin(user).observe(this) { response ->

                if (response.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Admin Registered Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    val  intent= Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Invalid email or password",
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