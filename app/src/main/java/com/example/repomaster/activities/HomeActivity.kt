package com.example.repomaster.activities


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.repomaster.viewmodel.HomeViewModelFactory
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.repomaster.R
import com.example.repomaster.adapters.RecentSearchAdapter
import com.example.repomaster.adapters.SearchSuggestionAdapter
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.HomeViewModel

import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.addCallback

class HomeActivity : AppCompatActivity() {
    private lateinit var toolbar:
            androidx.appcompat.widget.Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var etVehicleNumber: TextInputEditText
    private lateinit var btnSearch: MaterialButton
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var suggestionAdapter: SearchSuggestionAdapter


    private lateinit var recentSearchAdapter: RecentSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//allow notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
        setContentView(R.layout.activity_home)

        //button for view whole vehicle list
        val btnviewvehicle = findViewById<MaterialButton>(R.id.btnviewvehicle)
        btnviewvehicle.setOnClickListener {
            val intent = Intent(this, ViewVehicleActivity::class.java)
            startActivity(intent)
        }
        initializeViews()
//exit function
        onBackPressedDispatcher.addCallback(this) {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

                drawerLayout.closeDrawer(GravityCompat.START)

            } else {

                AlertDialog.Builder(this@HomeActivity)
                    .setTitle("Exit")
                    .setMessage("Do you want to exit the application?")
                    .setPositiveButton("Yes") { _, _ ->
                        finishAffinity()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
        setupToolbar()

        setupNavigation()

        val factory =
            HomeViewModelFactory(applicationContext)

        homeViewModel =
            ViewModelProvider(
                this,
                factory
            )[HomeViewModel::class.java]
        val agencyId =
            SessionManager(this).getAgencyId()
        homeViewModel.syncVehicles(
            agencyId
        )
        setupUserDetails()

        observeVehicle()

        setupRecyclerViews()

        loadRecentSearches()

        setupSearchSuggestion()

        setupSearchButton()


    }

    private fun initializeViews() {

        drawerLayout =
            findViewById(R.id.drawerLayout)


        navigationView =
            findViewById(R.id.navigationView)


        toolbar =
            findViewById(R.id.toolbar)


        etVehicleNumber =
            findViewById(R.id.etVehicleNumber)


        btnSearch =
            findViewById(R.id.btnSearch)


        rvSuggestions =
            findViewById(R.id.rvSuggestions)


    }

    private fun setupToolbar() {


        setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener {

            drawerLayout.openDrawer(
                GravityCompat.START
            )

        }


        toolbar.setTitleTextColor(
            resources.getColor(R.color.white)
        )


        supportActionBar?.title =
            "Repo Master"


    }

    private fun setupNavigation() {


        navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {


                R.id.nav_home -> {

                    drawerLayout.closeDrawer(
                        GravityCompat.START
                    )

                }


                R.id.nav_profile -> {

                    startActivity(
                        Intent(
                            this,
                            ProfileActivity::class.java
                        )
                    )

                }


                R.id.nav_logout -> {

                    drawerLayout.closeDrawer(GravityCompat.START)

                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") { _, _ ->

                            SessionManager(this).logout()

                            Toast.makeText(
                                this,
                                "Logged out successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                R.id.nav_recentSearches -> {
                    startActivity(
                        Intent(
                            this,
                            SearchVehicleActivity::class.java
                        )
                    )
                }
                R.id.nav_userReport ->{
                    startActivity(
                        Intent(
                        this, UserReportActivity::class.java
                    )
                    )
                }

            }

            drawerLayout.closeDrawer(
                GravityCompat.START
            )


            true

        }


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

    private fun setupUserDetails() {


        val session =
            SessionManager(this)


        val header =
            navigationView.getHeaderView(0)


        val txtUserName =
            header.findViewById<TextView>(
                R.id.txtUserName
            )


        val txtUserEmail =
            header.findViewById<TextView>(
                R.id.txtUserEmail
            )
        val txtProfileLetter =
            header.findViewById<TextView>(R.id.txtProfileLetter)

        val userName = session.getUserName()

        txtUserName.text = "$userName"


        txtUserEmail.text =
            session.getUserEmail()
        txtProfileLetter.text =
            userName
                ?.trim()
                ?.firstOrNull()
                ?.uppercase()
                ?: "A"


    }

    private fun observeVehicle() {

        homeViewModel.vehicle.observe(this) { vehicle ->

            android.util.Log.d("HOME", "Observer Called : $vehicle")

            if (vehicle != null) {

                android.util.Log.d("HOME", "Vehicle Found")

                val intent = Intent(this, UserVehicleDetails::class.java)
                intent.putExtra("vehicleNumber", vehicle.vehicleNumber)
                startActivity(intent)

            } else {

                android.util.Log.d("HOME", "Vehicle Not Found")

                Toast.makeText(
                    this,
                    "Vehicle Not Found",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }





    private fun setupRecyclerViews() {

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


    }

    private fun setupSearchSuggestion() {


        rvSuggestions.layoutManager =
            LinearLayoutManager(this)



        suggestionAdapter = SearchSuggestionAdapter(emptyList()) { vehicle ->

            rvSuggestions.visibility = View.GONE
            etVehicleNumber.setText(vehicle.vehicleNumber)

            val session = SessionManager(this)

            homeViewModel.saveSearchHistory(
                vehicle.vehicleNumber ?: "",
                session.getUserEmail(),
                session.getUserName(),
                session.getAgencyId()
            ).observe(this) {

                loadRecentSearches()

                val intent = Intent(this, UserVehicleDetails::class.java)
                intent.putExtra("vehicleNumber", vehicle.vehicleNumber)
                startActivity(intent)
            }
        }
        rvSuggestions.adapter =
            suggestionAdapter

        etVehicleNumber.addTextChangedListener(
            object : TextWatcher {


                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }


                override fun afterTextChanged(
                    s: Editable?
                ) {
                }


                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {


                    val keyword =
                        s.toString()
                            .trim()



                    if (keyword.isEmpty()) {


                        rvSuggestions.visibility =
                            View.GONE


                        return

                    }



                    homeViewModel
                        .searchVehicles(keyword)
                        .observe(this@HomeActivity) {


                                response ->


                            if (response.isSuccessful &&
                                response.body() != null
                            ) {


                                val list =
                                    response.body()!!



                                suggestionAdapter
                                    .updateList(list)



                                rvSuggestions.visibility =
                                    if (list.isEmpty())
                                        View.GONE
                                    else
                                        View.VISIBLE


                            }


                        }


                }


            }
        )


    }


    private fun setupSearchButton() {


        btnSearch.setOnClickListener {


            val vehicleNumber =
                etVehicleNumber.text
                    .toString()
                    .trim()
                    .replace("-", "")
                    .replace("/", "")
                    .replace(".", "")
                    .replace(" ", "")
                    .uppercase()



            if (vehicleNumber.isEmpty()) {


                etVehicleNumber.error =
                    "Enter Vehicle Number"


                return@setOnClickListener

            }



            homeViewModel
                .searchVehicle(vehicleNumber)


        }


    }

}