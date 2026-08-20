package com.example.repomaster.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.models.Invoice
import com.example.repomaster.models.Vehicle
import com.example.repomaster.repository.InvoiceRepository
import com.example.repomaster.api.InvoiceApi
import com.example.repomaster.utils.Constants
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.InvoiceViewModel
import com.example.repomaster.viewmodel.InvoiceViewModelFactory
import com.example.repomaster.viewmodel.HomeViewModel
import com.example.repomaster.viewmodel.HomeViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar

class CreateInvoice : AppCompatActivity() {

    // Invoice ViewModel
    private lateinit var invoiceViewModel: InvoiceViewModel

    // Vehicle ViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val vehicleSuggestions = mutableListOf<String>()

    private lateinit var vehicleSuggestionAdapter: ArrayAdapter<String>
    // Vehicle
    private lateinit var etVehicleNumber: AutoCompleteTextView
    private lateinit var btnSearchVehicle: MaterialButton
    private lateinit var toolbar: MaterialToolbar
    // Customer / Vehicle details
    private lateinit var etCustomerName: TextInputEditText
    private lateinit var etLoanNumber: TextInputEditText
    private lateinit var etVehicleType: TextInputEditText
    private lateinit var etVehicleMake: TextInputEditText
    private lateinit var etVehicleModel: TextInputEditText
    private lateinit var etEngineNumber: TextInputEditText
    private lateinit var etChassisNumber: TextInputEditText
    private lateinit var etCustomerAddress: TextInputEditText

    // Invoice details
    private lateinit var etInvoiceNumber: TextInputEditText
    private lateinit var etInvoiceDate: TextInputEditText
    private lateinit var etInvoiceBank: TextInputEditText
    private lateinit var etInvoiceAddress: TextInputEditText

    // Amount section
    private lateinit var etDescription1: TextInputEditText
    private lateinit var etBasic1Amount: TextInputEditText
    private lateinit var etDescription2: TextInputEditText
    private lateinit var etBasic2Amount: TextInputEditText
private lateinit var txtTotalPreview: TextView
    private lateinit var etGstPercent: TextInputEditText

    private lateinit var etRemarks: TextInputEditText

    private lateinit var btnCreateInvoice: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_invoice)
        toolbar =
            findViewById(R.id.toolbar1)

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.black)
        )
        supportActionBar?.title =
            "Generate Invoice"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeViews()

        setupInvoiceViewModel()

        setupVehicleViewModel()

        setupDatePicker()
        setupVehicleAutocomplete()
        setupVehicleSearch()

        setupCreateInvoiceButton()
        // ADD AUTOMATIC CALCULATION HERE
        setupAmountCalculation(
            etBasic1Amount,
            etBasic2Amount,
            etGstPercent,
            txtTotalPreview
        )
    }


    // ------------------------------------------------
    // INITIALIZE VIEWS
    // ------------------------------------------------

    private fun initializeViews() {

        // Vehicle
        etVehicleNumber =
            findViewById(R.id.etVehicleNumber)

        btnSearchVehicle =
            findViewById(R.id.btnSearchVehicle)

        // Customer
        etCustomerName =
            findViewById(R.id.etCustomerName)

        etLoanNumber =
            findViewById(R.id.etLoanNumber)

        etVehicleType =
            findViewById(R.id.etVehicleType)

        etVehicleMake =
            findViewById(R.id.etVehicleMake)

        etVehicleModel =
            findViewById(R.id.etVehicleModel)

        etEngineNumber =
            findViewById(R.id.etEngineNumber)

        etChassisNumber =
            findViewById(R.id.etChassisNumber)

        etCustomerAddress =
            findViewById(R.id.etCustomerAddress)

        // Invoice
        etInvoiceNumber =
            findViewById(R.id.etInvoiceNumber)

        etInvoiceDate =
            findViewById(R.id.etInvoiceDate)

        etInvoiceBank =
            findViewById(R.id.etInvoiceBank)

        etInvoiceAddress =
            findViewById(R.id.etInvoiceAddress)

        // Amount
        etDescription1 =
            findViewById(R.id.etDescription1)

        etBasic1Amount =
            findViewById(R.id.etBasic1Amount)

        etDescription2 =
            findViewById(R.id.etDescription2)

        etBasic2Amount =
            findViewById(R.id.etBasic2Amount)
        txtTotalPreview=findViewById(R.id.txtTotalPreview)
        etGstPercent =
            findViewById(R.id.etGstPercent)

        etRemarks =
            findViewById(R.id.etRemarks)

        btnCreateInvoice =
            findViewById(R.id.btnCreateInvoice)
    }

    // ------------------------------------------------
    // INVOICE VIEW MODEL
    // ------------------------------------------------

    private fun setupInvoiceViewModel() {

        val retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()

        val api =
            retrofit.create(InvoiceApi::class.java)

        val repository =
            InvoiceRepository(api)

        invoiceViewModel =
            ViewModelProvider(
                this,
                InvoiceViewModelFactory(repository)
            )[InvoiceViewModel::class.java]

        invoiceViewModel.invoice.observe(this) { invoice ->

            if (invoice != null) {

                Toast.makeText(
                    this,
                    "Invoice created successfully",
                    Toast.LENGTH_LONG
                ).show()

                finish()
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
    private fun setupAmountCalculation(
        etBasic1Amount: TextInputEditText,
        etBasic2Amount: TextInputEditText,
        etGstPercent: TextInputEditText,
        txtTotalPreview: TextView
    ) {

        val calculateTotal = {

            val basic1 =
                etBasic1Amount.text
                    ?.toString()
                    ?.toDoubleOrNull()
                    ?: 0.0

            val basic2 =
                etBasic2Amount.text
                    ?.toString()
                    ?.toDoubleOrNull()
                    ?: 0.0

            val gstPercent =
                etGstPercent.text
                    ?.toString()
                    ?.toDoubleOrNull()
                    ?: 0.0

            // Total basic amount
            val totalBasic =
                basic1 + basic2

            // GST amount
            val gst =
                totalBasic * gstPercent / 100

            // Final invoice total
            val invoiceTotal =
                totalBasic + gst

            txtTotalPreview.text =
                "Basic: ₹%.2f\nGST: ₹%.2f\nTotal: ₹%.2f".format(
                    totalBasic,
                    gst,
                    invoiceTotal
                )
        }

        etBasic1Amount.addTextChangedListener(
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
                    calculateTotal()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )

        etBasic2Amount.addTextChangedListener(
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
                    calculateTotal()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )

        etGstPercent.addTextChangedListener(
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
                    calculateTotal()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )
    }

    // ------------------------------------------------
    // VEHICLE VIEW MODEL
    // ------------------------------------------------

    private fun setupVehicleViewModel() {

        val factory =
            HomeViewModelFactory(applicationContext)

        homeViewModel =
            ViewModelProvider(
                this,
                factory
            )[HomeViewModel::class.java]

        homeViewModel.vehicle.observe(this) { vehicle ->

            if (vehicle != null) {

                fillVehicleDetails(vehicle)

            } else {

                Toast.makeText(
                    this,
                    "Vehicle not found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ------------------------------------------------
    // VEHICLE SEARCH
    // ------------------------------------------------

    private fun setupVehicleSearch() {

        btnSearchVehicle.setOnClickListener {

            val vehicleNumber =
                etVehicleNumber.text
                    ?.toString()
                    ?.trim()
                    ?.replace("-", "")
                    ?.replace("/", "")
                    ?.replace(".", "")
                    ?.replace(" ", "")
                    ?.uppercase()
                    ?: ""

            if (vehicleNumber.isEmpty()) {

                etVehicleNumber.error =
                    "Enter Vehicle Number"

                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Searching vehicle...",
                Toast.LENGTH_SHORT
            ).show()

            homeViewModel.searchVehicle(
                vehicleNumber
            )
        }
    }

    // ------------------------------------------------
    // FILL VEHICLE DETAILS
    // ------------------------------------------------

    private fun fillVehicleDetails(
        vehicle: Vehicle
    ) {

        etVehicleNumber.setText(
            vehicle.vehicleNumber ?: ""
        )

        etCustomerName.setText(
            vehicle.ownerName ?: ""
        )

        etLoanNumber.setText(
            vehicle.id?.loanNumber ?: ""
        )

        etVehicleType.setText(
            vehicle.vehicleType ?: ""
        )

        etVehicleMake.setText(
            vehicle.vehicleMake ?: ""
        )

        etVehicleModel.setText(
            vehicle.model ?: ""
        )

        etEngineNumber.setText(
            vehicle.engineNumber ?: ""
        )

        etChassisNumber.setText(
            vehicle.chassisNumber ?: ""
        )

        etCustomerAddress.setText(
            vehicle.customerAddress ?: ""
        )

        Toast.makeText(
            this,
            "Vehicle details filled automatically",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ------------------------------------------------
    // DATE PICKER
    // ------------------------------------------------

    private fun setupDatePicker() {

        etInvoiceDate.setOnClickListener {

            val calendar =
                Calendar.getInstance()

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

                    etInvoiceDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // ------------------------------------------------
    // CREATE INVOICE
    // ------------------------------------------------

    private fun setupCreateInvoiceButton() {

        btnCreateInvoice.setOnClickListener {

            createInvoice()
        }
    }

    // ------------------------------------------------
    // CREATE INVOICE OBJECT
    // ------------------------------------------------

    private fun createInvoice() {

        val session =
            SessionManager(this)

        val agencyId =
            session.getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val invoiceNumber =
            etInvoiceNumber.text
                ?.toString()
                ?.trim()

        if (invoiceNumber.isNullOrEmpty()) {

            etInvoiceNumber.error =
                "Enter Invoice Number"

            return
        }

        val vehicleNumber =
            etVehicleNumber.text
                ?.toString()
                ?.trim()

        if (vehicleNumber.isNullOrEmpty()) {

            etVehicleNumber.error =
                "Select Vehicle"

            return
        }

        val basic1 =
            etBasic1Amount.text
                ?.toString()
                ?.toDoubleOrNull()
                ?: 0.0

        val basic2 =
            etBasic2Amount.text
                ?.toString()
                ?.toDoubleOrNull()
                ?: 0.0

        val gstPercent =
            etGstPercent.text
                ?.toString()
                ?.toDoubleOrNull()
                ?: 0.0

        val totalBasic =
            basic1 + basic2

        val gstAmount =
            totalBasic * gstPercent / 100.0

        val invoiceTotal =
            totalBasic + gstAmount

        val invoice =
            Invoice(

                invoiceNumber =
                    invoiceNumber,

                invoiceDate =
                    etInvoiceDate.text
                        ?.toString(),

                repoYear = null,

                repoMonth = null,

                invoiceBank =
                    etInvoiceBank.text
                        ?.toString(),

                invoiceAddress =
                    etInvoiceAddress.text
                        ?.toString(),

                loanNumber =
                    etLoanNumber.text
                        ?.toString(),

                customerName =
                    etCustomerName.text
                        ?.toString(),

                vehicleNumber =
                    vehicleNumber,

                vehicleType =
                    etVehicleType.text
                        ?.toString(),

                vehicleMake =
                    etVehicleMake.text
                        ?.toString(),

                vehicleModel =
                    etVehicleModel.text
                        ?.toString(),

                engineNumber =
                    etEngineNumber.text
                        ?.toString(),

                chassisNumber =
                    etChassisNumber.text
                        ?.toString(),

                description1 =
                    etDescription1.text
                        ?.toString(),

                basic1Amount =
                    basic1,

                description2 =
                    etDescription2.text
                        ?.toString(),

                basic2Amount =
                    basic2,

                cgst = gstAmount / 2,

                sgst = gstAmount / 2,

                igst = 0.0,

                totalBasic =
                    totalBasic,

                gst =
                    gstAmount,

                invoiceTotal =
                    invoiceTotal,

                remarks =
                    etRemarks.text
                        ?.toString(),

                createdBy =
                    session.getUserEmail(),

                createdDate =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(
                        Calendar.getInstance().time
                    ),

                gstPercent =
                    gstPercent,

                paymentDate = null,

                paymentReceived =
                    0.0,

                paymentStatus =
                    "Pending",

                agencyId =
                    agencyId
            )

        invoiceViewModel.createInvoice(
            invoice
        )
    }
    private fun setupVehicleAutocomplete() {

        vehicleSuggestionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            vehicleSuggestions
        )

        etVehicleNumber.setAdapter(vehicleSuggestionAdapter)

        // Start showing suggestions after 2 characters
        etVehicleNumber.threshold = 2

        etVehicleNumber.addTextChangedListener(
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

                    val keyword =
                        s?.toString()
                            ?.trim()
                            ?.replace("-", "")
                            ?.replace("/", "")
                            ?.replace(".", "")
                            ?.replace(" ", "")
                            ?.uppercase()
                            ?: ""

                    if (keyword.length >= 2) {

                        loadVehicleSuggestions(keyword)

                    } else {

                        vehicleSuggestionAdapter.clear()
                        vehicleSuggestionAdapter.notifyDataSetChanged()

                        etVehicleNumber.dismissDropDown()
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        // User selects a vehicle from dropdown
        etVehicleNumber.setOnItemClickListener { _, _, position, _ ->

            val selectedVehicle =
                vehicleSuggestionAdapter.getItem(position)

            if (!selectedVehicle.isNullOrEmpty()) {

                etVehicleNumber.setText(
                    selectedVehicle,
                    false
                )

                etVehicleNumber.setSelection(
                    selectedVehicle.length
                )

                // Automatically load vehicle details
                homeViewModel.searchVehicle(
                    selectedVehicle
                )
            }
        }
    }

    private fun loadVehicleSuggestions(
        keyword: String
    ) {

        homeViewModel
            .searchVehicles(keyword)
            .observe(this) { response ->

                if (response == null) {
                    return@observe
                }

                if (!response.isSuccessful) {

                    vehicleSuggestionAdapter.clear()
                    vehicleSuggestionAdapter.notifyDataSetChanged()

                    return@observe
                }

                val vehicles =
                    response.body()
                        ?: emptyList()

                val suggestions =
                    vehicles
                        .mapNotNull { vehicle ->
                            vehicle.vehicleNumber
                        }
                        .map { number ->
                            number
                                .replace("-", "")
                                .replace("/", "")
                                .replace(".", "")
                                .replace(" ", "")
                                .uppercase()
                        }
                        .distinct()

                vehicleSuggestionAdapter.clear()

                vehicleSuggestionAdapter.addAll(
                    suggestions
                )

                vehicleSuggestionAdapter.notifyDataSetChanged()

                if (suggestions.isNotEmpty()) {

                    etVehicleNumber.showDropDown()

                } else {

                    etVehicleNumber.dismissDropDown()
                }
            }
    }
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}