package com.example.repomaster.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.models.Yard
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.YardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import android.graphics.Color
import android.view.View
import android.widget.TableLayout
import com.example.repomaster.models.Vehicle
import com.example.repomaster.utils.FileDownloader
import com.example.repomaster.utils.PdfReportGenerator
class YardReportActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var autoYard: MaterialAutoCompleteTextView

    private lateinit var txtSelectedYard: TextView
    private lateinit var txtVehicleCount: TextView

    private lateinit var txtRepoMarked: TextView
    private lateinit var txtParked: TextView
    private lateinit var txtReleased: TextView
    private lateinit var tableVehicleReport: TableLayout
    private var selectedStatus = "ALL"
    private var yardVehicleList: List<Vehicle> = emptyList()
    private lateinit var btnDownloadPdf: MaterialButton
    private lateinit var btnDownloadExcel: MaterialButton

    private lateinit var progressYardReport: View

    private lateinit var yardViewModel: YardViewModel
    private var selectedVehicles: List<String> = emptyList()
    private var yardList: List<Yard> = emptyList()

    private var selectedYard: Yard? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_yard_report)

        initializeViews()

        setupToolbar()

        yardViewModel =
            ViewModelProvider(this)[YardViewModel::class.java]

        loadYards()

        observeYards()
        observeYardExcel()
        observeYardVehicles()
        setupSummaryClicks()
        setupYardSelection()

        btnDownloadPdf.setOnClickListener {

            downloadYardPdf()
        }

        btnDownloadExcel.setOnClickListener {

            downloadYardExcel()
        }
    }
    //download excel
    private fun downloadYardExcel() {

        val yard = selectedYard

        if (yard == null) {

            Toast.makeText(
                this,
                "Please select a yard first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val agencyId =
            SessionManager(this).getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        yardViewModel.downloadYardExcel(
            yard.id!!,
            agencyId,
            selectedStatus
        )
    }
    //observe excel
    private fun observeYardExcel() {

        yardViewModel.yardExcelResponse
            .observe(this) { response ->

                if (response.isSuccessful &&
                    response.body() != null
                ) {

                    val fileName =
                        "${selectedYard?.yardName ?: "Yard"}_Report.xlsx"

                    val uri =
                        FileDownloader(this).saveExcel(
                            response.body()!!,
                            fileName
                        )

                    if (uri != null) {

                        Toast.makeText(
                            this,
                            "Yard Excel saved in Downloads",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        Toast.makeText(
                            this,
                            "Excel download failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Failed to download Yard Excel",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        yardViewModel.yardExcelError
            .observe(this) { error ->

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    //download pdf
    private fun downloadYardPdf() {

        val yard = selectedYard

        if (yard == null) {
            Toast.makeText(
                this,
                "Please select a yard first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (yardVehicleList.isEmpty()) {
            Toast.makeText(
                this,
                "No vehicles assigned to this yard",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val agencyId = SessionManager(this).getAgencyId()

        if (agencyId.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Apply currently selected filter
        val filteredVehicles = when (selectedStatus.uppercase()) {

            "ALL" -> yardVehicleList

            else -> yardVehicleList.filter {
                it.repoStatus.equals(
                    selectedStatus,
                    ignoreCase = true
                )
            }
        }

        if (filteredVehicles.isEmpty()) {
            Toast.makeText(
                this,
                "No vehicles found for selected filter",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Create PDF rows
        val rows = filteredVehicles.mapIndexed { index, vehicle ->

            listOf(
                (index + 1).toString(),
                vehicle.vehicleNumber ?: "N/A",
                vehicle.repoStatus ?: "N/A"
            )
        }

        val filterName = when (selectedStatus.uppercase()) {

            "ALL" -> "All Vehicles"

            "REPO MARK" -> "Repo Marked"

            "PARKED" -> "Parked"

            "RELEASED" -> "Released"

            else -> selectedStatus
        }

        val safeYardName =
            yard.yardName
                .replace(" ", "_")
                .replace("/", "_")

        val fileName =
            "Yard_${safeYardName}_${filterName.replace(" ", "_")}.pdf"

        PdfReportGenerator(this).generateReport(

            title = "Yard Vehicle Report",

            agencyId = agencyId,

            headers = listOf(
                "Sr. No.",
                "Vehicle Number",
                "Status"
            ),

            rows = rows,

            fileName = fileName
        )
    }
    private fun initializeViews() {

        toolbar = findViewById(R.id.toolbar)

        autoYard = findViewById(R.id.autoYard)

        txtSelectedYard =
            findViewById(R.id.txtSelectedYard)

        txtVehicleCount =
            findViewById(R.id.txtVehicleCount)

        txtRepoMarked =
            findViewById(R.id.txtRepoMarked)

        txtParked =
            findViewById(R.id.txtParked)

        txtReleased =
            findViewById(R.id.txtReleased)
        tableVehicleReport =
            findViewById(R.id.tableVehicleReport)

        btnDownloadPdf =
            findViewById(R.id.btnDownloadPdf)

        btnDownloadExcel =
            findViewById(R.id.btnDownloadExcel)

        progressYardReport =
            findViewById(R.id.progressYardReport)
    }

    private fun setupToolbar() {

        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title = "Yard Report"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {

            finish()
        }
    }

    private fun loadYards() {

        val agencyId =
            SessionManager(this).getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        yardViewModel.getYards(agencyId)
    }

    private fun observeYards() {

        yardViewModel.yards.observe(this) { response ->

            if (response.isSuccessful) {

                yardList =
                    response.body() ?: emptyList()

                val yardNames =
                    yardList.map {
                        it.yardName
                    }

                val adapter =
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        yardNames
                    )

                autoYard.setAdapter(adapter)

            } else {

                Toast.makeText(
                    this,
                    "Failed to load yards",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupYardSelection() {

        autoYard.setOnItemClickListener { _, _, position, _ ->

            selectedYard =
                yardList[position]

            val yard =
                selectedYard

            if (yard != null) {

                txtSelectedYard.text =
                    "Selected Yard: ${yard.yardName}"

                loadVehiclesForYard(yard)
            }
        }
    }

    private fun loadVehiclesForYard(yard: Yard) {

        val agencyId =
            SessionManager(this).getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        progressYardReport.visibility =
            View.VISIBLE

        yardViewModel.getVehiclesByYard(
            yard.id!!,
            agencyId
        )
    }

    private fun observeYardVehicles() {

        yardViewModel.yardVehicles.observe(this) { response ->

            progressYardReport.visibility = View.GONE

            if (response.isSuccessful) {

                val vehicles =
                    response.body() ?: emptyList()

                yardVehicleList = vehicles

                selectedStatus = "ALL"
                updateSelectedCard(
                    txtVehicleCount
                )

                displayVehicles(vehicles)
            } else {

                Toast.makeText(
                    this,
                    "Failed to load vehicles",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        yardViewModel.yardVehiclesError.observe(this) { error ->

            progressYardReport.visibility = View.GONE

            Toast.makeText(
                this,
                error,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayVehicles(
        vehicles: List<Vehicle>
    ) {

        // Keep header row
        while (tableVehicleReport.childCount > 1) {
            tableVehicleReport.removeViewAt(1)
        }

        txtVehicleCount.text =
            "Vehicles: ${vehicles.size}"

        val totalVehicles =
            vehicles.size

        val repoMarked =
            vehicles.count {
                it.repoStatus.equals(
                    "repo mark",
                    ignoreCase = true
                )
            }

        val parked =
            vehicles.count {
                it.repoStatus.equals(
                    "parked",
                    ignoreCase = true
                )
            }

        val released =
            vehicles.count {
                it.repoStatus.equals(
                    "released",
                    ignoreCase = true
                )
            }

        txtRepoMarked.text =
            "Repo Marked: $repoMarked"

        txtParked.text =
            "Parked: $parked"

        txtReleased.text =
            "Released: $released"
        vehicles.forEachIndexed { index, vehicle ->

            val vehicleNumber =
                vehicle.vehicleNumber

            if (vehicleNumber.isNullOrBlank()) {
                return@forEachIndexed
            }

            val row = TableRow(this)

            // -------------------------
            // Sr. No.
            // -------------------------
            val srText = TextView(this)

            srText.text =
                (index + 1).toString()

            srText.setPadding(
                10,
                10,
                10,
                10
            )

            srText.gravity =
                android.view.Gravity.CENTER

            srText.setTextColor(Color.WHITE)

            srText.background =
                getDrawable(
                    R.drawable.table_cell_border
                )


            // -------------------------
            // Vehicle Number
            // -------------------------
            val vehicleText = TextView(this)

            vehicleText.text =
                vehicleNumber

            vehicleText.setPadding(
                10,
                10,
                10,
                10
            )

            vehicleText.gravity =
                android.view.Gravity.CENTER

            vehicleText.setTextColor(Color.WHITE)

            vehicleText.textSize = 16f

            vehicleText.isClickable = true

            vehicleText.background =
                getDrawable(
                    R.drawable.table_cell_border
                )


            // -------------------------
            // Status
            // -------------------------
            val statusText = TextView(this)

            statusText.text =
                vehicle.repoStatus ?: "N/A"

            statusText.setPadding(
                10,
                10,
                10,
                10
            )

            statusText.gravity =
                android.view.Gravity.CENTER

            statusText.setTextColor(Color.WHITE)

            statusText.textSize = 15f

            statusText.background =
                getDrawable(
                    R.drawable.table_cell_border
                )


            // -------------------------
            // Vehicle Click
            // -------------------------
            vehicleText.setOnClickListener {

                val intent =
                    Intent(
                        this,
                        VehicleInfo::class.java
                    )

                intent.putExtra(
                    "vehicleNumber",
                    vehicleNumber
                )

                startActivity(intent)
            }


            row.addView(srText)

            row.addView(vehicleText)

            row.addView(statusText)

            tableVehicleReport.addView(row)
        }
    }
    private fun setupSummaryClicks() {

        // Total Vehicles
        txtVehicleCount.setOnClickListener {

            selectedStatus = "ALL"

            updateSelectedCard(
                txtVehicleCount
            )

            displayVehicles(
                yardVehicleList
            )
        }


        // Repo Marked
        txtRepoMarked.setOnClickListener {

            selectedStatus = "repo mark"

            updateSelectedCard(
                txtRepoMarked
            )

            filterVehiclesByStatus(
                "repo mark"
            )
        }


        // Parked
        txtParked.setOnClickListener {

            selectedStatus = "parked"

            updateSelectedCard(
                txtParked
            )

            filterVehiclesByStatus(
                "parked"
            )
        }


        // Released
        txtReleased.setOnClickListener {

            selectedStatus = "released"

            updateSelectedCard(
                txtReleased
            )

            filterVehiclesByStatus(
                "released"
            )
        }
    }
    private fun filterVehiclesByStatus(
        status: String
    ) {

        val filteredVehicles =
            yardVehicleList.filter {

                it.repoStatus.equals(
                    status,
                    ignoreCase = true
                )
            }

        displayFilteredVehicles(
            filteredVehicles
        )
    }
    private fun displayFilteredVehicles(
        vehicles: List<Vehicle>
    ) {

        while (tableVehicleReport.childCount > 1) {

            tableVehicleReport.removeViewAt(1)
        }

        vehicles.forEachIndexed { index, vehicle ->

            val vehicleNumber =
                vehicle.vehicleNumber

            if (vehicleNumber.isNullOrBlank()) {
                return@forEachIndexed
            }

            val row =
                TableRow(this)

            // Sr No
            val srText =
                TextView(this)

            srText.text =
                (index + 1).toString()

            srText.setPadding(
                10,
                10,
                10,
                10
            )

            srText.gravity =
                android.view.Gravity.CENTER

            srText.setTextColor(
                Color.WHITE
            )

            srText.background =
                getDrawable(
                    R.drawable.table_cell_border
                )


            // Vehicle Number
            val vehicleText =
                TextView(this)

            vehicleText.text =
                vehicleNumber

            vehicleText.setPadding(
                10,
                10,
                10,
                10
            )

            vehicleText.gravity =
                android.view.Gravity.CENTER

            vehicleText.setTextColor(
                Color.WHITE
            )

            vehicleText.textSize =
                16f

            vehicleText.isClickable =
                true

            vehicleText.background =
                getDrawable(
                    R.drawable.table_cell_border
                )


            // Status
            val statusText =
                TextView(this)

            statusText.text =
                vehicle.repoStatus ?: "N/A"

            statusText.setPadding(
                10,
                10,
                10,
                10
            )

            statusText.gravity =
                android.view.Gravity.CENTER

            statusText.setTextColor(
                Color.WHITE
            )

            statusText.textSize =
                15f

            statusText.background =
                getDrawable(
                    R.drawable.table_cell_border
                )


            // Vehicle Details
            vehicleText.setOnClickListener {

                val intent =
                    Intent(
                        this,
                        VehicleInfo::class.java
                    )

                intent.putExtra(
                    "vehicleNumber",
                    vehicleNumber
                )

                startActivity(intent)
            }


            row.addView(srText)

            row.addView(vehicleText)

            row.addView(statusText)

            tableVehicleReport.addView(row)
        }
    }
    private fun updateSelectedCard(
        selected: TextView
    ) {

        val cards = listOf(
            txtVehicleCount,
            txtRepoMarked,
            txtParked,
            txtReleased
        )

        cards.forEach { card ->

            card.background =
                getDrawable(
                    R.drawable.yard_summary_card
                )

            card.setTextColor(
                Color.WHITE
            )
        }

        selected.background =
            getDrawable(
                R.drawable.yard_summary_card_selected
            )

        selected.setTextColor(
            Color.BLACK
        )
    }
    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}