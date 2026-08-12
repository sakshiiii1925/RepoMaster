package com.example.repomaster.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
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
import com.example.repomaster.utils.FileDownloader
import com.example.repomaster.utils.PdfReportGenerator
class YardReportActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var autoYard: MaterialAutoCompleteTextView

    private lateinit var txtSelectedYard: TextView
    private lateinit var txtVehicleCount: TextView

    private lateinit var tableVehicleReport: TableLayout

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
            agencyId
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

        if (selectedVehicles.isEmpty()) {

            Toast.makeText(
                this,
                "No vehicles assigned to this yard",
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

        val rows = selectedVehicles.mapIndexed { index, vehicleNumber ->

            listOf(
                (index + 1).toString(),
                vehicleNumber
            )
        }

        val fileName =
            "Yard_Report_${yard.yardName.replace(" ", "_")}.pdf"

        PdfReportGenerator(this).generateReport(

            title = "Yard Vehicle Report - ${yard.yardName}",

            agencyId = agencyId,

            headers = listOf(
                "Sr. No.",
                "Vehicle Number"
            ),

            rows = rows,

            fileName = fileName
        )

        Toast.makeText(
            this,
            "Yard PDF downloaded successfully",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun initializeViews() {

        toolbar = findViewById(R.id.toolbar)

        autoYard = findViewById(R.id.autoYard)

        txtSelectedYard =
            findViewById(R.id.txtSelectedYard)

        txtVehicleCount =
            findViewById(R.id.txtVehicleCount)

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

            progressYardReport.visibility =
                View.GONE

            if (response.isSuccessful) {

                val vehicles =
                    response.body() ?: emptyList()

                selectedVehicles = vehicles.mapNotNull {
                    it.vehicleNumber
                }

                displayVehicles(selectedVehicles)

            } else {

                Toast.makeText(
                    this,
                    "Failed to load vehicles",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        yardViewModel.yardVehiclesError.observe(this) { error ->

            progressYardReport.visibility =
                View.GONE

            Toast.makeText(
                this,
                error,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayVehicles(
        vehicleNumbers: List<String?>
    ) {

        // Keep header row
        while (tableVehicleReport.childCount > 1) {
            tableVehicleReport.removeViewAt(1)
        }

        txtVehicleCount.text =
            "Vehicles: ${vehicleNumbers.size}"

        vehicleNumbers.forEachIndexed { index, vehicleNumber ->

            if (vehicleNumber.isNullOrBlank()) {
                return@forEachIndexed
            }

            val row = TableRow(this)

            // Sr No
            val srText = TextView(this)

            srText.text = (index + 1).toString()

            srText.setPadding(
                10,
                10,
                10,
                10
            )

            srText.gravity = android.view.Gravity.CENTER

            srText.setTextColor(Color.WHITE)

            srText.background =
                getDrawable(R.drawable.table_cell_border)

            // Vehicle Number
            val vehicleText = TextView(this)

            vehicleText.text = vehicleNumber

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
                getDrawable(R.drawable.table_cell_border)

            // Click vehicle
            vehicleText.setOnClickListener {

                val intent =
                    android.content.Intent(
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

            tableVehicleReport.addView(row)
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}