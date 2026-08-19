package com.example.repomaster.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.RecentSearchAdapter
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.HomeViewModel
import com.google.android.material.textfield.TextInputEditText
import com.example.repomaster.viewmodel.HomeViewModelFactory

class SearchVehicleActivity : AppCompatActivity() {
    private lateinit var toolbar:
            androidx.appcompat.widget.Toolbar

    private lateinit var etVehicleNumber: TextInputEditText

    private lateinit var homeViewModel: HomeViewModel


    private lateinit var rvRecentSearch: RecyclerView

    private lateinit var recentSearchAdapter: RecentSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_vehicle)

        initializeViews()
        setupToolbar()



        val factory = HomeViewModelFactory(applicationContext)

        homeViewModel =
            ViewModelProvider(
                this,
                factory
            )[HomeViewModel::class.java]
        setupRecyclerViews()

        loadRecentSearches()


    }

    private fun initializeViews() {


        toolbar =
            findViewById(R.id.toolbar)


        rvRecentSearch =
            findViewById(R.id.rvRecentSearch)


    }

    private fun setupToolbar() {


        setSupportActionBar(toolbar)


        toolbar.setTitleTextColor(
            resources.getColor(R.color.white)
        )

        supportActionBar?.title =
            "Recent Search"
    }

    private fun loadRecentSearches() {

        val agencyId = SessionManager(this).getAgencyId()

        homeViewModel.getSearchHistory(agencyId)

            .observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    recentSearchAdapter.updateData(response.body()!!)
                }
            }
    }


    private fun setupRecyclerViews() {


        rvRecentSearch.layoutManager =
            LinearLayoutManager(this)



        recentSearchAdapter =
            RecentSearchAdapter(emptyList()) {

                    history ->


                etVehicleNumber.setText(
                    history.vehicleNumber
                )


                homeViewModel.searchVehicle(
                    history.vehicleNumber
                )

            }



        rvRecentSearch.adapter =
            recentSearchAdapter


    }

    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}