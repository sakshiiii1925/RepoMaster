package com.example.repomaster.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import android.content.Intent
import com.example.repomaster.databinding.ActivityAdminPaymentBinding
import com.example.repomaster.models.*
import com.example.repomaster.network.RetrofitClient
import com.example.repomaster.repository.AdminPaymentRepository
import com.example.repomaster.viewmodel.AdminPaymentViewModel
import com.example.repomaster.viewmodel.AdminPaymentViewModelFactory

class AdminPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPaymentBinding

    private val repository by lazy {
        AdminPaymentRepository(
            RetrofitClient.adminPaymentApi
        )
    }
    private val viewModel: AdminPaymentViewModel by viewModels {
        AdminPaymentViewModelFactory(repository)
    }


    private var users = emptyList<AdminPaymentUser>()

    private var vehicles =
        emptyList<AdminPaymentVehicle>()

    private var selectedUser: AdminPaymentUser? = null

    private var selectedVehicle: AdminPaymentVehicle? = null

    private var userAdapter:
            ArrayAdapter<String>? = null

    private var vehicleAdapter:
            ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityAdminPaymentBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupPaymentMethods()
        setupUserSearch()
        setupVehicleDropdown()
        setupObservers()
        setupButtons()
        setupPaymentHistoryButton()

        viewModel.loadUsers()
    }


    // =========================================================
    // PAYMENT METHODS
    // =========================================================

    private fun setupPaymentMethods() {

        val methods =
            listOf(
                "Cash",
                "PhonePe",
                "Other"
            )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                methods
            )

        binding.autoPaymentMethod.setAdapter(adapter)

        binding.autoPaymentMethod.setText(
            methods.first(),
            false
        )
    }


    // =========================================================
    // USER SEARCH
    // =========================================================

    private fun setupUserSearch() {

        binding.autoUser.setOnClickListener {

            binding.autoUser.showDropDown()
        }

        binding.autoUser.setOnItemClickListener { _, _, position, _ ->

            val user =
                users[position]

            selectedUser = user

            binding.autoUser.setText(
                "${user.full_name} - ${user.mobile ?: user.email}",
                false
            )

            selectedVehicle = null
            vehicles = emptyList()

            clearVehicleInformation()

            viewModel.loadVehicles(user.id)

            viewModel.loadSummary(user.id)

            binding.autoVehicle.setText(
                "",
                false
            )

            binding.btnPaymentHistory.isEnabled =
                true
        }
    }


    // =========================================================
    // VEHICLE DROPDOWN
    // =========================================================

    private fun setupVehicleDropdown() {

        binding.autoVehicle.setOnClickListener {

            if (vehicles.isNotEmpty()) {
                binding.autoVehicle.showDropDown()
            }
        }

        binding.autoVehicle.setOnItemClickListener { _, _, position, _ ->

            val vehicle =
                vehicles[position]

            selectedVehicle = vehicle

            showVehicleInformation(vehicle)

            val user =
                selectedUser ?: return@setOnItemClickListener

            viewModel.calculatePayment(
                userId = user.id,
                vehicle = vehicle
            )
        }
    }


    // =========================================================
    // OBSERVERS
    // =========================================================

    private fun setupObservers() {

        viewModel.loading.observe(this) {

            binding.progressBar.isVisible =
                it
        }


        viewModel.error.observe(this) { message ->

            if (!message.isNullOrBlank()) {

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        // -----------------------------------------------------
        // USERS
        // -----------------------------------------------------

        viewModel.users.observe(this) { list ->

            users = list

            setupUserAdapter(list)
        }


        // -----------------------------------------------------
        // VEHICLES
        // -----------------------------------------------------

        viewModel.vehicles.observe(this) { list ->

            vehicles = list

            setupVehicleAdapter(list)
        }


        // -----------------------------------------------------
        // SUMMARY
        // -----------------------------------------------------

        viewModel.summary.observe(this) { summary ->

            if (summary == null) return@observe

            // User summary is supplementary.
            // Selected work calculation is displayed below.
        }


        // -----------------------------------------------------
        // CALCULATION
        // -----------------------------------------------------

        viewModel.calculation.observe(this) { calculation ->

            if (calculation == null) return@observe

            binding.txtTotalAmount.text =
                "Total Due: ₹${calculation.total_amount}"

            binding.txtPaidAmount.text =
                "Paid Till Date: ₹${calculation.paid_amount}"

            binding.txtRemainingAmount.text =
                "Remaining: ₹${calculation.remaining_amount}"

            // Automatically suggest remaining amount.
            if (!calculation.already_paid) {

                binding.edtPaymentAmount.setText(
                    calculation.remaining_amount
                )

            } else {

                binding.edtPaymentAmount.setText("")

                binding.edtPaymentAmount.isEnabled =
                    false

                binding.btnSubmitPayment.isEnabled =
                    false
            }

            if (!calculation.already_paid) {

                binding.edtPaymentAmount.isEnabled =
                    true

                binding.btnSubmitPayment.isEnabled =
                    true
            }
        }


        // -----------------------------------------------------
        // PAYMENT CREATED
        // -----------------------------------------------------

        viewModel.paymentResult.observe(this) { result ->

            if (result == null) return@observe

            Toast.makeText(
                this,
                "Payment ₹${result.payment.amount} created successfully",
                Toast.LENGTH_LONG
            ).show()

            binding.edtPaymentAmount.setText("")

            val user =
                selectedUser

            val vehicle =
                selectedVehicle

            if (user != null && vehicle != null) {

                viewModel.calculatePayment(
                    userId = user.id,
                    vehicle = vehicle
                )

                viewModel.loadSummary(
                    user.id
                )
            }
        }
    }


    // =========================================================
    // USER ADAPTER
    // =========================================================

    private fun setupUserAdapter(
        list: List<AdminPaymentUser>
    ) {

        val names =
            list.map {

                "${it.full_name} - ${
                    it.mobile ?: it.email
                }"
            }

        userAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                names
            )

        binding.autoUser.setAdapter(
            userAdapter
        )
    }


    // =========================================================
    // VEHICLE ADAPTER
    // =========================================================

    private fun setupVehicleAdapter(
        list: List<AdminPaymentVehicle>
    ) {

        val displayItems =
            list.map {

                "${it.vehicle_number} - " +
                        "${it.vehicle_type ?: "-"} - " +
                        "${it.work_type ?: "-"}"
            }

        vehicleAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                displayItems
            )

        binding.autoVehicle.setAdapter(
            vehicleAdapter
        )

        if (list.isNotEmpty()) {

            binding.autoVehicle.showDropDown()

        } else {

            binding.autoVehicle.setText(
                "",
                false
            )
        }
    }


    // =========================================================
    // VEHICLE INFORMATION
    // =========================================================

    private fun showVehicleInformation(
        vehicle: AdminPaymentVehicle
    ) {

        binding.txtVehicleNumber.text =
            "Vehicle: ${vehicle.vehicle_number}"

        binding.txtVehicleType.text =
            "Vehicle Type: ${
                vehicle.vehicle_type ?: "-"
            }"

        binding.txtWorkType.text =
            "Work Type: ${
                vehicle.work_type ?: "-"
            }"

        binding.txtCompletedAt.text =
            "Completed: ${
                vehicle.completed_at ?: "-"
            }"

        binding.edtPaymentAmount.isEnabled =
            true

        binding.btnSubmitPayment.isEnabled =
            true
    }


    // =========================================================
    // CLEAR VEHICLE INFORMATION
    // =========================================================

    private fun clearVehicleInformation() {

        binding.txtVehicleNumber.text =
            "Vehicle: -"

        binding.txtVehicleType.text =
            "Vehicle Type: -"

        binding.txtWorkType.text =
            "Work Type: -"

        binding.txtCompletedAt.text =
            "Completed: -"

        binding.txtTotalAmount.text =
            "Total Due: ₹0.00"

        binding.txtPaidAmount.text =
            "Paid Till Date: ₹0.00"

        binding.txtRemainingAmount.text =
            "Remaining: ₹0.00"

        binding.edtPaymentAmount.setText("")

        binding.edtPaymentAmount.isEnabled =
            false

        binding.btnSubmitPayment.isEnabled =
            false
    }


    // =========================================================
    // BUTTONS
    // =========================================================

    private fun setupButtons() {

        binding.btnSubmitPayment.setOnClickListener {

            submitPayment()
        }


        binding.btnPaymentHistory.setOnClickListener {

            val user =
                selectedUser

            if (user == null) {

                Toast.makeText(
                    this,
                    "Please select a user first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            viewModel.loadPaymentHistory(
                user.id
            )

            /*
             * We will show the history in a dialog
             * or a separate PaymentHistoryActivity.
             *
             * I recommend a separate Activity once
             * the basic payment screen is working.
             */
        }
    }


    // =========================================================
    // SUBMIT PAYMENT
    // =========================================================

    private fun submitPayment() {

        val user =
            selectedUser

        val vehicle =
            selectedVehicle

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
                "Please select vehicle/work",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val workType =
            vehicle.work_type

        if (workType.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Work type is missing",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val amountText =
            binding.edtPaymentAmount
                .text
                ?.toString()
                ?.trim()

        val amount =
            amountText?.toDoubleOrNull()

        if (amount == null || amount <= 0) {

            Toast.makeText(
                this,
                "Enter a valid payment amount",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val calculation =
            viewModel.calculation.value

        if (calculation != null) {

            val remaining =
                calculation.remaining_amount
                    .toDoubleOrNull()
                    ?: 0.0

            if (remaining <= 0) {

                Toast.makeText(
                    this,
                    "This work is already fully paid",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            if (amount > remaining) {

                Toast.makeText(
                    this,
                    "Payment cannot be greater than remaining ₹$remaining",
                    Toast.LENGTH_LONG
                ).show()

                return
            }
        }


        val paymentMethod =
            binding.autoPaymentMethod
                .text
                .toString()
                .trim()

        if (paymentMethod.isBlank()) {

            Toast.makeText(
                this,
                "Select payment method",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val remarks =
            binding.edtRemarks
                .text
                ?.toString()
                ?.trim()


        val request =
            CreatePaymentRequest(
                user_id = user.id,
                repo_year = vehicle.repo_year,
                repo_month = vehicle.repo_month,
                loan_number = vehicle.loan_number,
                work_type = workType,
                payment_amount = amount,
                payment_method = paymentMethod,
                payment_date = null,
                remarks = remarks
            )

        viewModel.createPayment(
            request
        )
    }
    private fun setupPaymentHistoryButton() {

        binding.btnPaymentHistory.setOnClickListener {

            val user = selectedUser

            if (user == null) {

                Toast.makeText(
                    this,
                    "Please select a user first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent =
                Intent(
                    this,
                    PaymentHistoryActivity::class.java
                )

            intent.putExtra(
                "user_id",
                user.id
            )

            intent.putExtra(
                "user_name",
                user.full_name
            )

            startActivity(intent)
        }
    }
}