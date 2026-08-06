package com.example.repomaster.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.AdminSearchHistoryAdapter
import com.example.repomaster.viewmodel.HomeViewModel
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import android.text.TextWatcher
import android.text.Editable
import android.app.DatePickerDialog
import java.util.Calendar
import com.example.repomaster.viewmodel.UserViewModel
import com.example.repomaster.utils.SessionManager
class AdminSearchHistoryActivity : AppCompatActivity() {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchVehicle: TextInputEditText
    private lateinit var adapter: AdminSearchHistoryAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var spUser: AutoCompleteTextView
    private lateinit var spDate: AutoCompleteTextView
    private lateinit var spSort: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_search_history)
        //toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        supportActionBar?.title = "Search History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //initialize variables
        spUser = findViewById(R.id.spUser)
        spDate = findViewById(R.id.spDate)
        spSort = findViewById(R.id.spSort)
        //sort dropdown
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
        //sort listner
        spSort.setOnItemClickListener { _, _, position, _ ->

            val order =
                if (position == 0)
                    "newest"
                else
                    "oldest"

            val agencyId = SessionManager(this).getAgencyId()

            homeViewModel.sortSearchHistory(
                agencyId,
                order
            )
                .observe(this) {

                    if (it.isSuccessful && it.body() != null) {

                        adapter.updateData(it.body()!!)
                    }
                }
        }

        //listner for user
        spUser.setOnItemClickListener { _, _, _, _ ->

            val selectedUser = spUser.text.toString()

            if (selectedUser == "All Users") {

                loadSearchHistory()

            } else {

                val selectedUser = spUser.text.toString()

                val agencyId = SessionManager(this).getAgencyId()

                homeViewModel.filterByUser(
                    agencyId,
                    selectedUser
                )
                    .observe(this) { response ->

                        if (response.isSuccessful && response.body() != null) {

                            adapter.updateData(response.body()!!)
                        }
                    }
            }
        }
        //datepicker
        spDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    val selectedDate = String.format(
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        day
                    )

                    spDate.setText(selectedDate)

                    val agencyId = SessionManager(this).getAgencyId()

                    homeViewModel.filterByDate(
                        agencyId,
                        selectedDate
                    )
                        .observe(this) {

                            if (it.isSuccessful && it.body() != null) {

                                adapter.updateData(it.body()!!)
                            }
                        }

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        etSearchVehicle = findViewById(R.id.etSearchVehicle)
        //search number
        etSearchVehicle.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val keyword = s.toString()

                if (keyword.isEmpty()) {

                    loadSearchHistory()

                } else {
                    val agencyId = SessionManager(this@AdminSearchHistoryActivity).getAgencyId()

                    homeViewModel.searchHistoryByVehicle(
                        agencyId,
                        keyword
                    )
                        .observe(this@AdminSearchHistoryActivity) { response ->

                            if (response.isSuccessful && response.body() != null) {

                                adapter.updateData(response.body()!!)

                            }
                        }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        recyclerView = findViewById(R.id.recyclerSearchHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminSearchHistoryAdapter(emptyList())
        recyclerView.adapter = adapter
//viewmodels
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        loadUsers()
        loadSearchHistory()
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
    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}