
package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.repomaster.R
import com.example.repomaster.models.PaymentRate
import com.example.repomaster.models.SavePaymentRateRequest
import com.example.repomaster.network.RetrofitClient
import com.example.repomaster.repository.AdminPaymentRepository
import com.example.repomaster.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class PaymentRatesActivity : AppCompatActivity() {

    private lateinit var repository: AdminPaymentRepository
    private lateinit var sessionManager: SessionManager

    private lateinit var edtVehicleType: TextInputEditText
    private lateinit var edtRepoMarkRate: TextInputEditText
    private lateinit var edtParkedRate: TextInputEditText

    private lateinit var btnSaveRate: Button
    private lateinit var btnCancelEdit: Button

    private lateinit var ratesContainer: LinearLayout
    private lateinit var progressRates: ProgressBar
    private lateinit var txtNoRates: TextView
    private lateinit var txtAgency: TextView
    private lateinit var txtFormTitle: TextView
    private lateinit var edtSearchRate: TextInputEditText

    private var allRates: List<PaymentRate> = emptyList()
    private var editingRateId: Long? = null

    private var agencyId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_payment_rates)

        // -----------------------------------------------------
        // Initialize
        // -----------------------------------------------------

        sessionManager = SessionManager(this)

        repository = AdminPaymentRepository(
            RetrofitClient.adminPaymentApi
        )

        val toolbar =
            findViewById<MaterialToolbar>(
                R.id.toolbarPaymentRate
            )

        toolbar.setNavigationOnClickListener {
            finish()
        }

        edtVehicleType =
            findViewById(R.id.edtVehicleType)

        edtRepoMarkRate =
            findViewById(R.id.edtRepoMarkRate)

        edtParkedRate =
            findViewById(R.id.edtParkedRate)

        btnSaveRate =
            findViewById(R.id.btnSaveRate)

        btnCancelEdit =
            findViewById(R.id.btnCancelEdit)

        ratesContainer =
            findViewById(R.id.ratesContainer)

        progressRates =
            findViewById(R.id.progressRates)

        txtNoRates =
            findViewById(R.id.txtNoRates)

        txtAgency =
            findViewById(R.id.txtAgency)

        txtFormTitle =
            findViewById(R.id.txtFormTitle)
        edtSearchRate =
            findViewById(R.id.edtSearchRate)

        // -----------------------------------------------------
        // Get logged-in admin agency
        // -----------------------------------------------------

        agencyId =
            sessionManager
                .getAgencyId()
                ?.trim()
                ?: ""

        if (agencyId.isEmpty()) {

            Toast.makeText(
                this,
                "Agency information not found",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        txtAgency.text =
            "Agency ID: $agencyId"


        // -----------------------------------------------------
        // Buttons
        // -----------------------------------------------------

        btnSaveRate.setOnClickListener {

            saveRate()
        }

        btnCancelEdit.setOnClickListener {

            clearForm()
        }


        // -----------------------------------------------------
        // Load existing rates
        // -----------------------------------------------------
        setupRateSearch()
        loadRates()
    }


    // =========================================================
    // LOAD RATES
    // =========================================================

    private fun loadRates() {

        progressRates.visibility =
            View.VISIBLE

        txtNoRates.visibility =
            View.GONE

        lifecycleScope.launch {

            try {

                val response =
                    repository.getRates(agencyId)

                progressRates.visibility =
                    View.GONE

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (
                        body?.success == true &&
                        !body.data.isNullOrEmpty()
                    ) {

                        allRates =
                            body.data

                        displayRates(
                            allRates
                        )
                    }
                    else {

                        ratesContainer.removeAllViews()

                        txtNoRates.visibility =
                            View.VISIBLE
                    }

                } else {

                    ratesContainer.removeAllViews()

                    Toast.makeText(
                        this@PaymentRatesActivity,
                        "Failed to load payment rates",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                progressRates.visibility =
                    View.GONE

                Toast.makeText(
                    this@PaymentRatesActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // DISPLAY RATES
    // =========================================================

    private fun displayRates(
        rates: List<PaymentRate>
    ) {

        ratesContainer.removeAllViews()

        if (rates.isEmpty()) {

            txtNoRates.text =
                "No payment rate found"

            txtNoRates.visibility =
                View.VISIBLE

            return
        }

        txtNoRates.visibility =
            View.GONE

        for (rate in rates) {

            val card =
                createRateCard(rate)

            ratesContainer.addView(card)
        }
    }


    // =========================================================
    // CREATE RATE CARD
    // =========================================================

    private fun createRateCard(
        rate: PaymentRate
    ): View {

        val card =
            MaterialCardView(this)

        card.radius =
            18f

        card.cardElevation =
            6f

        card.setCardBackgroundColor(
            getColor(android.R.color.white)
        )

        val outer =
            LinearLayout(this)

        outer.orientation =
            LinearLayout.VERTICAL

        outer.setPadding(
            20,
            18,
            20,
            18
        )


        // Vehicle Type

        val vehicleType =
            TextView(this)

        vehicleType.text =
            rate.vehicle_type

        vehicleType.textSize =
            19f

        vehicleType.setTextColor(
            getColor(android.R.color.black)
        )

        vehicleType.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )


        // Repo Mark

        val repoMark =
            TextView(this)

        repoMark.text =
            "Repo Mark: ₹${rate.repo_mark_rate}"

        repoMark.textSize =
            16f

        repoMark.setTextColor(
            getColor(android.R.color.darker_gray)
        )


        // Parked

        val parked =
            TextView(this)

        parked.text =
            "Parked: ₹${rate.parked_rate}"

        parked.textSize =
            16f

        parked.setTextColor(
            getColor(android.R.color.darker_gray)
        )


        // Buttons

        val buttonLayout =
            LinearLayout(this)

        buttonLayout.orientation =
            LinearLayout.HORIZONTAL

        buttonLayout.gravity =
            android.view.Gravity.END

        val editButton =
            Button(this)

        editButton.text =
            "Edit"

        val deleteButton =
            Button(this)

        deleteButton.text =
            "Delete"


        editButton.setOnClickListener {

            startEditing(rate)
        }


        deleteButton.setOnClickListener {

            confirmDelete(rate)
        }


        buttonLayout.addView(
            editButton
        )

        buttonLayout.addView(
            deleteButton
        )


        outer.addView(vehicleType)

        outer.addView(repoMark)

        outer.addView(parked)

        outer.addView(buttonLayout)


        card.addView(outer)


        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            0,
            0,
            0,
            16
        )

        card.layoutParams =
            params

        return card
    }


    // =========================================================
    // SAVE RATE
    // =========================================================

    private fun saveRate() {

        val vehicleType =
            edtVehicleType
                .text
                ?.toString()
                ?.trim()
                ?.uppercase()
                ?: ""

        val repoMarkText =
            edtRepoMarkRate
                .text
                ?.toString()
                ?.trim()
                ?: ""

        val parkedText =
            edtParkedRate
                .text
                ?.toString()
                ?.trim()
                ?: ""


        // -----------------------------------------------------
        // Validation
        // -----------------------------------------------------

        if (vehicleType.isEmpty()) {

            edtVehicleType.error =
                "Vehicle type is required"

            edtVehicleType.requestFocus()

            return
        }

        if (repoMarkText.isEmpty()) {

            edtRepoMarkRate.error =
                "Repo Mark rate is required"

            edtRepoMarkRate.requestFocus()

            return
        }

        if (parkedText.isEmpty()) {

            edtParkedRate.error =
                "Parked rate is required"

            edtParkedRate.requestFocus()

            return
        }


        val repoMarkRate =
            repoMarkText.toDoubleOrNull()

        val parkedRate =
            parkedText.toDoubleOrNull()


        if (repoMarkRate == null ||
            repoMarkRate < 0
        ) {

            edtRepoMarkRate.error =
                "Enter a valid rate"

            return
        }


        if (parkedRate == null ||
            parkedRate < 0
        ) {

            edtParkedRate.error =
                "Enter a valid rate"

            return
        }


        // -----------------------------------------------------
        // Request
        // -----------------------------------------------------

        val request =
            SavePaymentRateRequest(
                agency_id = agencyId,
                vehicle_type = vehicleType,
                repo_mark_rate = repoMarkRate,
                parked_rate = parkedRate
            )


        btnSaveRate.isEnabled =
            false


        lifecycleScope.launch {

            try {

                val response =
                    repository.saveRate(request)

                btnSaveRate.isEnabled =
                    true

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        Toast.makeText(
                            this@PaymentRatesActivity,
                            if (editingRateId == null)
                                "Payment rate added"
                            else
                                "Payment rate updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        clearForm()

                        loadRates()

                    } else {

                        Toast.makeText(
                            this@PaymentRatesActivity,
                            body?.message
                                ?: "Failed to save rate",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@PaymentRatesActivity,
                        "Failed: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                btnSaveRate.isEnabled =
                    true

                Toast.makeText(
                    this@PaymentRatesActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // EDIT
    // =========================================================

    private fun startEditing(
        rate: PaymentRate
    ) {

        editingRateId =
            rate.id

        txtFormTitle.text =
            "Edit Vehicle Type Rate"

        btnSaveRate.text =
            "Update Rate"

        btnCancelEdit.visibility =
            View.VISIBLE

        edtVehicleType.setText(
            rate.vehicle_type
        )

        edtRepoMarkRate.setText(
            rate.repo_mark_rate
        )

        edtParkedRate.setText(
            rate.parked_rate
        )

        edtVehicleType.requestFocus()

        window.decorView.post {

            edtVehicleType
                .parent
                ?.parent
                ?.let {
                    // Form is already near the top.
                }
        }
    }


    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private fun confirmDelete(
        rate: PaymentRate
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Payment Rate")
            .setMessage(
                "Delete ${rate.vehicle_type} payment rate?"
            )
            .setPositiveButton("Delete") { _, _ ->

                deleteRate(rate)
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }


    // =========================================================
    // DELETE
    // =========================================================

    private fun deleteRate(
        rate: PaymentRate
    ) {

        lifecycleScope.launch {

            try {

                val response =
                    repository.deleteRate(
                        rate.id,
                        agencyId
                    )

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true) {

                        Toast.makeText(
                            this@PaymentRatesActivity,
                            "Payment rate deleted",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadRates()

                    } else {

                        Toast.makeText(
                            this@PaymentRatesActivity,
                            body?.message
                                ?: "Delete failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@PaymentRatesActivity,
                        "Delete failed: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@PaymentRatesActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // CLEAR FORM
    // =========================================================

    private fun clearForm() {

        editingRateId =
            null

        edtVehicleType.text =
            null

        edtRepoMarkRate.text =
            null

        edtParkedRate.text =
            null

        txtFormTitle.text =
            "Add Vehicle Type Rate"

        btnSaveRate.text =
            "Save Rate"

        btnCancelEdit.visibility =
            View.GONE
    }
    private fun setupRateSearch() {

        edtSearchRate.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    filterRates(
                        s?.toString()?.trim() ?: ""
                    )
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {
                }
            }
        )
    }
    private fun filterRates(
        query: String
    ) {

        if (query.isEmpty()) {

            displayRates(allRates)

            return
        }

        val filteredRates =
            allRates.filter { rate ->

                rate.vehicle_type
                    .contains(
                        query,
                        ignoreCase = true
                    )
            }

        displayRates(filteredRates)
    }
}

