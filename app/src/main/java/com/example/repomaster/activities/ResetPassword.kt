package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ResetPassword : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnResetPassword: MaterialButton

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        val passwordPattern =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")

        etNewPassword.doAfterTextChanged { text ->

            val password = text.toString()

            if (password.isEmpty()) {
                etNewPassword.error = "Password is required"
            } else if (!password.matches(passwordPattern)) {
                etNewPassword.error =
                    "Min 8 chars, uppercase, lowercase, number & special character"
            } else {
                etNewPassword.error = null
            }
        }
        val email = intent.getStringExtra("email") ?: ""

        btnResetPassword.setOnClickListener {

            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (newPassword.isEmpty()) {
                etNewPassword.error = "Enter New Password"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                etConfirmPassword.error = "Confirm Password"
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            userViewModel.resetPassword(
                email,
                newPassword
            ).observe(this) { response ->

                if (response.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Password Updated Successfully",
                        Toast.LENGTH_SHORT
                    ).show()


                    val intent = Intent(
                        this,
                        LoginActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)

                } else {

                    Toast.makeText(
                        this,
                        "Password Update Failed: ${response.code()}",
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