package com.example.repomaster.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.UserActivityAdapter
import com.example.repomaster.utils.FileDownloader
import com.example.repomaster.utils.PdfReportGenerator
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.HomeViewModel
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class UserActivityReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserActivityAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var homeViewModel: HomeViewModel



    private lateinit var btnDownloadPdf: MaterialButton
    private lateinit var btnDownloadExcel: MaterialButton

    private lateinit var agencyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_report)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.rvUserActivity)
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)
        btnDownloadExcel = findViewById(R.id.btnDownloadExcel)

        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title = "Executive Report"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserActivityAdapter(emptyList())
        recyclerView.adapter = adapter

        agencyId = SessionManager(this).getAgencyId()

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        loadReport()

        btnDownloadPdf.setOnClickListener {
            downloadUserActivityPdf()
        }

        btnDownloadExcel.setOnClickListener {
            downloadUserActivityExcel()
        }

    }

    private fun loadReport() {

        userViewModel.getUserActivityReport(agencyId)
            .observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    adapter.updateList(response.body()!!)

                } else {

                    Toast.makeText(
                        this,
                        "No report found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun downloadUserActivityPdf() {

        userViewModel.getUserActivityReport(agencyId)
            .observe(this) { response ->

                if (response.isSuccessful) {

                    val reports = response.body() ?: emptyList()

                    val rows = reports.map {

                        listOf(
                            it.userName,
                            it.userEmail,
                            it.totalSearches.toString(),
                            it.repoMarkedCount.toString(),
                            it.parkedCount.toString(),
                            it.releasedCount.toString(),
                            it.lastSearchTime
                        )
                    }

                    PdfReportGenerator(this).generateReport(

                        title = "Executive Report",

                        agencyId = agencyId,
                        headers = listOf(
                            "User",
                            "Email",
                            "Searches",
                            "Repo Mark",
                            "Parked",
                            "Released",
                            "Last Search"
                        ),

                        rows = rows,

                        fileName = "Executive_Report.pdf"
                    )

                    Toast.makeText(
                        this,
                        "PDF saved successfully",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun downloadUserActivityExcel() {

        userViewModel.downloadUserActivityExcel(agencyId)
            .observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    val uri = FileDownloader(this).saveExcel(
                        response.body()!!,
                        "Executive_Report.xlsx"
                    )

                    if (uri != null) {

                        Toast.makeText(
                            this,
                            "Excel saved in Downloads",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        Toast.makeText(
                            this,
                            "Download failed",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            }
    }


    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}