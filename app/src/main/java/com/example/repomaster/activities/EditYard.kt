package com.example.repomaster.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.models.Yard
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.YardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class EditYard : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var edtYardName: EditText
    private lateinit var edtYardAddress: EditText
    private lateinit var edtYardManager: EditText
    private lateinit var edtYardContact: EditText
    private lateinit var btnUpdateYard: MaterialButton

    private lateinit var yardViewModel: YardViewModel
    private lateinit var sessionManager: SessionManager

    private var yardId: Long = 0L
    private lateinit var agencyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_yard)

        // -------------------------
        // Initialize Views
        // -------------------------

        toolbar = findViewById(R.id.toolbar)

        edtYardName = findViewById(R.id.edtYardName)
        edtYardAddress = findViewById(R.id.edtYardAddress)
        edtYardManager = findViewById(R.id.edtYardManager)
        edtYardContact = findViewById(R.id.edtYardContact)

        btnUpdateYard = findViewById(R.id.btnUpdateYard)

        // -------------------------
        // Toolbar
        // -------------------------

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Yard"

        // -------------------------
        // Session
        // -------------------------

        sessionManager = SessionManager(this)

        // -------------------------
        // ViewModel
        // -------------------------

        yardViewModel =
            ViewModelProvider(this)[YardViewModel::class.java]

        // -------------------------
        // Get Intent Data
        // -------------------------

        yardId = intent.getLongExtra(
            "yard_id",
            0L
        )

        val yardName =
            intent.getStringExtra("yard_name")

        val yardAddress =
            intent.getStringExtra("yard_address")

        val yardManagerName =
            intent.getStringExtra("yard_manager_name")

        val yardContactNo =
            intent.getStringExtra("yard_contact_no")

        agencyId =
            intent.getStringExtra("agency_id")
                ?: sessionManager.getAgencyId()
                        ?: ""

        // -------------------------
        // Set Existing Data
        // -------------------------

        edtYardName.setText(yardName ?: "")

        edtYardAddress.setText(yardAddress ?: "")

        edtYardManager.setText(
            yardManagerName ?: ""
        )

        edtYardContact.setText(
            yardContactNo ?: ""
        )

        // -------------------------
        // Update Button
        // -------------------------

        btnUpdateYard.setOnClickListener {

            updateYard()
        }

        // -------------------------
        // Observe Update Response
        // -------------------------

        yardViewModel.updateYardResponse
            .observe(this) { response ->

                if (response.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Yard updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to update yard",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun updateYard() {

        val yardName =
            edtYardName.text.toString().trim()

        val yardAddress =
            edtYardAddress.text.toString().trim()

        val yardManager =
            edtYardManager.text.toString().trim()

        val yardContact =
            edtYardContact.text.toString().trim()

        // -------------------------
        // Validation
        // -------------------------

        if (yardName.isEmpty()) {

            edtYardName.error =
                "Enter yard name"

            edtYardName.requestFocus()

            return
        }

        if (agencyId.isEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (yardId == 0L) {

            Toast.makeText(
                this,
                "Invalid yard ID",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // -------------------------
        // Create Updated Yard
        // -------------------------

        val updatedYard = Yard(

            id = yardId,

            yardName = yardName,

            yardAddress =
                yardAddress.ifEmpty { null },

            yardManagerName =
                yardManager.ifEmpty { null },

            yardContactNo =
                yardContact.ifEmpty { null },

            agencyId = agencyId
        )

        // -------------------------
        // Call API
        // -------------------------

        yardViewModel.updateYard(
            yardId,
            agencyId,
            updatedYard
        )
    }

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}