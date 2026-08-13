package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.repository.InvoiceRepository
import com.example.repomaster.api.InvoiceApi
import com.example.repomaster.utils.Constants
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.InvoiceViewModel
import com.example.repomaster.viewmodel.InvoiceViewModelFactory
import retrofit2.Retrofit
import android.content.Intent
import retrofit2.converter.gson.GsonConverterFactory
import com.example.repomaster.adapter.InvoiceAdapter
class InvoiceActivity : AppCompatActivity() {

    private lateinit var recyclerInvoices: RecyclerView
    private lateinit var invoiceAdapter: InvoiceAdapter
    private lateinit var invoiceViewModel: InvoiceViewModel

    private lateinit var progressInvoice: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_invoice)

        recyclerInvoices =
            findViewById(R.id.recyclerInvoices)

        progressInvoice =
            findViewById(R.id.progressInvoice)

        recyclerInvoices.layoutManager =
            LinearLayoutManager(this)
        invoiceAdapter = InvoiceAdapter(emptyList()) { invoice ->

            invoice.id?.let { id ->

                val intent = Intent(
                    this,
                    InvoiceDetailsActivity::class.java
                )

                intent.putExtra(
                    "invoiceId",
                    id
                )

                startActivity(intent)
            }
        }

        recyclerInvoices.adapter =
            invoiceAdapter
        setupViewModel()

        observeInvoices()

        loadInvoices()
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

    private fun loadInvoices() {

        val agencyId =
            SessionManager(this)
                .getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        invoiceViewModel
            .getInvoicesByAgency(agencyId)
    }

    private fun observeInvoices() {

        invoiceViewModel.loading
            .observe(this) { loading ->

                progressInvoice.visibility =
                    if (loading)
                        View.VISIBLE
                    else
                        View.GONE
            }

        invoiceViewModel.invoices
            .observe(this) { invoices ->

                invoiceAdapter.updateList(invoices)

                Toast.makeText(
                    this,
                    "${invoices.size} invoice(s) found",
                    Toast.LENGTH_SHORT
                ).show()
            }

        invoiceViewModel.error
            .observe(this) { error ->

                if (!error.isNullOrEmpty()) {

                    Toast.makeText(
                        this,
                        error,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}