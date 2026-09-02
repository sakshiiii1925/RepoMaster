package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repomaster.databinding.ActivityPaymentHistoryBinding
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

        binding =
            ActivityPaymentHistoryBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        userId =
            intent.getIntExtra(
                "user_id",
                0
            )

        userName =
            intent.getStringExtra(
                "user_name"
            ) ?: "User"

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
        observeViewModel()

        binding.txtUserName.text =
            userName

        viewModel.loadPaymentHistory(userId)
        viewModel.loadSummary(userId)
    }

    private fun setupToolbar() {

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {

        adapter =
            PaymentHistoryAdapter1(
                emptyList()
            )

        binding.recyclerPaymentHistory.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerPaymentHistory.adapter =
            adapter
    }

    private fun observeViewModel() {

        viewModel.paymentHistory.observe(this) { history ->

            adapter.updateData(history)

            binding.txtNoHistory.visibility =
                if (history.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

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

        viewModel.loading.observe(this) { loading ->

            binding.progressBar.visibility =
                if (loading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.error.observe(this) { error ->

            if (!error.isNullOrBlank()) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



}