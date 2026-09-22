package com.example.repomaster.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapter.VehicleReportAdapter
import com.example.repomaster.models.VehicleReport
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class VehicleReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VehicleReportAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var userViewModel: UserViewModel
    private lateinit var agencyId: String
    private lateinit var searchLayout: TextInputLayout
    private lateinit var etSearch: TextInputEditText

    private val allVehicles = ArrayList<VehicleReport>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_vehicle_report)

        // --------------------------------------------------
        // ViewModel
        // --------------------------------------------------
        userViewModel =
            ViewModelProvider(this)[UserViewModel::class.java]

        // --------------------------------------------------
        // Session
        // --------------------------------------------------
        agencyId = SessionManager(this).getAgencyId()

        // --------------------------------------------------
        // Intent values
        // --------------------------------------------------
        val reportType =
            intent.getStringExtra("REPORT_TYPE") ?: ""

        val finance =
            intent.getStringExtra("FINANCE")

        val branch =
            intent.getStringExtra("BRANCH")

        val status =
            intent.getStringExtra("STATUS") ?: "ALL"

        val year =
            intent.getStringExtra("YEAR")

        val month =
            intent.getStringExtra("MONTH")

        // --------------------------------------------------
        // Views
        // --------------------------------------------------
        toolbar = findViewById(R.id.toolbar)

        recyclerView =
            findViewById(R.id.rvVehicles)

        searchLayout =
            findViewById(R.id.searchLayout)

        etSearch =
            findViewById(R.id.etSearch1)

        // --------------------------------------------------
        // Toolbar
        // --------------------------------------------------
        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // --------------------------------------------------
        // Toolbar title
        // --------------------------------------------------
        if (reportType == "MONTHLY") {

            val monthText =
                getMonthName(month)

            supportActionBar?.title =
                "$monthText $year - $status"

        } else {

            supportActionBar?.title =
                "$status Vehicles"
        }

        // --------------------------------------------------
        // RecyclerView
        // --------------------------------------------------
        recyclerView.layoutManager =
            LinearLayoutManager(this)

        // --------------------------------------------------
        // Load vehicles
        // --------------------------------------------------
        loadVehicles(
            reportType = reportType,
            finance = finance,
            branch = branch,
            year = year,
            month = month,
            status = status
        )

        // --------------------------------------------------
        // Search
        // --------------------------------------------------
        setupSearch()
    }

    // ======================================================
    // LOAD VEHICLES
    // ======================================================

    private fun loadVehicles(
        reportType: String,
        finance: String?,
        branch: String?,
        year: String?,
        month: String?,
        status: String
    ) {

        /*
         * MONTHLY REPORT
         *
         * Example:
         *
         * REPORT_TYPE = MONTHLY
         * YEAR        = 2026
         * MONTH       = 9
         * STATUS      = repo mark
         *
         * PHP will filter using repo_marked_at.
         */

        if (reportType == "MONTHLY") {

            if (
                year.isNullOrEmpty() ||
                month.isNullOrEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Invalid monthly report selection",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }

        userViewModel.getVehicleReport(
            agencyId,
            finance,
            branch,
            year,
            month,
            status
        ).observe(this) { response ->

            if (response.isSuccessful) {

                val vehicles =
                    response.body() ?: emptyList()

                allVehicles.clear()

                allVehicles.addAll(vehicles)

                adapter =
                    VehicleReportAdapter(allVehicles)

                recyclerView.adapter = adapter

                if (vehicles.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No Vehicles Found",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                allVehicles.clear()

                if (::adapter.isInitialized) {
                    adapter.updateList(emptyList())
                }

                Toast.makeText(
                    this,
                    "No Vehicles Found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ======================================================
    // SEARCH
    // ======================================================

    private fun setupSearch() {

        etSearch.addTextChangedListener(
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

                    val search =
                        s?.toString()
                            ?.trim()
                            ?: ""

                    if (!::adapter.isInitialized) {
                        return
                    }

                    if (search.isEmpty()) {

                        adapter.updateList(
                            allVehicles
                        )

                        return
                    }

                    val filteredList =
                        allVehicles.filter { vehicle ->

                            (
                                    vehicle.vehicleNumber
                                        ?.contains(
                                            search,
                                            ignoreCase = true
                                        )
                                        ?: false
                                    )
                                    ||
                                    (
                                            vehicle.ownerName
                                                ?.contains(
                                                    search,
                                                    ignoreCase = true
                                                )
                                                ?: false
                                            )
                                    ||
                                    (
                                            vehicle.loanNumber
                                                ?.contains(
                                                    search,
                                                    ignoreCase = true
                                                )
                                                ?: false
                                            )
                        }

                    adapter.updateList(
                        filteredList
                    )

                    searchLayout.boxStrokeColor =
                        getColor(R.color.white)
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    // ======================================================
    // MONTH NAME
    // ======================================================

    private fun getMonthName(
        month: String?
    ): String {

        return when (
            month?.toIntOrNull()
        ) {

            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"

            else -> month ?: ""
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}