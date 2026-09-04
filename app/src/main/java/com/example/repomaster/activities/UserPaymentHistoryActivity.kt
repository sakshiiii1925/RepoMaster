package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repomaster.R
import com.example.repomaster.databinding.ActivityUserPaymentHistoryBinding
import com.example.repomaster.network.RetrofitClient
import com.example.repomaster.repository.UserPaymentRepository
import com.example.repomaster.viewmodel.UserPaymentHistoryViewModel
import com.example.repomaster.viewmodel.UserPaymentHistoryViewModelFactory
import com.example.repomaster.utils.SessionManager
class UserPaymentHistoryActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityUserPaymentHistoryBinding

    private val repository by lazy {

        UserPaymentRepository(
            RetrofitClient.userPaymentApi
        )
    }

    private val viewModel:
            UserPaymentHistoryViewModel by viewModels {

        UserPaymentHistoryViewModelFactory(
            repository
        )
    }

    private lateinit var adapter:
            PaymentHistoryAdapter1


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityUserPaymentHistoryBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        loadPaymentData()
    }


    // =====================================================
    // TOOLBAR
    // =====================================================

    private fun setupToolbar() {

        binding.toolbarUserPayment
            .setNavigationOnClickListener {

                finish()
            }
    }


    // =====================================================
    // RECYCLER VIEW
    // =====================================================

    private fun setupRecyclerView() {

        adapter =
            PaymentHistoryAdapter1(
                emptyList(),
                showDeleteButton = false
            ) {
                // User cannot delete
            }

        binding.recyclerPaymentHistory.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerPaymentHistory.adapter =
            adapter
    }


    // =====================================================
    // LOAD PAYMENT DATA
    // =====================================================

    private fun loadPaymentData() {

        val sessionManager =
            SessionManager(this)

        val userId =
            sessionManager.getUserId()

        val userName =
            sessionManager.getUserName()

        if (userId <= 0) {
            Toast.makeText(
                this,
                "User session not found. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        binding.txtUserName.text =
            if (userName.isNotBlank()) {
                userName
            } else {
                "My Payments"
            }

        viewModel.loadPaymentData(userId)
    }



    // =====================================================
    // OBSERVE VIEWMODEL
    // =====================================================

    private fun observeViewModel() {


        // -------------------------------------------------
        // PAYMENT HISTORY
        // -------------------------------------------------

        viewModel.paymentHistory.observe(
            this
        ) { history ->

            adapter.updateData(
                history
            )

            binding.txtNoHistory.visibility =
                if (history.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }


        // -------------------------------------------------
        // PAYMENT SUMMARY
        // -------------------------------------------------

        viewModel.summary.observe(
            this
        ) { summary ->

            if (summary == null) {
                return@observe
            }


            binding.txtTotalDue.text =
                "₹${summary.total_due}"


            binding.txtTotalPaid.text =
                "₹${summary.total_paid}"


            binding.txtRemaining.text =
                "₹${summary.remaining}"
        }


        // -------------------------------------------------
        // LOADING
        // -------------------------------------------------

        viewModel.loading.observe(
            this
        ) { loading ->

            binding.progressBar.visibility =
                if (loading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }


        // -------------------------------------------------
        // ERROR
        // -------------------------------------------------

        viewModel.error.observe(
            this
        ) { error ->

            if (!error.isNullOrBlank()) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}