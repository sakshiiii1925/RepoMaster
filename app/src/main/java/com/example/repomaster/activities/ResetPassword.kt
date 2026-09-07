
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

    private lateinit var email: String
    private lateinit var otp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reset_password)

        // Get email and OTP from OTP verification screen
        email = intent.getStringExtra("email") ?: ""
        otp = intent.getStringExtra("otp") ?: ""

        // Toolbar
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.title = "Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // ViewModel
        userViewModel =
            ViewModelProvider(this)[UserViewModel::class.java]

        // Views
        etNewPassword =
            findViewById(R.id.etNewPassword)

        etConfirmPassword =
            findViewById(R.id.etConfirmPassword)

        btnResetPassword =
            findViewById(R.id.btnResetPassword)

        // Password validation
        val passwordPattern =
            Regex(
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\\$%^&+=!]).{8,}$"
            )

        etNewPassword.doAfterTextChanged { text ->

            val password = text.toString()

            if (password.isEmpty()) {

                etNewPassword.error =
                    "Password is required"

            } else if (!password.matches(passwordPattern)) {

                etNewPassword.error =
                    "Min 8 chars, uppercase, lowercase, number & special character"

            } else {

                etNewPassword.error = null
            }
        }

        // Reset Password button
        btnResetPassword.setOnClickListener {

            val newPassword =
                etNewPassword.text
                    .toString()
                    .trim()

            val confirmPassword =
                etConfirmPassword.text
                    .toString()
                    .trim()

            // Validate new password
            if (newPassword.isEmpty()) {

                etNewPassword.error =
                    "Enter New Password"

                return@setOnClickListener
            }

            if (!newPassword.matches(passwordPattern)) {

                etNewPassword.error =
                    "Password must contain uppercase, lowercase, number and special character"

                return@setOnClickListener
            }

            // Validate confirm password
            if (confirmPassword.isEmpty()) {

                etConfirmPassword.error =
                    "Confirm Password"

                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {

                etConfirmPassword.error =
                    "Passwords do not match"

                return@setOnClickListener
            }

            // Make sure email and OTP are available
            if (email.isEmpty()) {

                Toast.makeText(
                    this,
                    "Email information is missing",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (otp.isEmpty()) {

                Toast.makeText(
                    this,
                    "OTP information is missing",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // Disable button while resetting
            btnResetPassword.isEnabled = false

            userViewModel
                .resetPasswordWithOtp(
                    email,
                    otp,
                    newPassword
                )
                .observe(this) { response ->

                    btnResetPassword.isEnabled = true

                    if (
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        Toast.makeText(
                            this,
                            "Password Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Go to loginscreen
                        val intent = Intent(
                            this,
                            LoginActivity::class.java
                        )

                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)

                        finish()

                    } else {

                        val message =
                            response.body()?.message
                                ?: "Password Update Failed"

                        Toast.makeText(
                            this,
                            message,
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

