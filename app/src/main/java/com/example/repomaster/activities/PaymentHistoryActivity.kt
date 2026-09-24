
package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repomaster.R
import com.example.repomaster.databinding.ActivityPaymentHistoryBinding
import com.example.repomaster.models.AdminPayment
import com.example.repomaster.network.RetrofitClient
import com.example.repomaster.repository.AdminPaymentRepository
import com.example.repomaster.viewmodel.AdminPaymentViewModel
import com.example.repomaster.viewmodel.AdminPaymentViewModelFactory

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentHistoryBinding

    private val repository by lazy {
        AdminPaymentRepository(
            RetrofitClient.adminPaymentApi
        )
    }

    private val viewModel: AdminPaymentViewModel by viewModels {
        AdminPaymentViewModelFactory(repository)
    }

    private lateinit var adapter: PaymentHistoryAdapter1

    private var userId: Int = 0
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentHistoryBinding.inflate(
            layoutInflater
        )

        setContentView(binding.root)

        getIntentData()

        if (userId <= 0) {
            Toast.makeText(
                this,
                "Invalid user",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupUserInfo()

        loadData()
    }

    // ---------------------------------------------------------
    // GET INTENT DATA
    // ---------------------------------------------------------

    private fun getIntentData() {

        userId = intent.getIntExtra(
            "user_id",
            0
        )

        userName = intent.getStringExtra(
            "user_name"
        ) ?: "User"
    }

    // ---------------------------------------------------------
    // TOOLBAR
    // ---------------------------------------------------------

    private fun setupToolbar() {

        binding.toolbar.setTitleTextColor(
            getColor(R.color.black)
        )

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // ---------------------------------------------------------
    // USER INFORMATION
    // ---------------------------------------------------------

    private fun setupUserInfo() {

        binding.txtUserName.text = userName
    }

    // ---------------------------------------------------------
    // RECYCLER VIEW
    // ---------------------------------------------------------

    private fun setupRecyclerView() {

        adapter = PaymentHistoryAdapter1(
            emptyList(),
            showDeleteButton = true
        ) { payment ->

            showDeleteConfirmation(payment)
        }

        binding.recyclerPaymentHistory.apply {

            layoutManager = LinearLayoutManager(
                this@PaymentHistoryActivity
            )

            adapter = this@PaymentHistoryActivity.adapter

            setHasFixedSize(false)

            // Important when RecyclerView is inside
            // NestedScrollView in the modern XML.
            isNestedScrollingEnabled = false
        }
    }

    // ---------------------------------------------------------
    // OBSERVERS
    // ---------------------------------------------------------

    private fun setupObservers() {

        // PAYMENT HISTORY
        viewModel.paymentHistory.observe(this) { history ->

            adapter.updateData(history)

            updateEmptyState(history.isEmpty())
        }

        // SUMMARY
        viewModel.summary.observe(this) { summary ->

            if (summary == null) {
                return@observe
            }

            binding.txtTotalDue.text =
                "Total Due: ₹${summary.total_due}"

            binding.txtTotalPaid.text =
                "Total Paid: ₹${summary.total_paid}"

            binding.txtRemaining.text =
                "Remaining: ₹${summary.remaining}"
        }

        // LOADING
        viewModel.loading.observe(this) { loading ->

            updateLoadingState(loading)
        }

        // ERROR
        viewModel.error.observe(this) { error ->

            if (!error.isNullOrBlank()) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // DELETE RESULT
        viewModel.deleteResult.observe(this) { result ->

            result.onSuccess { message ->

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
                ).show()

                // Refresh both history and summary
                loadData()
            }

            result.onFailure { error ->

                Toast.makeText(
                    this,
                    error.message ?: "Delete failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------------------------------------------------
    // LOAD DATA
    // ---------------------------------------------------------

    private fun loadData() {

        viewModel.loadPaymentHistory(userId)

        viewModel.loadSummary(userId)
    }

    // ---------------------------------------------------------
    // EMPTY STATE
    // ---------------------------------------------------------

    private fun updateEmptyState(
        isEmpty: Boolean
    ) {

        if (isEmpty) {

            binding.emptyHistoryCard.visibility =
                View.VISIBLE

            binding.recyclerPaymentHistory.visibility =
                View.GONE

        } else {

            binding.emptyHistoryCard.visibility =
                View.GONE

            binding.recyclerPaymentHistory.visibility =
                View.VISIBLE
        }
    }

    // ---------------------------------------------------------
    // LOADING STATE
    // ---------------------------------------------------------

    private fun updateLoadingState(
        loading: Boolean
    ) {

        if (loading) {

            binding.paymentLoadingOverlay.visibility =
                View.VISIBLE

        } else {

            binding.paymentLoadingOverlay.visibility =
                View.GONE
        }
    }

    // ---------------------------------------------------------
    // DELETE CONFIRMATION
    // ---------------------------------------------------------

    private fun showDeleteConfirmation(
        payment: AdminPayment
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Payment")
            .setMessage(
                "Are you sure you want to delete this payment?\n\n" +
                        "Vehicle: ${payment.vehicle_number}\n" +
                        "Amount: ₹${payment.amount}\n" +
                        "Payment Method: ${payment.payment_method}"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                viewModel.deletePayment(
                    payment.id
                )
            }
            .show()
    }

    // ---------------------------------------------------------
    // SYSTEM BACK
    // ---------------------------------------------------------

    override fun onSupportNavigateUp(): Boolean {

        onBackPressedDispatcher.onBackPressed()

        return true
    }
}
