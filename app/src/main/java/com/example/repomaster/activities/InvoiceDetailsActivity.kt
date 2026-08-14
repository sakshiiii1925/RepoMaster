package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.*
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
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.example.repomaster.utils.PdfReportGenerator
import java.io.File
class InvoiceDetailsActivity : AppCompatActivity() {

    private lateinit var invoiceViewModel: InvoiceViewModel

    private lateinit var progressInvoiceDetails: View
    private var currentInvoice: Invoice? = null
    private var invoiceId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_invoice_details
        )
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
    override fun onResume() {
        super.onResume()

        loadInvoice()
    }
}