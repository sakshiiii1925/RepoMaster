package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var etOtp: TextInputEditText
    private lateinit var btnVerifyOtp: MaterialButton

    private lateinit var userViewModel: UserViewModel

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_otp_verification)

        userViewModel =
            ViewModelProvider(this)[UserViewModel::class.java]

        // Get email from ForgetPassword screen
        email = intent.getStringExtra("email") ?: ""

        // Toolbar
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.title = "Verify OTP"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Views
        etOtp = findViewById(R.id.etOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)

        btnVerifyOtp.setOnClickListener {

            val otp = etOtp.text
                .toString()
                .trim()

            // Validate OTP
            if (otp.isEmpty()) {

                etOtp.error = "Enter OTP"

                return@setOnClickListener
            }

            if (!otp.matches(Regex("^\\d{6}$"))) {

                etOtp.error =
                    "OTP must be 6 digits"

                return@setOnClickListener
            }

            btnVerifyOtp.isEnabled = false

            userViewModel
                .verifyPasswordResetOtp(
                    email,
                    otp
                )
                .observe(this) { response ->

                    btnVerifyOtp.isEnabled = true

                    if (
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        Toast.makeText(
                            this,
                            "OTP verified successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Open Reset Password screen
                        val intent = Intent(
                            this,
                            ResetPassword::class.java
                        )

                        intent.putExtra(
                            "email",
                            email
                        )

                        intent.putExtra(
                            "otp",
                            otp
                        )

                        startActivity(intent)

                        finish()

                    } else {

                        val message =
                            response.body()?.message
                                ?: "Invalid OTP"

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