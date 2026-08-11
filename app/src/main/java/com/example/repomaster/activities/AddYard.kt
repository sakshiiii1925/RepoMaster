
package com.example.repomaster.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.models.Yard
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.YardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddYard : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var edtYardName: TextInputEditText
    private lateinit var edtYardAddress: TextInputEditText
    private lateinit var edtYardManager: TextInputEditText
    private lateinit var edtYardContact: TextInputEditText

    private lateinit var btnSaveYard: MaterialButton

    private lateinit var yardViewModel: YardViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_yard)

        // =========================
        // Toolbar
        // =========================

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title = "Add Yard"

        // =========================
        // Initialize Views
        // =========================

        edtYardName =
            findViewById(R.id.edtYardName)

        edtYardAddress =
            findViewById(R.id.edtYardAddress)

        edtYardManager =
            findViewById(R.id.edtYardManager)

        edtYardContact =
            findViewById(R.id.edtYardContact)

        btnSaveYard =
            findViewById(R.id.btnSaveYard)

        // =========================
        // Session
        // =========================

        sessionManager =
            SessionManager(this)

        // =========================
        // ViewModel
        // =========================

        yardViewModel =
            ViewModelProvider(this)[YardViewModel::class.java]

        // =========================
        // Save Button
        // =========================

        btnSaveYard.setOnClickListener {

            saveYard()
        }

        // =========================
        // Observe Add Response
        // =========================

        yardViewModel.addYardResponse
            .observe(this) { response ->

                btnSaveYard.isEnabled = true

                if (response.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Yard added successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to add yard",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveYard() {

        val yardName =
            edtYardName.text
                ?.toString()
                ?.trim()

        val yardAddress =
            edtYardAddress.text
                ?.toString()
                ?.trim()

        val yardManager =
            edtYardManager.text
                ?.toString()
                ?.trim()

        val yardContact =
            edtYardContact.text
                ?.toString()
                ?.trim()

        // =========================
        // Validate Yard Name
        // =========================

        if (yardName.isNullOrEmpty()) {

            edtYardName.error =
                "Enter yard name"

            edtYardName.requestFocus()

            return
        }

        // =========================
        // Get Agency ID
        // =========================

        val agencyId =
            sessionManager.getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // =========================
        // Create Yard
        // =========================

        val yard = Yard(

            id = null,

            yardName = yardName,

            yardAddress =
                yardAddress?.ifEmpty { null },

            yardManagerName =
                yardManager?.ifEmpty { null },

            yardContactNo =
                yardContact?.ifEmpty { null },

            agencyId = agencyId
        )

        // =========================
        // Disable Button
        // =========================

        btnSaveYard.isEnabled = false

        // =========================
        // API Call
        // =========================

        yardViewModel.addYard(yard)
    }

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}