
package com.example.repomaster.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.google.android.material.card.MaterialCardView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.repomaster.R
import com.example.repomaster.adapters.VehicleAdapter
import com.example.repomaster.models.Vehicle
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.HomeViewModel
import com.example.repomaster.viewmodel.HomeViewModelFactory

import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ViewVehicleActivity :
    AppCompatActivity() {


    private lateinit var toolbar:
            MaterialToolbar

    private lateinit var swipeRefresh:
            SwipeRefreshLayout

    private lateinit var recyclerVehicles:
            RecyclerView

    private lateinit var homeViewModel:
            HomeViewModel

    private lateinit var etSearch:
            TextInputEditText

    private lateinit var searchLayout:
            TextInputLayout

    private lateinit var adapter:
            VehicleAdapter

    private lateinit var sessionManager:
            SessionManager

    private lateinit var btnSelectDate:
            MaterialButton

    private lateinit var btnClearDate:
            MaterialButton

    private lateinit var btnSelectAll:
            MaterialButton

    private lateinit var btnDeleteSelected:
            MaterialButton

    private lateinit var btnDeleteDate:
            MaterialButton
    private lateinit var btnCancelSelection: MaterialButton
    private lateinit var txtVehicleCount: TextView
    private lateinit var uploadDate:MaterialCardView
    private lateinit var vehicleaction: MaterialCardView
    private lateinit var txtDateInfo:
            android.widget.TextView
    private var isAdmin = false

    private val allVehicles =
        mutableListOf<Vehicle>()


    private var selectedUploadDate:
            String? = null
    private lateinit var checkHeader: TextView

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        sessionManager =
            SessionManager(this)
        isAdmin =
            sessionManager
                .getRole()
                .equals(
                    "ADMIN",
                    ignoreCase = true
                )

        setContentView(
            R.layout.activity_view_vehicles
        )


        // =====================================================
        // FIND VIEWS
        // =====================================================

        toolbar =
            findViewById(
                R.id.toolbar
            )

        swipeRefresh =
            findViewById(
                R.id.swipeRefresh
            )

        recyclerVehicles =
            findViewById(
                R.id.recyclerVehicles
            )

        etSearch =
            findViewById(
                R.id.etSearch
            )

        searchLayout =
            findViewById(
                R.id.searchLayout
            )

        btnSelectDate =
            findViewById(
                R.id.btnSelectDate
            )

        btnClearDate =
            findViewById(
                R.id.btnClearDate
            )

        btnSelectAll =
            findViewById(
                R.id.btnSelectAll
            )

        btnDeleteSelected =
            findViewById(
                R.id.btnDeleteSelected
            )

        btnDeleteDate =
            findViewById(
                R.id.btnDeleteDate
            )
        btnCancelSelection =
            findViewById(
                R.id.btnCancelSelection
            )
        txtDateInfo =
            findViewById(
                R.id.txtDateInfo
            )
        txtVehicleCount =
            findViewById(
                R.id.txtVehicleCount
            )
        checkHeader =
            findViewById(R.id.checkHeader)
        uploadDate=findViewById(R.id.uploaddate)
        vehicleaction=findViewById(R.id.vehicleaction)

        // =====================================================
        // TOOLBAR
        // =====================================================

        setSupportActionBar(
            toolbar
        )
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "View Vehicles"

        supportActionBar
            ?.setDisplayHomeAsUpEnabled(
                true
            )


        // =====================================================
        // RECYCLER
        // =====================================================

        recyclerVehicles.layoutManager =
            LinearLayoutManager(this)

        recyclerVehicles.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )


        // =====================================================
        // VIEW MODEL
        // =====================================================

        val factory =
            HomeViewModelFactory(
                applicationContext
            )

        homeViewModel =
            ViewModelProvider(
                this,
                factory
            )[HomeViewModel::class.java]


        // =====================================================
        // ADAPTER
        // =====================================================

        adapter =
            VehicleAdapter(
                emptyList(),
                homeViewModel,

                // Selection changed
                { selectedCount, totalCount ->

                    updateSelectionButtons(
                        selectedCount,
                        totalCount
                    )
                },

                // Selection mode changed
                { selectionMode ->

                    updateSelectionModeUI(
                        selectionMode
                    )
                }
            )


        recyclerVehicles.adapter =
            adapter


        // =====================================================
        // OBSERVERS
        // =====================================================

        observeVehicles()

        setupDeleteObservers()


        // =====================================================
        // SEARCH
        // =====================================================

        setupSearch()


        // =====================================================
        // DATE
        // =====================================================

        setupDateFilter()


        // =====================================================
        // BUTTONS
        // =====================================================

        setupSelectionButtons()
        setupRoleBasedVisibility()
        setupRefresh()
        // =====================================================
        // INITIAL LOAD
        // =====================================================

        homeViewModel.getAllVehicles()
    }


    // =========================================================
    // OBSERVE VEHICLES
    // =========================================================

    private fun observeVehicles() {

        homeViewModel.vehicleList
            .observe(this) { vehicles ->

                if (vehicles != null) {

                    allVehicles.clear()

                    /*
                     * Sort by upload date.
                     *
                     * Newest uploaded vehicles
                     * will appear first.
                     */

                    val sorted =
                        vehicles.sortedByDescending {

                            parseUploadDate(
                                it.uploadDate
                            )
                        }

                    allVehicles.addAll(
                        sorted
                    )


                    applyFilters()


                    swipeRefresh
                        .isRefreshing = false
                }
            }
    }


    // =========================================================
    // PARSE UPLOAD DATE
    // =========================================================

    private fun parseUploadDate(
        dateString: String?
    ): Long {

        if (
            dateString.isNullOrBlank()
        ) {
            return 0L
        }


        val formats =
            listOf(
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd"
            )


        for (format in formats) {

            try {

                val formatter =
                    SimpleDateFormat(
                        format,
                        Locale.getDefault()
                    )

                return formatter
                    .parse(dateString)
                    ?.time ?: 0L

            } catch (
                ignored: Exception
            ) {
            }
        }


        return 0L
    }


    // =========================================================
    // EXTRACT DATE
    // =========================================================

    private fun getDateOnly(
        dateString: String?
    ): String {

        if (
            dateString.isNullOrBlank()
        ) {
            return ""
        }


        return dateString
            .trim()
            .take(10)
    }


    // =========================================================
    // APPLY SEARCH + DATE FILTER
    // =========================================================

    private fun applyFilters() {

        val search =
            etSearch.text
                ?.toString()
                ?.trim()
                ?: ""


        var filtered =
            allVehicles.toList()


        // =====================================================
        // DATE FILTER
        // =====================================================

        selectedUploadDate?.let { date ->

            filtered =
                filtered.filter { vehicle ->

                    getDateOnly(
                        vehicle.uploadDate
                    ) == date
                }
        }


        // =====================================================
        // SEARCH FILTER
        // =====================================================

        if (search.isNotEmpty()) {

            filtered =
                filtered.filter { vehicle ->

                    (
                            vehicle.vehicleNumber
                                ?.contains(
                                    search,
                                    ignoreCase = true
                                ) == true
                            )

                            ||

                            (
                                    vehicle.ownerName
                                        ?.contains(
                                            search,
                                            ignoreCase = true
                                        ) == true
                                    )

                            ||

                            (
                                    vehicle.repoStatus
                                        ?.contains(
                                            search,
                                            ignoreCase = true
                                        ) == true
                                    )

                            ||

                            (
                                    vehicle.engineNumber
                                        ?.contains(
                                            search,
                                            ignoreCase = true
                                        ) == true
                                    )

                            ||

                            (
                                    vehicle.chassisNumber
                                        ?.contains(
                                            search,
                                            ignoreCase = true
                                        ) == true
                                    )
                }
        }


        // =====================================================
        // UPDATE LIST
        // =====================================================

        adapter.updateList(
            filtered
        )
        txtVehicleCount.text =
            filtered.size.toString()

        updateDateInformation(
            filtered
        )
    }


    // =========================================================
    // DATE INFORMATION
    // =========================================================

    private fun updateDateInformation(
        filtered: List<Vehicle>
    ) {

        if (
            selectedUploadDate != null
        ) {

            txtDateInfo.text =
                "Upload Date: " +
                        selectedUploadDate +
                        "  |  Vehicles: " +
                        filtered.size

        } else {

            txtDateInfo.text =
                "All Upload Dates  |  Vehicles: " +
                        filtered.size
        }
    }


    // =========================================================
    // SEARCH
    // =========================================================

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

                    applyFilters()
                }


                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }


    // =========================================================
    // DATE FILTER
    // =========================================================

    private fun setupDateFilter() {

        btnSelectDate.setOnClickListener {

            showDatePicker()
        }


        btnClearDate.setOnClickListener {

            selectedUploadDate =
                null

            btnSelectDate.text =
                "Select Upload Date"

            txtDateInfo.text =
                "All Upload Dates"

            adapter.clearSelection()

            applyFilters()
        }
    }


    // =========================================================
    // DATE PICKER
    // =========================================================

    private fun showDatePicker() {

        val calendar =
            Calendar.getInstance()


        DatePickerDialog(
            this,
            { _, year, month, day ->

                val selected =
                    Calendar.getInstance()

                selected.set(
                    year,
                    month,
                    day
                )


                val formatter =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )


                selectedUploadDate =
                    formatter.format(
                        selected.time
                    )


                btnSelectDate.text =
                    selectedUploadDate


                adapter.clearSelection()


                applyFilters()

            },
            calendar.get(
                Calendar.YEAR
            ),
            calendar.get(
                Calendar.MONTH
            ),
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        ).show()
    }


    // =========================================================
    // SELECTION BUTTONS
    // =========================================================

    private fun setupSelectionButtons() {

        // =====================================================
        // SELECT ALL
        // =====================================================

        btnSelectAll.setOnClickListener {

            if (adapter.itemCount == 0) {

                Toast.makeText(
                    this,
                    "No vehicles available",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val selectedCount =
                adapter
                    .getSelectedVehicles()
                    .size

            if (
                selectedCount ==
                adapter.itemCount
            ) {

                adapter.deselectAll()

            } else {

                adapter.selectAll()
            }
        }


        // =====================================================
        // DELETE SELECTED
        // =====================================================

        btnDeleteSelected.setOnClickListener {

            deleteSelectedVehicles()
        }


        // =====================================================
        // CANCEL SELECTION
        // =====================================================

        btnCancelSelection.setOnClickListener {

            adapter.clearSelection()
        }


        // =====================================================
        // DELETE DATE
        // =====================================================

        btnDeleteDate.setOnClickListener {

            deleteDateWiseVehicles()
        }


        // =====================================================
        // INITIAL STATE
        // =====================================================

        btnSelectAll.visibility =
            View.GONE

        btnDeleteSelected.visibility =
            View.GONE

        btnCancelSelection.visibility =
            View.GONE

        btnDeleteSelected.isEnabled =
            false
    }

    // =========================================================
    // SELECTION BUTTON STATE
    // =========================================================

    private fun updateSelectionButtons(
        selectedCount: Int,
        totalCount: Int
    ) {

        btnDeleteSelected.isEnabled =
            selectedCount > 0


        btnDeleteSelected.text =
            if (selectedCount > 0) {

                "Delete Selected ($selectedCount)"

            } else {

                "Delete Selected"
            }


        btnSelectAll.text =
            if (
                totalCount > 0 &&
                selectedCount == totalCount
            ) {

                "Deselect All"

            } else {

                "Select All"
            }
    }



    // =========================================================
    // DELETE SELECTED
    // =========================================================

    private fun deleteSelectedVehicles() {

        val selected =
            adapter
                .getSelectedVehicles()


        if (selected.isEmpty()) {

            Toast.makeText(
                this,
                "Please select vehicles first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        AlertDialog.Builder(this)
            .setTitle(
                "Delete Selected Vehicles"
            )
            .setMessage(
                "Are you sure you want to delete " +
                        "${selected.size} selected vehicle(s)?"
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                homeViewModel
                    .deleteMultipleVehicles(
                        selected
                    )
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }


    // =========================================================
    // DELETE DATE-WISE DATA
    // =========================================================

    private fun deleteDateWiseVehicles() {

        val date =
            selectedUploadDate


        if (date.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Please select an upload date first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val count =
            allVehicles.count { vehicle ->

                getDateOnly(
                    vehicle.uploadDate
                ) == date
            }


        if (count == 0) {

            Toast.makeText(
                this,
                "No vehicles found for $date",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        AlertDialog.Builder(this)
            .setTitle(
                "Delete Date-Wise Data"
            )
            .setMessage(
                "This will permanently delete all " +
                        "$count vehicle(s) uploaded on $date.\n\n" +
                        "This action cannot be undone.\n\n" +
                        "Do you want to continue?"
            )
            .setPositiveButton(
                "Delete All"
            ) { _, _ ->

                homeViewModel
                    .deleteVehiclesByUploadDate(
                        date
                    )
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }


    // =========================================================
    // DELETE OBSERVERS
    // =========================================================

    private fun setupDeleteObservers() {

        homeViewModel.deleteSuccess
            .observe(this) { success ->

                if (success == true) {

                    Toast.makeText(
                        this,
                        "Vehicle Deleted Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    homeViewModel
                        .getAllVehicles()
                }
            }


        homeViewModel
            .deleteMultipleVehiclesResult
            .observe(this) { success ->

                if (success == true) {

                    Toast.makeText(
                        this,
                        "Selected vehicles deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    adapter.clearSelection()

                    homeViewModel
                        .getAllVehicles()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to delete selected vehicles",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        homeViewModel
            .deleteVehiclesByDateResult
            .observe(this) { success ->

                if (success == true) {

                    Toast.makeText(
                        this,
                        "Date-wise vehicles deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()


                    selectedUploadDate =
                        null


                    btnSelectDate.text =
                        "Select Upload Date"


                    adapter.clearSelection()


                    homeViewModel
                        .getAllVehicles()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to delete date-wise data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    // =========================================================
    // REFRESH
    // =========================================================

    private fun setupRefresh() {

        swipeRefresh
            .setOnRefreshListener {

                homeViewModel
                    .getAllVehicles()
            }
    }
    private fun setupRoleBasedVisibility() {

        if (isAdmin) {

            // =============================================
            // ADMIN
            // =============================================

            btnSelectDate.visibility =
                View.VISIBLE

            btnClearDate.visibility =
                View.VISIBLE

            btnDeleteDate.visibility =
                View.VISIBLE

            txtDateInfo.visibility =
                View.VISIBLE

            checkHeader.visibility =
                View.VISIBLE


            // Selection controls are initially hidden.
            btnSelectAll.visibility =
                View.GONE

            btnDeleteSelected.visibility =
                View.GONE

            btnCancelSelection.visibility =
                View.GONE
            uploadDate.visibility= View.VISIBLE
            vehicleaction.visibility= View.VISIBLE

        } else {

            // =============================================
            // USER
            // =============================================

            btnSelectDate.visibility =
                View.GONE

            btnClearDate.visibility =
                View.GONE

            btnSelectAll.visibility =
                View.GONE

            btnDeleteSelected.visibility =
                View.GONE

            btnDeleteDate.visibility =
                View.GONE

            btnCancelSelection.visibility =
                View.GONE

            txtDateInfo.visibility =
                View.GONE

            checkHeader.visibility =
                View.GONE
            uploadDate.visibility= View.GONE
            vehicleaction.visibility= View.GONE
        }
    }



    private fun updateSelectionModeUI(
        selectionMode: Boolean
    ) {

        if (
            !isAdmin
        ) {
            return
        }


        if (selectionMode) {

            // =============================================
            // SELECTION MODE ON
            // =============================================

            btnSelectAll.visibility =
                View.VISIBLE

            btnDeleteSelected.visibility =
                View.VISIBLE

            btnCancelSelection.visibility =
                View.VISIBLE

            /*
             * Optional:
             * Change action card title
             * if you add an ID to it later.
             */

        } else {

            // =============================================
            // SELECTION MODE OFF
            // =============================================

            btnSelectAll.visibility =
                View.GONE

            btnDeleteSelected.visibility =
                View.GONE

            btnCancelSelection.visibility =
                View.GONE

            btnDeleteSelected.isEnabled =
                false

            btnDeleteSelected.text =
                "Delete Selected"
        }
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    override fun onSupportNavigateUp():
            Boolean {

        finish()

        return true
    }
}

