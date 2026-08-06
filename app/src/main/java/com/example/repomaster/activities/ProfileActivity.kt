package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.repomaster.R
import com.example.repomaster.utils.SessionManager
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.appbar.MaterialToolbar

class ProfileActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var toolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        toolbar =
            findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val txtName = findViewById<TextView>(R.id.txtName)
        val txtEmail = findViewById<TextView>(R.id.txtEmail)
        val txtRole = findViewById<TextView>(R.id.txtRole)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)
        val txtMobile = findViewById<TextView>(R.id.txtMobile)
val editprofile=findViewById<Button>(R.id.editprofile)
        editprofile.setOnClickListener {
            val intent= Intent(this, EditUserProfile::class.java)
            startActivity(intent)
        }
        val email = SessionManager(this).getUserEmail()

        userViewModel.getProfile(email).observe(this) { response ->

            if (response.isSuccessful && response.body() != null) {

                val user = response.body()!!

                txtName.text = user.fullName
                txtEmail.text = user.email
                txtMobile.text = user.mobile
                txtRole.text = user.role
                txtStatus.text = user.status

            } else {

                txtName.text = "Profile not found"

            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}