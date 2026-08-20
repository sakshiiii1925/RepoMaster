package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.RecentSearchAdapter
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.HomeViewModel
import com.example.repomaster.viewmodel.HomeViewModelFactory

class SearchVehicleActivity : AppCompatActivity() {

    private lateinit var toolbar:
            androidx.appcompat.widget.Toolbar

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var rvRecentSearch: RecyclerView

    private lateinit var recentSearchAdapter: RecentSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_vehicle)

        initializeViews()

        setupToolbar()

        val factory =
            HomeViewModelFactory(applicationContext)

        homeViewModel =
            ViewModelProvider(
                this,
                factory
            )[HomeViewModel::class.java]

        setupRecyclerViews()

        loadRecentSearches()
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private fun initializeViews() {

        toolbar =
            findViewById(R.id.toolbar)

        rvRecentSearch =
            findViewById(R.id.rvRecentSearch)
    }

    // =========================================================
    // TOOLBAR
    // =========================================================

    private fun setupToolbar() {

        setSupportActionBar(toolbar)

        toolbar.setTitleTextColor(
            resources.getColor(R.color.white)
        )

        supportActionBar?.title =
            "Recent Search"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // =========================================================
    // LOAD RECENT SEARCHES
    // =========================================================

    private fun loadRecentSearches() {

        val agencyId =
            SessionManager(this).getAgencyId()

        homeViewModel
            .getSearchHistory(agencyId)
            .observe(this) { response ->

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    recentSearchAdapter.updateData(
                        response.body()!!
                    )
                }
            }
    }

    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private fun setupRecyclerViews() {

        rvRecentSearch.layoutManager =
            LinearLayoutManager(this)

        recentSearchAdapter =
            RecentSearchAdapter(emptyList()) { history ->

                val vehicleNumber =
                    history.vehicleNumber
                        ?.trim()
                        ?.replace("-", "")
                        ?.replace("/", "")
                        ?.replace(".", "")
                        ?.replace(" ", "")
                        ?.uppercase()
                        ?: return@RecentSearchAdapter

                // -------------------------------------------------
                // Open vehicle details
                // -------------------------------------------------

                val intent =
                    Intent(
                        this,
                        UserVehicleDetails::class.java
                    )

                intent.putExtra(
                    "vehicleNumber",
                    vehicleNumber
                )

                startActivity(intent)
            }

        rvRecentSearch.adapter =
            recentSearchAdapter
    }

    // =========================================================
    // BACK BUTTON
    // =========================================================

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}