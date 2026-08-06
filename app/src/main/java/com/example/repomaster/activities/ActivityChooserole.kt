package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.repomaster.R
import android.view.animation.AnimationUtils
import android.widget.ImageView

class ActivityChooserole : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chooserole)
        val imgLogo = findViewById<ImageView>(R.id.imgLogo)

        val animation =
            AnimationUtils.loadAnimation(
                this,
                R.anim.logo_flip
            )

        imgLogo.startAnimation(animation)
        val cardAdmin=findViewById<CardView>(R.id.cardAdmin)
        val cardUser=findViewById<CardView>(R.id.cardUser)
        cardAdmin.setOnClickListener {
            val intent= Intent(this, AdminRegistrationActivity::class.java)
            startActivity(intent)
        }
        cardUser.setOnClickListener {
            val intent= Intent(this, UserRegistration::class.java)
            startActivity(intent)
        }

    }
}