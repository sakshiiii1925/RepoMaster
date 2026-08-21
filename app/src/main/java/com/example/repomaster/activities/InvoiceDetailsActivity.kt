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
    private lateinit var paymentHistorySection: LinearLayout
    private lateinit var btnPaymentHistory: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_invoice_details
        )
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


        setupViewModel()
        setupDeleteButton()
        observeInvoice()
        setupPaymentButton()
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

        val remainingAmount =
            (invoiceTotal - paymentReceived)
                .coerceAtLeast(0.0)
        findViewById<TextView>(
            R.id.txtPaymentInvoiceTotal
        ).text =
            "₹%.2f".format(
                invoice.invoiceTotal ?: 0.0
            )
        findViewById<TextView>(
            R.id.txtPaymentStatus
        ).text =
            invoice.paymentStatus ?: "Pending"
        findViewById<TextView>(
            R.id.txtRemainingAmount
        ).text =
            "Remaining Amount: ₹%.2f".format(
                remainingAmount
            )
        findViewById<TextView>(
            R.id.txtRemarks
        ).text =
            "Remarks: ${invoice.remarks ?: "N/A"}"
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
    //diaglogue box
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
    private fun setupPaymentButton() {

        findViewById<Button>(
            R.id.btnUpdatePayment
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

            showPaymentDialog(invoice)
        }
    }
    private fun showPaymentDialog(
        invoice: Invoice
    ) {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_update_payment,
                null
            )

        val txtDialogInvoiceTotal =
            dialogView.findViewById<TextView>(
                R.id.txtDialogInvoiceTotal
            )

        val txtRemainingAmount =
            dialogView.findViewById<TextView>(
                R.id.txtRemainingAmount
            )

        val etPaymentReceived =
            dialogView.findViewById<EditText>(
                R.id.etPaymentReceived
            )

        val etPaymentDate =
            dialogView.findViewById<EditText>(
                R.id.etPaymentDate
            )
        val txtPaymentStatusPreview =
            dialogView.findViewById<TextView>(
                R.id.txtPaymentStatusPreview
            )



        val invoiceTotal =
            invoice.invoiceTotal ?: 0.0

        val alreadyPaid =
            invoice.paymentReceived ?: 0.0


        txtDialogInvoiceTotal.text =
            "Invoice Total: ₹%.2f".format(
                invoiceTotal
            )


        val remaining =
            invoiceTotal - alreadyPaid

        txtRemainingAmount.text =
            "Remaining: ₹%.2f".format(
                remaining.coerceAtLeast(0.0)
            )


        etPaymentReceived.setText(
            if (alreadyPaid > 0)
                alreadyPaid.toString()
            else
                ""
        )
        etPaymentReceived.addTextChangedListener(
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

                    val payment =
                        s?.toString()
                            ?.toDoubleOrNull()
                            ?: 0.0

                    val remaining =
                        invoiceTotal - payment

                    txtRemainingAmount.text =
                        "Remaining: ₹%.2f".format(
                            remaining.coerceAtLeast(0.0)
                        )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )


        etPaymentDate.setText(
            invoice.paymentDate ?: ""
        )





        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Update Payment")
                .setView(dialogView)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Save Payment",
                    null
                )
                .create()


        dialog.setOnShowListener {

            val saveButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            saveButton.setOnClickListener {

                val payment =
                    etPaymentReceived.text
                        .toString()
                        .toDoubleOrNull()

                if (payment == null) {

                    etPaymentReceived.error =
                        "Enter payment amount"

                    return@setOnClickListener
                }


                if (payment < 0) {

                    etPaymentReceived.error =
                        "Invalid payment amount"

                    return@setOnClickListener
                }


                if (payment > invoiceTotal) {

                    etPaymentReceived.error =
                        "Payment cannot exceed invoice total"

                    return@setOnClickListener
                }


                val paymentDate =
                    etPaymentDate.text
                        .toString()
                        .trim()


                val status =
                    when {
                        payment == 0.0 ->
                            "Pending"

                        payment < invoiceTotal ->
                            "Partial"

                        payment == invoiceTotal ->
                            "Paid"

                        else ->
                            "Pending"
                    }
                txtPaymentStatusPreview.text =
                    "Status: $status"

                val request =
                    PaymentUpdateRequest(
                        paymentReceived =
                            payment,

                        paymentDate =
                            paymentDate.ifEmpty {
                                null
                            },

                        paymentStatus =
                            status
                    )


                invoiceViewModel.updatePayment(
                    invoiceId,
                    request
                )


                dialog.dismiss()
            }
        }


        dialog.show()
    }
    private fun showAddPaymentDialog() {

        val dialogView =
            layoutInflater.inflate(
                R.layout.dialog_add_payment,
                null
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

        val dialog =
            AlertDialog.Builder(this)
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
                        paymentAmount = amount,
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

            showAddPaymentDialog()
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
}