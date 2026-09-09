package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.*
import com.example.repomaster.models.PaymentUpdateRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.api.InvoiceApi
import com.example.repomaster.repository.InvoiceRepository
import com.example.repomaster.utils.Constants
import com.example.repomaster.viewmodel.InvoiceViewModel
import com.example.repomaster.viewmodel.InvoiceViewModelFactory
import retrofit2.Retrofit
import androidx.appcompat.app.AlertDialog
import retrofit2.converter.gson.GsonConverterFactory
import com.example.repomaster.models.Invoice
import android.text.TextWatcher
import android.app.DatePickerDialog
import com.google.android.material.textfield.TextInputEditText
import com.example.repomaster.models.PaymentCreateRequest
import com.example.repomaster.utils.SessionManager
import java.util.Calendar
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.adapter.PaymentHistoryAdapter
import com.example.repomaster.utils.PdfReportGenerator
import android.text.Editable

class InvoiceDetailsActivity : AppCompatActivity() {

    private lateinit var invoiceViewModel: InvoiceViewModel
    private lateinit var recyclerPaymentHistory: RecyclerView
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter
    private lateinit var progressInvoiceDetails: View
    private var currentInvoice: Invoice? = null
    private var invoiceId: Long = -1L
    private lateinit var txtDpd: TextView
    private var isSettingDpdPercent = false
    private lateinit var txtDpdInvoiceAmount: TextView
    private lateinit var txtDpdExtraCharge: TextView
    private lateinit var txtDpdTotalAmount: TextView
    private lateinit var etDpdPercent: TextInputEditText
    private lateinit var paymentHistorySection: LinearLayout
    private lateinit var btnPaymentHistory: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_invoice_details
        )
        txtDpd =
            findViewById(R.id.txtDpd)

        txtDpdInvoiceAmount =
            findViewById(R.id.txtDpdInvoiceAmount)

        txtDpdExtraCharge =
            findViewById(R.id.txtDpdExtraCharge)

        txtDpdTotalAmount =
            findViewById(R.id.txtDpdTotalAmount)

        etDpdPercent =
            findViewById(R.id.etDpdPercent)

        setupDpdCalculation()
        paymentHistorySection =
            findViewById(R.id.paymentHistorySection)

        btnPaymentHistory =
            findViewById(R.id.btnPaymentHistory)

        paymentHistorySection.visibility =
            View.GONE
        recyclerPaymentHistory =
            findViewById(R.id.recyclerPaymentHistory)

        recyclerPaymentHistory.layoutManager =
            LinearLayoutManager(this)

        paymentHistoryAdapter =
            PaymentHistoryAdapter(emptyList()) { payment ->

                payment.id?.let { id ->

                    showDeletePaymentConfirmation(id)
                }
            }

        recyclerPaymentHistory.adapter =
            paymentHistoryAdapter
        val btnGeneratePdf =
            findViewById<Button>(R.id.btnGeneratePdf)

        btnGeneratePdf.setOnClickListener {

            Toast.makeText(
                this,
                "Generate PDF button clicked",
                Toast.LENGTH_SHORT
            ).show()

            val invoice = currentInvoice

            if (invoice == null) {

                Toast.makeText(
                    this,
                    "Invoice data not loaded",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            generateInvoicePdf(invoice)
        }

        progressInvoiceDetails =
            findViewById(R.id.progressInvoiceDetails)

        invoiceId =
            intent.getLongExtra(
                "invoiceId",
                -1L
            )

        if (invoiceId == -1L) {

            Toast.makeText(
                this,
                "Invoice ID not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setupSaveDpdButton()
        setupViewModel()
        setupDeleteButton()
        observeInvoice()
        setupAddPaymentButton()
        setupPaymentHistoryButton()
        loadInvoice()
    }

    private fun setupViewModel() {

        val retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()

        val api =
            retrofit.create(
                InvoiceApi::class.java
            )

        val repository =
            InvoiceRepository(api)

        invoiceViewModel =
            ViewModelProvider(
                this,
                InvoiceViewModelFactory(repository)
            )[InvoiceViewModel::class.java]
    }

    private fun loadInvoice() {

        invoiceViewModel.getInvoiceById(
            invoiceId
        )
    }

    private fun observeInvoice() {

        invoiceViewModel.loading.observe(this) { loading ->

            progressInvoiceDetails.visibility =
                if (loading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
        invoiceViewModel.deleteSuccess.observe(
                this
                ) { success ->

            if (success == true) {

                Toast.makeText(
                    this,
                    "Invoice deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        invoiceViewModel.invoice.observe(this) { invoice ->

            if (invoice != null) {

                // IMPORTANT
                currentInvoice = invoice

                displayInvoice(invoice)

                Toast.makeText(
                    this,
                    "Invoice loaded successfully",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Invoice received as NULL",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        invoiceViewModel.error.observe(this) { error ->

            if (!error.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        invoiceViewModel.paymentUpdated.observe(this) { invoice ->

            if (invoice != null) {

                currentInvoice = invoice

                displayInvoice(invoice)

                Toast.makeText(
                    this,
                    "Payment updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        invoiceViewModel.payments.observe(this) { payments ->

            paymentHistoryAdapter.updateList(
                payments
            )
        }
        invoiceViewModel.paymentAdded.observe(this) { payment ->

            if (payment != null) {

                Toast.makeText(
                    this,
                    "Payment added successfully",
                    Toast.LENGTH_SHORT
                ).show()

                invoiceViewModel.getInvoiceById(
                    invoiceId
                )

                if (paymentHistorySection.visibility == View.VISIBLE) {

                    invoiceViewModel.getPaymentHistory(
                        invoiceId
                    )
                }
            }
        }
        invoiceViewModel.paymentDeleted.observe(this) { deleted ->

            if (deleted == true) {

                Toast.makeText(
                    this,
                    "Payment deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()

                invoiceViewModel.getInvoiceById(
                    invoiceId
                )

                if (paymentHistorySection.visibility == View.VISIBLE) {

                    invoiceViewModel.getPaymentHistory(
                        invoiceId
                    )
                }
            }
        }
        invoiceViewModel.dpdUpdated.observe(this) { invoice ->

            if (invoice != null) {

                currentInvoice = invoice

                displayInvoice(invoice)

                Toast.makeText(
                    this,
                    "DPD charge updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun displayInvoice(
        invoice: Invoice
    ) {

        findViewById<TextView>(
            R.id.txtInvoiceNumber
        ).text =
            "Invoice Number: ${invoice.invoiceNumber ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtInvoiceDate
        ).text =
            "Invoice Date: ${invoice.invoiceDate ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtCustomerName
        ).text =
            "Customer Name: ${invoice.customerName ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtVehicleNumber
        ).text =
            "Vehicle Number: ${invoice.vehicleNumber ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtLoanNumber
        ).text =
            "Loan Number: ${invoice.loanNumber ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtVehicleType
        ).text =
            "Vehicle Type: ${invoice.vehicleType ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtVehicleMake
        ).text =
            "Vehicle Make: ${invoice.vehicleMake ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtVehicleModel
        ).text =
            "Vehicle Model: ${invoice.vehicleModel ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtEngineNumber
        ).text =
            "Engine Number: ${invoice.engineNumber ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtChassisNumber
        ).text =
            "Chassis Number: ${invoice.chassisNumber ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtBasicAmount
        ).text =
            "Basic Amount: ₹${invoice.totalBasic ?: 0.0}"

        findViewById<TextView>(
            R.id.txtCgst
        ).text =
            "CGST: ₹${invoice.cgst ?: 0.0}"

        findViewById<TextView>(
            R.id.txtSgst
        ).text =
            "SGST: ₹${invoice.sgst ?: 0.0}"

        findViewById<TextView>(
            R.id.txtIgst
        ).text =
            "IGST: ₹${invoice.igst ?: 0.0}"

        findViewById<TextView>(
            R.id.txtGst
        ).text =
            "GST: ₹${invoice.gst ?: 0.0}"

        findViewById<TextView>(
            R.id.txtInvoiceTotal
        ).text =
            "Invoice Total: ₹${invoice.invoiceTotal ?: 0.0}"

        findViewById<TextView>(
            R.id.txtPaymentStatus
        ).text =
            "Payment Status: ${invoice.paymentStatus ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtPaymentDate
        ).text =
            "Payment Date: ${invoice.paymentDate ?: "N/A"}"

        findViewById<TextView>(
            R.id.txtPaymentReceived
        ).text =
            "Payment Received: ₹${invoice.paymentReceived ?: 0.0}"
        val invoiceTotal =
            invoice.invoiceTotal ?: 0.0

        val paymentReceived =
            invoice.paymentReceived ?: 0.0

        val dpd =
            invoice.dpd ?: 0

        val percent =
            etDpdPercent.text
                ?.toString()
                ?.trim()
                ?.toDoubleOrNull()
                ?: 0.0

        val dpdExtraCharge =
            invoiceTotal *
                    (percent / 100.0) *
                    dpd

        val totalWithDpd =
            invoiceTotal + dpdExtraCharge

        val remainingAmount =
            (totalWithDpd - paymentReceived)
                .coerceAtLeast(0.0)

        findViewById<TextView>(
            R.id.txtPaymentInvoiceTotal
        ).text =
            "₹%.2f".format(
                totalWithDpd
            )

        findViewById<TextView>(
            R.id.txtPaymentReceived
        ).text =
            "₹%.2f".format(
                paymentReceived
            )

        findViewById<TextView>(
            R.id.txtRemainingAmount
        ).text =
            "Remaining Amount: ₹%.2f".format(
                remainingAmount
            )

        findViewById<TextView>(
            R.id.txtPaymentStatus
        ).text =
            when {
                paymentReceived <= 0 -> "Pending"
                paymentReceived < totalWithDpd -> "Partial"
                else -> "Paid"
            }

        findViewById<TextView>(
            R.id.txtRemarks
        ).text =
            "Remarks: ${invoice.remarks ?: "N/A"}"
        txtDpd.text =
            "${invoice.dpd ?: 0} Days"

        txtDpdInvoiceAmount.text =
            "₹%.2f".format(
                invoice.invoiceTotal ?: 0.0
            )
        isSettingDpdPercent = true

        etDpdPercent.setText(
            if ((invoice.dpdChargePercent ?: 0.0) > 0.0) {
                invoice.dpdChargePercent.toString()
            } else {
                ""
            }
        )

        isSettingDpdPercent = false

        calculateDpdCharge()
    }

    //generate pdf
    private fun generateInvoicePdf(invoice: Invoice) {

        Toast.makeText(
            this,
            "Starting PDF generation...",
            Toast.LENGTH_SHORT
        ).show()

        PdfReportGenerator(this)
            .generateInvoicePdf(invoice)
    }
    //elete invoice
    private fun setupDeleteButton() {

        findViewById<Button>(
            R.id.btnDeleteInvoice
        ).setOnClickListener {

            showDeleteConfirmation()
        }
    }
    //Diaglogue box
    private fun showDeleteConfirmation() {

        AlertDialog.Builder(this)
            .setTitle("Delete Invoice")
            .setMessage(
                "Are you sure you want to delete this invoice?"
            )
            .setPositiveButton("Delete") { _, _ ->

                deleteInvoice()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun deleteInvoice() {

        if (invoiceId == -1L) {

            Toast.makeText(
                this,
                "Invalid invoice ID",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        invoiceViewModel.deleteInvoice(invoiceId)
    }

    private fun showAddPaymentDialog(invoice: Invoice) {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_add_payment,
                null
            )

        val txtInvoiceTotal =
            dialogView.findViewById<TextView>(
                R.id.txtInvoiceTotal
            )

        val txtAlreadyPaid =
            dialogView.findViewById<TextView>(
                R.id.txtAlreadyPaid
            )

        val txtRemainingAmount =
            dialogView.findViewById<TextView>(
                R.id.txtRemainingAmount
            )

        val etPaymentDate =
            dialogView.findViewById<TextInputEditText>(
                R.id.etPaymentDate
            )

        val etPaymentAmount =
            dialogView.findViewById<TextInputEditText>(
                R.id.etPaymentAmount
            )

        val etPaymentRemarks =
            dialogView.findViewById<TextInputEditText>(
                R.id.etPaymentRemarks
            )

        val invoiceTotal =
            invoice.invoiceTotal ?: 0.0

        val alreadyPaid =
            invoice.paymentReceived ?: 0.0

        val dpd =
            invoice.dpd ?: 0

        val percent =
            etDpdPercent.text
                ?.toString()
                ?.trim()
                ?.toDoubleOrNull()
                ?: 0.0

        val dpdExtraCharge =
            invoiceTotal *
                    (percent / 100.0) *
                    dpd

        val totalWithDpd =
            roundMoney(
                invoiceTotal + dpdExtraCharge
            )

        val currentRemaining =
            roundMoney(
                (totalWithDpd - alreadyPaid)
                    .coerceAtLeast(0.0)
            )

        txtInvoiceTotal.text =
            "Total With DPD Charge: ₹%.2f".format(
                totalWithDpd
            )

        txtAlreadyPaid.text =
            "Already Paid: ₹%.2f".format(alreadyPaid)

        txtRemainingAmount.text =
            "Remaining: ₹%.2f".format(currentRemaining)


        // Default today's date
        val calendar = Calendar.getInstance()

        etPaymentDate.setText(
            String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )


        // Date picker
        etPaymentDate.setOnClickListener {

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    val date =
                        String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            day
                        )

                    etPaymentDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        // Update remaining amount while typing
        etPaymentAmount.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val newPayment =
                        s?.toString()
                            ?.trim()
                            ?.toDoubleOrNull()
                            ?: 0.0

                    val newRemaining =
                        roundMoney(
                            (currentRemaining - newPayment)
                                .coerceAtLeast(0.0)
                        )

                    txtRemainingAmount.text =
                        "Remaining: ₹%.2f".format(
                            newRemaining
                        )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )


        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Add Payment")
                .setView(dialogView)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Add Payment",
                    null
                )
                .create()


        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val amount =
                    etPaymentAmount.text
                        ?.toString()
                        ?.trim()
                        ?.toDoubleOrNull()

                if (amount == null || amount <= 0) {

                    etPaymentAmount.error =
                        "Enter valid payment amount"

                    return@setOnClickListener
                }


                // Prevent payment greater than remaining
                val roundedAmount =
                    roundMoney(amount)

                if (roundedAmount > currentRemaining) {

                    etPaymentAmount.error =
                        "Payment cannot exceed remaining amount"

                    return@setOnClickListener
                }

                val paymentDate =
                    etPaymentDate.text
                        ?.toString()
                        ?.trim()

                if (paymentDate.isNullOrEmpty()) {

                    etPaymentDate.error =
                        "Select payment date"

                    return@setOnClickListener
                }


                val remarks =
                    etPaymentRemarks.text
                        ?.toString()
                        ?.trim()


                val session =
                    SessionManager(this)


                val request =
                    PaymentCreateRequest(
                        paymentDate = paymentDate,
                        paymentAmount = roundedAmount,
                        remarks = remarks,
                        createdBy =
                            session.getUserEmail()
                    )


                invoiceViewModel.addPayment(
                    invoiceId,
                    request
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }
    private fun setupAddPaymentButton() {

        findViewById<Button>(
            R.id.btnAddPayment
        ).setOnClickListener {

            val invoice = currentInvoice

            if (invoice == null) {

                Toast.makeText(
                    this,
                    "Invoice data not loaded",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            showAddPaymentDialog(invoice)
        }
    }
    private fun showDeletePaymentConfirmation(
        paymentId: Long
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Payment")
            .setMessage(
                "Are you sure you want to delete this payment?"
            )
            .setPositiveButton("Delete") { _, _ ->

                invoiceViewModel.deletePayment(
                    paymentId
                )
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }
    private fun setupDpdCalculation() {

        etDpdPercent.addTextChangedListener(
            object : TextWatcher {

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

                    if (!isSettingDpdPercent) {
                        calculateDpdCharge()
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun calculateDpdCharge() {

        val invoice =
            currentInvoice ?: return

        val invoiceAmount =
            invoice.invoiceTotal ?: 0.0

        val dpd =
            invoice.dpd ?: 0

        val percent =
            etDpdPercent.text
                ?.toString()
                ?.trim()
                ?.toDoubleOrNull()
                ?: invoice.dpdChargePercent
                ?: 0.0

        val extraCharge =
            invoiceAmount *
                    (percent / 100.0) *
                    dpd

        val totalAmount =
            roundMoney(
                invoiceAmount + extraCharge
            )

        val paymentReceived =
            roundMoney(
                invoice.paymentReceived ?: 0.0
            )

        val remainingAmount =
            roundMoney(
                (totalAmount - paymentReceived)
                    .coerceAtLeast(0.0)
            )

        /*
         * DPD
         */
        txtDpd.text =
            "$dpd Days"

        txtDpdInvoiceAmount.text =
            "₹%.2f".format(invoiceAmount)

        txtDpdExtraCharge.text =
            "₹%.2f".format(
                roundMoney(extraCharge)
            )

        txtDpdTotalAmount.text =
            "₹%.2f".format(totalAmount)

        /*
         * PAYMENT SUMMARY
         */
        findViewById<TextView>(
            R.id.txtPaymentInvoiceTotal
        ).text =
            "₹%.2f".format(totalAmount)

        findViewById<TextView>(
            R.id.txtPaymentReceived
        ).text =
            "₹%.2f".format(paymentReceived)

        findViewById<TextView>(
            R.id.txtRemainingAmount
        ).text =
            "Remaining Amount: ₹%.2f".format(
                remainingAmount
            )

        findViewById<TextView>(
            R.id.txtPaymentStatus
        ).text =
            when {

                paymentReceived <= 0.0 ->
                    "Pending"

                remainingAmount <= 0.0 ->
                    "Paid"

                else ->
                    "Partial"
            }
    }


    private fun setupPaymentHistoryButton() {

        btnPaymentHistory.setOnClickListener {

            if (paymentHistorySection.visibility == View.GONE) {

                // Show section
                paymentHistorySection.visibility =
                    View.VISIBLE

                btnPaymentHistory.text =
                    "Hide Payment History"

                // Load history only when user opens it
                invoiceViewModel.getPaymentHistory(
                    invoiceId
                )

            } else {

                // Hide section
                paymentHistorySection.visibility =
                    View.GONE

                btnPaymentHistory.text =
                    "Payment History"
            }
        }
    }
    private fun setupSaveDpdButton() {

        findViewById<Button>(
            R.id.btnSaveDpdPercent
        ).setOnClickListener {

            val percent =
                etDpdPercent.text
                    ?.toString()
                    ?.trim()
                    ?.toDoubleOrNull()

            if (percent == null) {

                etDpdPercent.error =
                    "Enter DPD percentage"

                return@setOnClickListener
            }

            if (percent < 0) {

                etDpdPercent.error =
                    "Percentage cannot be negative"

                return@setOnClickListener
            }

            if (percent > 100) {

                etDpdPercent.error =
                    "Percentage cannot be greater than 100"

                return@setOnClickListener
            }

            invoiceViewModel.updateDpdCharge(
                invoiceId,
                percent
            )
        }
    }
    override fun onResume() {
        super.onResume()

        if (::invoiceViewModel.isInitialized && invoiceId != -1L) {

            loadInvoice()

            // Only reload payment history if the section is visible
            if (paymentHistorySection.visibility == View.VISIBLE) {

                invoiceViewModel.getPaymentHistory(
                    invoiceId
                )
            }
        }
    }
    private fun roundMoney(value: Double): Double {
        return kotlin.math.round(value * 100.0) / 100.0
    }
}