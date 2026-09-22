
package com.example.repomaster.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.models.Yard
import com.example.repomaster.utils.SessionManager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.example.repomaster.viewmodel.YardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddYard : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var actvYard: AutoCompleteTextView
    private lateinit var tilOtherYardName: TextInputLayout
    private lateinit var edtOtherYardName: TextInputEditText
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

        actvYard =
            findViewById(R.id.actvYard)

        tilOtherYardName =
            findViewById(R.id.tilOtherYardName)

        edtOtherYardName =
            findViewById(R.id.edtOtherYardName)

        edtYardAddress =
            findViewById(R.id.edtYardAddress)

        edtYardManager =
            findViewById(R.id.edtYardManager)

        edtYardContact =
            findViewById(R.id.edtYardContact)

        btnSaveYard =
            findViewById(R.id.btnSaveYard)
        //dropdown
        val yardList = listOf(
            "Yard A",
            "Yard B",
            "Yard C",
            "Other"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            yardList
        )

        actvYard.setAdapter(adapter)

        actvYard.setOnItemClickListener { _, _, position, _ ->

            val selectedYard = yardList[position]

            if (selectedYard == "Other") {

                tilOtherYardName.visibility = View.VISIBLE

                edtOtherYardName.requestFocus()

            } else {

                tilOtherYardName.visibility = View.GONE

                edtOtherYardName.setText("")
            }
        }

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

        val selectedYard =
            actvYard.text
                ?.toString()
                ?.trim()

        val otherYardName =
            edtOtherYardName.text
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
        // Validate Yard Selection
        // =========================

        if (selectedYard.isNullOrEmpty()) {

            actvYard.error = "Select yard"

            actvYard.requestFocus()

            return
        }

        // =========================
        // Get Final Yard Name
        // =========================

        val finalYardName =
            if (selectedYard == "Other") {

                if (otherYardName.isNullOrEmpty()) {

                    edtOtherYardName.error =
                        "Enter yard name"

                    edtOtherYardName.requestFocus()

                    return
                }

                otherYardName

            } else {

                selectedYard
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

            yardName = finalYardName,

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
        // API
        // =========================

        yardViewModel.addYard(yard)
    }




    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}