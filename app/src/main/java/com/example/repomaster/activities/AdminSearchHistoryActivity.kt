package com.example.repomaster.activities

import android.os.Bundle
import android.view.View
import com.example.repomaster.models.SearchHistory
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.AdminSearchHistoryAdapter
import com.example.repomaster.viewmodel.HomeViewModel
import android.widget.*
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import android.text.TextWatcher
import android.text.Editable
import com.example.repomaster.viewmodel.HomeViewModelFactory
import android.app.DatePickerDialog
import java.util.Calendar
import com.google.android.material.button.MaterialButton
import com.example.repomaster.viewmodel.UserViewModel
import com.example.repomaster.utils.SessionManager
class AdminSearchHistoryActivity : AppCompatActivity() {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchVehicle: TextInputEditText
    private lateinit var adapter: AdminSearchHistoryAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var btnClearFilters: MaterialButton
    private lateinit var btnDeleteSelected: MaterialButton
    private lateinit var userViewModel: UserViewModel
    private lateinit var spUser: AutoCompleteTextView
    private lateinit var spDate: AutoCompleteTextView
    private lateinit var checkSelectAll: MaterialCheckBox
    private lateinit var spSort: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_search_history)

        // =========================================================
        // INITIALIZE VIEWS
        // =========================================================

        toolbar = findViewById(R.id.toolbar)

        recyclerView = findViewById(R.id.recyclerSearchHistory)

        etSearchVehicle = findViewById(R.id.etSearchVehicle)

        spUser = findViewById(R.id.spUser)
        spDate = findViewById(R.id.spDate)
        spSort = findViewById(R.id.spSort)

        btnClearFilters = findViewById(R.id.btnClearFilters)
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected)

        checkSelectAll = findViewById(R.id.checkSelectAll)


        // =========================================================
        // TOOLBAR
        // =========================================================

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            resources.getColor(R.color.white)
        )

        supportActionBar?.title = "Search History"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // =========================================================
        // RECYCLER VIEW
        // =========================================================

        recyclerView.layoutManager =
            LinearLayoutManager(this)


        // =========================================================
        // VIEW MODELS
        // =========================================================

        val homeFactory =
            HomeViewModelFactory(applicationContext)

        homeViewModel =
            ViewModelProvider(
                this,
                homeFactory
            )[HomeViewModel::class.java]

        userViewModel =
            ViewModelProvider(this)[UserViewModel::class.java]


        // =========================================================
        // ADAPTER
        // =========================================================

        adapter = AdminSearchHistoryAdapter(
            emptyList(),

            // Selection changed
            { selectedCount, totalCount ->

                checkSelectAll.setOnCheckedChangeListener(null)

                checkSelectAll.isChecked =
                    totalCount > 0 &&
                            selectedCount == totalCount

                checkSelectAll.setOnCheckedChangeListener { _, isChecked ->

                    if (isChecked) {
                        adapter.selectAll()
                    } else {
                        adapter.clearSelection()
                    }
                }
            },

            // Selection mode changed
            { selectionMode ->

                if (selectionMode) {

                    checkSelectAll.visibility =
                        View.VISIBLE

                    btnDeleteSelected.visibility =
                        View.VISIBLE

                } else {

                    checkSelectAll.visibility =
                        View.GONE

                    btnDeleteSelected.visibility =
                        View.GONE

                    checkSelectAll.setOnCheckedChangeListener(null)

                    checkSelectAll.isChecked = false

                    checkSelectAll.setOnCheckedChangeListener { _, isChecked ->

                        if (isChecked) {
                            adapter.selectAll()
                        } else {
                            adapter.clearSelection()
                        }
                    }
                }
            }
        )

        recyclerView.adapter = adapter


        // =========================================================
        // SORT DROPDOWN
        // =========================================================

        val sortList = listOf(
            "Newest First",
            "Oldest First"
        )

        spSort.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                sortList
            )
        )


        // =========================================================
        // SORT LISTENER
        // =========================================================

        spSort.setOnItemClickListener { _, _, position, _ ->

            val order =
                if (position == 0)
                    "newest"
                else
                    "oldest"

            val agencyId =
                SessionManager(this).getAgencyId()

            homeViewModel
                .sortSearchHistory(
                    agencyId,
                    order
                )
                .observe(this) { response ->

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        adapter.updateData(
                            response.body()!!
                        )
                    }
                }
        }


        // =========================================================
        // USER FILTER
        // =========================================================

        spUser.setOnItemClickListener { _, _, _, _ ->

            val selectedUser =
                spUser.text.toString()

            if (selectedUser == "All Users") {

                loadSearchHistory()

            } else {

                val agencyId =
                    SessionManager(this).getAgencyId()

                homeViewModel
                    .filterByUser(
                        agencyId,
                        selectedUser
                    )
                    .observe(this) { response ->

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            adapter.updateData(
                                response.body()!!
                            )
                        }
                    }
            }
        }


        // =========================================================
        // DATE FILTER
        // =========================================================

        spDate.setOnClickListener {

            val calendar =
                Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    val selectedDate =
                        String.format(
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            day
                        )

                    spDate.setText(
                        selectedDate,
                        false
                    )

                    val agencyId =
                        SessionManager(this).getAgencyId()

                    homeViewModel
                        .filterByDate(
                            agencyId,
                            selectedDate
                        )
                        .observe(this) { response ->

                            if (
                                response.isSuccessful &&
                                response.body() != null
                            ) {

                                adapter.updateData(
                                    response.body()!!
                                )
                            }
                        }

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

            ).show()
        }


        // =========================================================
        // SELECT ALL
        // =========================================================

        checkSelectAll.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {

                adapter.selectAll()

            } else {

                adapter.clearSelection()
            }
        }


        // =========================================================
        // DELETE SELECTED
        // =========================================================

        btnDeleteSelected.setOnClickListener {

            val selectedIds =
                adapter.getSelectedIds()

            if (selectedIds.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please select at least one search history",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            showBulkDeleteConfirmation(
                selectedIds
            )
        }


        // =========================================================
        // CLEAR FILTERS
        // =========================================================

        btnClearFilters.setOnClickListener {

            etSearchVehicle.setText("")

            spUser.setText(
                "",
                false
            )

            spDate.setText(
                "",
                false
            )

            spSort.setText(
                "",
                false
            )

            checkSelectAll.setOnCheckedChangeListener(null)

            checkSelectAll.isChecked = false

            checkSelectAll.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    adapter.selectAll()
                } else {
                    adapter.clearSelection()
                }
            }

            loadSearchHistory()

            Toast.makeText(
                this,
                "Filters cleared",
                Toast.LENGTH_SHORT
            ).show()
        }


        // =========================================================
        // VEHICLE SEARCH
        // =========================================================

        etSearchVehicle.addTextChangedListener(
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
                        s.toString().trim()

                    if (keyword.isEmpty()) {

                        loadSearchHistory()

                    } else {

                        val agencyId =
                            SessionManager(
                                this@AdminSearchHistoryActivity
                            ).getAgencyId()

                        homeViewModel
                            .searchHistoryByVehicle(
                                agencyId,
                                keyword
                            )
                            .observe(
                                this@AdminSearchHistoryActivity
                            ) { response ->

                                if (
                                    response.isSuccessful &&
                                    response.body() != null
                                ) {

                                    adapter.updateData(
                                        response.body()!!
                                    )
                                }
                            }
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )


        // =========================================================
        // LOAD USERS
        // =========================================================

        loadUsers()


        // =========================================================
        // LOAD SEARCH HISTORY
        // =========================================================

        loadSearchHistory()


        // =========================================================
        // DELETE RESULT
        // =========================================================

        homeViewModel
            .deleteSearchHistoryResult
            .observe(this) { success ->

                if (success) {

                    loadSearchHistory()

                    Toast.makeText(
                        this,
                        "Search history deleted",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to delete search history",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }





    //load user in dropdown
    private fun loadUsers() {

        val agencyId = SessionManager(this@AdminSearchHistoryActivity).getAgencyId()

        userViewModel.getApprovedUsers(agencyId)
            .observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    val userNames = mutableListOf("All Users")

                    response.body()!!.forEach {
                        userNames.add(it.fullName)
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        userNames
                    )

                    spUser.setAdapter(adapter)
                }
            }
    }
    private fun loadSearchHistory() {

        val agencyId = SessionManager(this@AdminSearchHistoryActivity).getAgencyId()

        homeViewModel.getSearchHistory(agencyId).observe(this) { response ->

            if (response.isSuccessful && response.body() != null) {

                adapter.updateData(response.body()!!)

            } else {

                Toast.makeText(
                    this,
                    "No Search History Found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun showBulkDeleteConfirmation(
        selectedIds: List<Long>
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Search History")
            .setMessage(
                "Are you sure you want to delete " +
                        "${selectedIds.size} selected search history record(s)?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                homeViewModel
                    .deleteMultipleSearchHistory(selectedIds)
                    .observe(this) { success ->

                        if (success) {

                            Toast.makeText(
                                this,
                                "${selectedIds.size} search history record(s) deleted",
                                Toast.LENGTH_SHORT
                            ).show()

                            loadSearchHistory()

                        } else {

                            Toast.makeText(
                                this,
                                "Failed to delete search history",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            .show()
    }
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}