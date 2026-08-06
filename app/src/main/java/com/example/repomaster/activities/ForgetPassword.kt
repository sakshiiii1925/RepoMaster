package com.example.repomaster.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.repomaster.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.viewmodel.UserViewModel
import android.widget.Toast
import androidx.lifecycle.observe
import android.content.Intent
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
class ForgetPassword : AppCompatActivity() {
    private lateinit var etEmail: TextInputEditText
    private lateinit var userViewModel: UserViewModel
    private lateinit var btnVerifyEmail: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        //toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        supportActionBar?.title = "Forgot Password"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        etEmail = findViewById(R.id.etEmail)
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail)
        btnVerifyEmail.setOnClickListener {

            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Enter your registered email"
                return@setOnClickListener
            }

            userViewModel.verifyEmail(email).observe(this) { response ->

                if(response.isSuccessful && response.body()?.exists == true){

                    Toast.makeText(
                        this,
                        "Email Verified",
                        Toast.LENGTH_SHORT
                    ).show()


                    val intent = Intent(
                        this,
                        ResetPassword::class.java
                    )

                    intent.putExtra(
                        "email",
                        email
                    )

                    startActivity(intent)

                }
                else{

                    Toast.makeText(
                        this,
                        "Email not found",
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