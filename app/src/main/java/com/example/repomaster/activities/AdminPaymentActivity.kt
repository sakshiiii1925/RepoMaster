
package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.example.repomaster.R
import com.example.repomaster.models.*
import com.example.repomaster.network.RetrofitClient
import com.example.repomaster.repository.AdminPaymentRepository
import com.example.repomaster.viewmodel.AdminPaymentViewModel
import com.example.repomaster.viewmodel.AdminPaymentViewModelFactory

import com.google.android.material.appbar.MaterialToolbar


class AdminPaymentActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminPaymentViewModel

    private lateinit var spinnerUser: AutoCompleteTextView
    private lateinit var spinnerVehicle: AutoCompleteTextView
    private lateinit var spinnerPaymentMethod: AutoCompleteTextView

    private lateinit var txtVehicleNumber: TextView
    private lateinit var txtVehicleType: TextView
    private lateinit var txtWorkType: TextView
    private lateinit var txtAmount: TextView

    private lateinit var txtCompletedWork: TextView
    private lateinit var txtTotalDue: TextView
    private lateinit var txtTotalPaid: TextView
    private lateinit var txtRemaining: TextView

    private lateinit var edtRemarks: EditText
    private lateinit var btnCreatePayment: Button
    private lateinit var progressBar: ProgressBar

    private var users =
        emptyList<AdminPaymentUser>()

    private var vehicles =
        emptyList<AdminPaymentVehicle>()

    private var selectedUser:
            AdminPaymentUser? = null

    private var selectedVehicle:
            AdminPaymentVehicle? = null

    private var calculation:
            PaymentCalculation? = null


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_payment
        )


        setupToolbar()
        initializeViews()
        setupViewModel()
        setupPaymentMethod()
        observeViewModel()

        loadUsers()
    }


    // =========================================================
    // TOOLBAR
    // =========================================================

    private fun setupToolbar() {

        val toolbar =
            findViewById<MaterialToolbar>(
                R.id.toolbar
            )

        setSupportActionBar(toolbar)

        supportActionBar?.title =
            "Admin Payment"

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }


    // =========================================================
    // VIEWS
    // =========================================================

    private fun initializeViews() {

        spinnerUser =
            findViewById(R.id.spinnerUser)

        spinnerVehicle =
            findViewById(R.id.spinnerVehicle)

        spinnerPaymentMethod =
            findViewById(R.id.spinnerPaymentMethod)


        txtVehicleNumber =
            findViewById(R.id.txtVehicleNumber)

        txtVehicleType =
            findViewById(R.id.txtVehicleType)

        txtWorkType =
            findViewById(R.id.txtWorkType)

        txtAmount =
            findViewById(R.id.txtAmount)


        txtCompletedWork =
            findViewById(R.id.txtCompletedWork)

        txtTotalDue =
            findViewById(R.id.txtTotalDue)

        txtTotalPaid =
            findViewById(R.id.txtTotalPaid)

        txtRemaining =
            findViewById(R.id.txtRemaining)


        edtRemarks =
            findViewById(R.id.edtRemarks)

        btnCreatePayment =
            findViewById(R.id.btnCreatePayment)

        progressBar =
            findViewById(R.id.progressBar)


        btnCreatePayment.isEnabled =
            false


        spinnerUser.setOnItemClickListener {
                _, _, position, _ ->

            if (
                position >= 0 &&
                position < users.size
            ) {

                selectedUser =
                    users[position]

                loadUserVehicles(
                    selectedUser!!.id
                )

                loadSummary(
                    selectedUser!!.id
                )
            }
        }


        spinnerVehicle.setOnItemClickListener {
                _, _, position, _ ->

            if (
                position >= 0 &&
                position < vehicles.size
            ) {

                selectedVehicle =
                    vehicles[position]

                selectedUser?.let { user ->

                    calculatePayment(
                        user.id,
                        selectedVehicle!!
                    )
                }
            }
        }


        btnCreatePayment.setOnClickListener {

            createPayment()
        }
    }


    // =========================================================
    // VIEWMODEL
    // =========================================================

    private fun setupViewModel() {

        val repository =
            AdminPaymentRepository(
                RetrofitClient.adminPaymentApi
            )

        val factory =
            AdminPaymentViewModelFactory(
                repository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[AdminPaymentViewModel::class.java]
    }


    // =========================================================
    // PAYMENT METHODS
    // =========================================================

    private fun setupPaymentMethod() {

        val methods =
            listOf(
                "Cash",
                "PhonePe",
                "Google Pay",
                "UPI",
                "Bank Transfer",
                "Other"
            )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                methods
            )

        spinnerPaymentMethod.setAdapter(
            adapter
        )
    }


    // =========================================================
    // OBSERVERS
    // =========================================================

    private fun observeViewModel() {

        viewModel.users.observe(this) {

            users = it

            val names =
                users.map { user ->
                    user.full_name
                }

            spinnerUser.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    names
                )
            )
        }


        viewModel.vehicles.observe(this) {

            vehicles = it

            val vehicleNames =
                vehicles.map { vehicle ->

                    "${vehicle.vehicle_number} - " +
                            "${vehicle.vehicle_type ?: "N/A"} - " +
                            "${vehicle.repo_status ?: "N/A"}"
                }

            spinnerVehicle.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    vehicleNames
                )
            )
        }


        viewModel.calculation.observe(this) {

            calculation = it

            if (it != null) {

                txtVehicleNumber.text =
                    it.vehicle_number

                txtVehicleType.text =
                    it.vehicle_type

                txtWorkType.text =
                    it.repo_status

                txtAmount.text =
                    "₹ ${it.amount}"

                btnCreatePayment.isEnabled =
                    true
            }
        }


        viewModel.summary.observe(this) {

            if (it != null) {

                txtCompletedWork.text =
                    it.completed_work.toString()

                txtTotalDue.text =
                    "₹ ${it.total_due}"

                txtTotalPaid.text =
                    "₹ ${it.total_paid}"

                txtRemaining.text =
                    "₹ ${it.remaining}"
            }
        }


        viewModel.paymentResult.observe(this) {

            if (it != null) {

                val payment =
                    it.payment

                val summary =
                    it.summary


                Toast.makeText(
                    this,
                    "Payment created successfully",
                    Toast.LENGTH_LONG
                ).show()


                // Update summary immediately
                summary?.let { s ->

                    txtCompletedWork.text =
                        s.completed_work.toString()

                    txtTotalDue.text =
                        "₹ ${s.total_due}"

                    txtTotalPaid.text =
                        "₹ ${s.total_paid}"

                    txtRemaining.text =
                        "₹ ${s.remaining}"
                }


                // Clear selected vehicle
                selectedVehicle = null
                calculation = null

                spinnerVehicle.text.clear()

                txtVehicleNumber.text =
                    "-"

                txtVehicleType.text =
                    "-"

                txtWorkType.text =
                    "-"

                txtAmount.text =
                    "₹ 0.00"

                edtRemarks.text.clear()

                btnCreatePayment.isEnabled =
                    false


                // Refresh vehicles
                selectedUser?.let { user ->

                    loadUserVehicles(
                        user.id
                    )

                    loadSummary(
                        user.id
                    )
                }
            }
        }


        viewModel.loading.observe(this) {

            progressBar.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }


        viewModel.error.observe(this) {

            if (
                !it.isNullOrEmpty()
            ) {

                Toast.makeText(
                    this,
                    it,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // LOAD USERS
    // =========================================================

    private fun loadUsers() {

        viewModel.loadUsers()
    }


    // =========================================================
    // LOAD VEHICLES
    // =========================================================

    private fun loadUserVehicles(
        userId: Int
    ) {

        selectedVehicle = null
        calculation = null

        spinnerVehicle.text.clear()

        txtVehicleNumber.text = "-"
        txtVehicleType.text = "-"
        txtWorkType.text = "-"
        txtAmount.text = "₹ 0.00"

        btnCreatePayment.isEnabled =
            false

        viewModel.loadVehicles(
            userId
        )
    }


    // =========================================================
    // CALCULATE
    // =========================================================

    private fun calculatePayment(
        userId: Int,
        vehicle: AdminPaymentVehicle
    ) {

        viewModel.calculatePayment(
            userId,
            vehicle
        )
    }


    // =========================================================
    // SUMMARY
    // =========================================================

    private fun loadSummary(
        userId: Int
    ) {

        viewModel.loadSummary(
            userId
        )
    }


    // =========================================================
    // CREATE PAYMENT
    // =========================================================

    private fun createPayment() {

        val user =
            selectedUser

        val vehicle =
            selectedVehicle

        val calc =
            calculation


        if (user == null) {

            Toast.makeText(
                this,
                "Please select a user",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (vehicle == null) {

            Toast.makeText(
                this,
                "Please select a vehicle",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (calc == null) {

            Toast.makeText(
                this,
                "Please calculate payment first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val paymentMethod =
            spinnerPaymentMethod.text
                .toString()
                .trim()


        if (paymentMethod.isEmpty()) {

            Toast.makeText(
                this,
                "Please select payment method",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val amount =
            calc.amount.toDoubleOrNull()


        if (
            amount == null ||
            amount <= 0
        ) {

            Toast.makeText(
                this,
                "Invalid payment amount",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val remarks =
            edtRemarks.text
                .toString()
                .trim()
        val request =
            CreatePaymentRequest(

                user_id = user.id,

                repo_year = calc.repo_year,

                repo_month = calc.repo_month,

                loan_number = calc.loan_number,

                work_type = calc.repo_status,

                payment_method = paymentMethod,

                remarks =
                    if (remarks.isEmpty()) {
                        null
                    } else {
                        remarks
                    }
            )

        btnCreatePayment.isEnabled =
            false


        viewModel.createPayment(
            request
        )
    }
}

