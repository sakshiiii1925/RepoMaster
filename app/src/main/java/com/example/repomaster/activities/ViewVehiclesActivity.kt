package com.example.repomaster.activities


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast

import com.example.repomaster.viewmodel.HomeViewModelFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.repomaster.R
import com.example.repomaster.adapters.VehicleAdapter
import com.example.repomaster.models.Vehicle
import com.example.repomaster.viewmodel.HomeViewModel

import com.google.android.material.textfield.TextInputEditText
import com.example.repomaster.utils.SessionManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputLayout
import androidx.recyclerview.widget.DividerItemDecoration


class ViewVehicleActivity : AppCompatActivity() {


    private lateinit var toolbar:
            androidx.appcompat.widget.Toolbar


    private lateinit var swipeRefresh:
            SwipeRefreshLayout


    private lateinit var recyclerVehicles:
            RecyclerView


    private lateinit var homeViewModel:
            HomeViewModel


    private lateinit var etSearch:
            TextInputEditText

    private lateinit var searchLayout: TextInputLayout
    private lateinit var adapter:
            VehicleAdapter

    private lateinit var sessionManager: SessionManager
    private lateinit var agencyId: String

    private var allVehicles =
        mutableListOf<Vehicle>()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        agencyId = sessionManager.getAgencyId()
        setContentView(
            R.layout.activity_view_vehicles
        )



        toolbar =
            findViewById(R.id.toolbar)


        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title =
            "View Vehicles"



        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        swipeRefresh =
            findViewById(R.id.swipeRefresh)


        recyclerVehicles =
            findViewById(R.id.recyclerVehicles)


        etSearch =
            findViewById(R.id.etSearch)

        searchLayout = findViewById(R.id.searchLayout)

        recyclerVehicles.layoutManager =
            LinearLayoutManager(this)
        recyclerVehicles.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        val factory = HomeViewModelFactory(applicationContext)

        homeViewModel =
            ViewModelProvider(
                this,
                factory
            )[HomeViewModel::class.java]


        adapter =
            VehicleAdapter(
                emptyList(),
                homeViewModel
            )


        recyclerVehicles.adapter =
            adapter




        observeVehicles()



        setupSearch()



        setupRefresh()



        setupDeleteObserver()


        homeViewModel.getAllVehicles()



    }
    private fun observeVehicles(){



        homeViewModel.vehicleList.observe(this){ vehicles ->



            if(vehicles != null){


                allVehicles.clear()


                allVehicles.addAll(
                    vehicles
                )


                adapter.updateList(
                    allVehicles
                )


                swipeRefresh.isRefreshing =
                    false


            }



        }



    }
    private fun setupRefresh(){



        swipeRefresh.setOnRefreshListener {


            homeViewModel.getAllVehicles()


        }



    }
    private fun setupDeleteObserver(){



        homeViewModel.deleteSuccess.observe(this){ success ->



            if(success == true){

                Toast.makeText(
                    this,
                    "Vehicle Deleted Successfully",
                    Toast.LENGTH_SHORT
                ).show()



                homeViewModel.getAllVehicles()



            }
            else{


                Toast.makeText(
                    this,
                    "Delete Failed",
                    Toast.LENGTH_SHORT
                ).show()



            }

            swipeRefresh.isRefreshing =
                false



        }



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
                                (vehicle.repoStatus?.contains(search, ignoreCase = true) ?: false)

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