package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapter.YardVehicleAdapter
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.YardViewModel
import com.google.android.material.textfield.TextInputEditText
import com.example.repomaster.models.Vehicle
import android.text.Editable
import android.text.TextWatcher
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
class YardVehiclesActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var txtYardName: TextView
    private lateinit var txtVehicleCount: TextView
    private lateinit var txtNoVehicles: TextView

    private lateinit var recyclerYardVehicles: RecyclerView

    private lateinit var progressYardVehicles: View

    private lateinit var yardViewModel: YardViewModel

    private lateinit var adapter: YardVehicleAdapter
    private lateinit var etSearchVehicle: TextInputEditText
    private lateinit var swipeRefreshYardVehicles: SwipeRefreshLayout
    private var allVehicles: List<Vehicle> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_yard_vehicles)

        initializeViews()

        // -------------------------
        // Toolbar
        // -------------------------

        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title = "Yard Vehicles"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // -------------------------
        // Get Yard Information
        // -------------------------

        val yardId =
            intent.getLongExtra("yardId", -1L)

        val yardName =
            intent.getStringExtra("yardName")
                ?: "Yard"


        if (yardId == -1L) {

            Toast.makeText(
                this,
                "Invalid yard",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        txtYardName.text = yardName


        // -------------------------
        // RecyclerView
        // -------------------------

        adapter =
            YardVehicleAdapter(
                emptyList(),

                // Vehicle click
                { vehicle ->

                    val intent =
                        Intent(
                            this,
                            VehicleDetailsActivity::class.java
                        )

                    intent.putExtra(
                        "vehicleNumber",
                        vehicle.vehicleNumber
                    )

                    startActivity(intent)
                },

                // Remove from yard
                { vehicle ->

                    showRemoveYardConfirmation(vehicle)
                }
            )

        recyclerYardVehicles.layoutManager =
            LinearLayoutManager(this)

        recyclerYardVehicles.adapter =
            adapter


        // -------------------------
        // ViewModel
        // -------------------------

        yardViewModel =
            ViewModelProvider(this)[YardViewModel::class.java]

        // Agency ID

        val agencyId =
            SessionManager(this).getAgencyId()
        yardViewModel.removeVehicleFromYardResponse.observe(
            this
        ) { response ->

            if (response.isSuccessful) {

                Toast.makeText(
                    this,
                    "Vehicle removed from yard",
                    Toast.LENGTH_SHORT
                ).show()

                // Reload current yard vehicles
                yardViewModel.getVehiclesByYard(
                    yardId,
                    agencyId
                )

            } else {

                Toast.makeText(
                    this,
                    "Failed to remove vehicle (${response.code()})",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        yardViewModel.removeVehicleFromYardError.observe(
            this
        ) { error ->

            Toast.makeText(
                this,
                error,
                Toast.LENGTH_LONG
            ).show()
        }

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // -------------------------
        // Load Vehicles
        // -------------------------

        progressYardVehicles.visibility =
            View.VISIBLE

        yardViewModel.getVehiclesByYard(
            yardId,
            agencyId
        )


        // -------------------------
        // Observe Vehicles
        // -------------------------

        yardViewModel.yardVehicles.observe(
            this
        ) { response ->

            progressYardVehicles.visibility =
                View.GONE

            swipeRefreshYardVehicles.isRefreshing = false
            if (response.isSuccessful) {

                // Save complete vehicle list
                allVehicles =
                    response.body()
                        ?: emptyList()

                // Vehicle count
                txtVehicleCount.text =
                    "Vehicles: ${allVehicles.size}"

                if (allVehicles.isEmpty()) {

                    recyclerYardVehicles.visibility =
                        View.GONE

                    txtNoVehicles.visibility =
                        View.VISIBLE

                    txtNoVehicles.text =
                        "No vehicles assigned to this yard"

                } else {

                    recyclerYardVehicles.visibility =
                        View.VISIBLE

                    txtNoVehicles.visibility =
                        View.GONE

                    adapter.updateList(
                        allVehicles
                    )
                }

            } else {

                recyclerYardVehicles.visibility =
                    View.GONE

                txtNoVehicles.visibility =
                    View.VISIBLE

                txtNoVehicles.text =
                    "Failed to load vehicles (${response.code()})"

                Toast.makeText(
                    this,
                    "Failed to load yard vehicles",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }


        // -------------------------
        // Error Observer
        // -------------------------

        yardViewModel.yardVehiclesError.observe(
            this
        ) { error ->

            progressYardVehicles.visibility =
                View.GONE

            swipeRefreshYardVehicles.isRefreshing = false
            Toast.makeText(
                this,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
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

                    val searchText =
                        s?.toString()?.trim() ?: ""

                    // -------------------------
                    // Search cleared
                    // -------------------------

                    if (searchText.isEmpty()) {

                        adapter.updateList(
                            allVehicles
                        )

                        txtVehicleCount.text =
                            "Vehicles: ${allVehicles.size}"

                        if (allVehicles.isEmpty()) {

                            recyclerYardVehicles.visibility =
                                View.GONE

                            txtNoVehicles.visibility =
                                View.VISIBLE

                            txtNoVehicles.text =
                                "No vehicles assigned to this yard"

                        } else {

                            recyclerYardVehicles.visibility =
                                View.VISIBLE

                            txtNoVehicles.visibility =
                                View.GONE
                        }

                        return
                    }

                    // -------------------------
                    // Filter Vehicles
                    // -------------------------

                    val filteredVehicles =
                        allVehicles.filter { vehicle ->

                            vehicle.vehicleNumber
                                ?.contains(
                                    searchText,
                                    ignoreCase = true
                                ) == true ||

                                    vehicle.ownerName
                                        ?.contains(
                                            searchText,
                                            ignoreCase = true
                                        ) == true ||

                                    vehicle.id?.loanNumber
                                        ?.contains(
                                            searchText,
                                            ignoreCase = true
                                        ) == true
                        }

                    // -------------------------
                    // Update Adapter
                    // -------------------------

                    adapter.updateList(
                        filteredVehicles
                    )

                    // -------------------------
                    // Update Count
                    // -------------------------

                    txtVehicleCount.text =
                        "Vehicles: ${filteredVehicles.size}"

                    // -------------------------
                    // Empty Search Result
                    // -------------------------

                    if (filteredVehicles.isEmpty()) {

                        recyclerYardVehicles.visibility =
                            View.GONE

                        txtNoVehicles.visibility =
                            View.VISIBLE

                        txtNoVehicles.text =
                            "No vehicles found"

                    } else {

                        recyclerYardVehicles.visibility =
                            View.VISIBLE

                        txtNoVehicles.visibility =
                            View.GONE
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
        swipeRefreshYardVehicles.setOnRefreshListener {

            yardViewModel.getVehiclesByYard(
                yardId,
                agencyId
            )
        }

    }


    // -------------------------
    // Initialize Views
    // -------------------------

    private fun initializeViews() {

        toolbar =
            findViewById(R.id.toolbar)

        txtYardName =
            findViewById(R.id.txtYardName)

        txtVehicleCount =
            findViewById(R.id.txtVehicleCount)

        txtNoVehicles =
            findViewById(R.id.txtNoVehicles)

        recyclerYardVehicles =
            findViewById(R.id.recyclerYardVehicles)

        progressYardVehicles =
            findViewById(R.id.progressYardVehicles)
        etSearchVehicle = findViewById(R.id.etSearchVehicle)
        swipeRefreshYardVehicles =
            findViewById(R.id.swipeRefreshYardVehicles)
    }

    private fun showRemoveYardConfirmation(
        vehicle: Vehicle
    ) {

        val vehicleNumber =
            vehicle.vehicleNumber ?: return

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Remove Vehicle")
            .setMessage(
                "Remove $vehicleNumber from this yard?"
            )
            .setPositiveButton("Remove") { _, _ ->

                yardViewModel.removeVehicleFromYard(
                    vehicleNumber
                )
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }
    // -------------------------
    // Back Button
    // -------------------------
    override fun onResume() {
        super.onResume()

        if (::yardViewModel.isInitialized) {

            val yardId =
                intent.getLongExtra("yardId", -1L)

            val agencyId =
                SessionManager(this).getAgencyId()

            if (yardId != -1L && !agencyId.isNullOrEmpty()) {

                // Clear previous search
                etSearchVehicle.setText("")

                progressYardVehicles.visibility = View.VISIBLE

                yardViewModel.getVehiclesByYard(
                    yardId,
                    agencyId
                )
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}