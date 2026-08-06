package com.example.repomaster.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapter.VehicleReportAdapter
import com.example.repomaster.models.VehicleReport
import com.example.repomaster.viewmodel.UserViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.utils.SessionManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
class VehicleReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VehicleReportAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var userViewModel: UserViewModel
    private lateinit var agencyId: String
    private lateinit var searchLayout: TextInputLayout
    private var allVehicles = ArrayList<VehicleReport>()
    private lateinit var etSearch:
            TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_report)
        //initialization
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        agencyId = SessionManager(this).getAgencyId()
        val finance = intent.getStringExtra("FINANCE")
        val branch = intent.getStringExtra("BRANCH")
        val status = intent.getStringExtra("STATUS") ?: "ALL"
        val year = intent.getStringExtra("YEAR")
        val month = intent.getStringExtra("MONTH")
        searchLayout = findViewById(R.id.searchLayout)
        supportActionBar?.title = "$status Vehicles"
        etSearch =
            findViewById(R.id.etSearch1)
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.rvVehicles)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "View Vehicles"
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        userViewModel.getVehicleReport(
            agencyId,
            finance,
            branch,
            year,
            month,
            status
        ).observe(this) { response ->

            if (response.isSuccessful) {

                allVehicles.clear()
                allVehicles.addAll(response.body() ?: emptyList())

                adapter = VehicleReportAdapter(allVehicles)

                recyclerView.adapter = adapter

            } else {

                Toast.makeText(
                    this,
                    "No Vehicles Found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        setupSearch()

    }
    private fun setupSearch(){



        etSearch.addTextChangedListener(
            object : TextWatcher {



                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ){}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ){

                    val search =
                        s.toString()
                            .trim()



                    if(search.isEmpty()){


                        adapter.updateList(
                            allVehicles
                        )


                        return

                    }

                    val filteredList = allVehicles.filter { vehicle ->

                        (vehicle.vehicleNumber?.contains(search, ignoreCase = true) ?: false) ||
                                (vehicle.ownerName?.contains(search, ignoreCase = true) ?: false) ||
                                (vehicle.loanNumber?.contains(search, ignoreCase = true) ?: false)

                    }

                    adapter.updateList(
                        filteredList
                    )
                    if (search.isEmpty()) {

                        searchLayout.boxStrokeColor =
                            getColor(R.color.white)

                    } else {

                        searchLayout.boxStrokeColor =
                            getColor(R.color.white)

                    }


                }

                override fun afterTextChanged(
                    s: Editable?
                ){}



            }
        )



    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}