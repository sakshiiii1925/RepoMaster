package com.example.repomaster.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.models.UserReport
import com.example.repomaster.utils.FileDownloader
import com.example.repomaster.utils.PdfReportGenerator
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton

class UserReportActivity : AppCompatActivity() {

    private lateinit var txtTotalVehicles: TextView
    private lateinit var txtRepoMarked: TextView
    private lateinit var txtParked: TextView
    private lateinit var txtReleased: TextView

    private lateinit var btnDownloadPdf: MaterialButton
    private lateinit var btnDownloadexcel: MaterialButton

    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var toolbar:Toolbar

    private var currentReport: UserReport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_report2)

        // Initialize TextViews
        txtTotalVehicles = findViewById(R.id.txtTotalVehicles)
        txtRepoMarked = findViewById(R.id.txtRepoMarked)
        txtParked = findViewById(R.id.txtParked)
        txtReleased = findViewById(R.id.txtReleased)
        toolbar=findViewById(R.id.toolbar)
//toolbar
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title = "Agent Report"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
        // PDF button
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)
        //excelButton
        btnDownloadexcel=findViewById(R.id.btnDownloadExcel)

        // Session
        sessionManager = SessionManager(this)

        // ViewModel
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Observe report
        userViewModel.userReport.observe(this) { report ->

            currentReport = report

            displayReport(report)
        }

        // Get logged-in user's email
        val userEmail = sessionManager.getUserEmail()

        if (!userEmail.isNullOrEmpty()) {

            userViewModel.getUserReport(userEmail)

        } else {

            Toast.makeText(
                this,
                "User email not found",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Download PDF
        btnDownloadPdf.setOnClickListener {

            downloadUserReportPdf()
        }
        //download Excel
        btnDownloadexcel.setOnClickListener {
            downloadUserActivityExcel()
        }

    }

    private fun displayReport(report: UserReport) {

        txtTotalVehicles.text = report.totalVehicles.toString()

        txtRepoMarked.text = report.repoMarked.toString()

        txtParked.text = report.parked.toString()

        txtReleased.text = report.released.toString()
    }

    private fun downloadUserReportPdf() {

        val report = currentReport

        if (report == null) {

            Toast.makeText(
                this,
                "Report data not available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val userEmail = sessionManager.getUserEmail() ?: ""
val agencyId=sessionManager.getAgencyId() ?:""
        val rows = listOf(
            listOf(
                report.totalVehicles.toString(),
                report.repoMarked.toString(),
                report.parked.toString(),
                report.released.toString()
            )
        )

        PdfReportGenerator(this).generateReport(

            title = "Agent Report",

            agencyId = agencyId,

            headers = listOf(
                "Total Vehicle",
                "Repo Marked",
                "Parked",
                "Released"
            ),

            rows = rows,

            fileName = "User_Report.pdf"
        )

        Toast.makeText(
            this,
            "PDF saved successfully",
            Toast.LENGTH_LONG
        ).show()
    }
    //excelReport
    private fun downloadUserActivityExcel() {

        val userEmail = sessionManager.getUserEmail()

        if (userEmail.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "User email not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        userViewModel.downloadUserReportExcel(userEmail)
            .observe(this) { responseBody ->

                if (responseBody != null) {

                    val uri = FileDownloader(this).saveExcel(
                        responseBody,
                        "Agent_Report.xlsx"
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

                } else {

                    Toast.makeText(
                        this,
                        "Excel download failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}