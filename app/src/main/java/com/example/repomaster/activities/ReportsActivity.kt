package com.example.repomaster.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.repomaster.R
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.example.repomaster.utils.PdfReportGenerator
import android.widget.*
import android.content.Intent
class ReportsActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var cardRepoStatus: MaterialCardView

    private lateinit var cardFinance: MaterialCardView
    private lateinit var cardExecutive: MaterialCardView
    private lateinit var cardMonthly: MaterialCardView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.white))
        supportActionBar?.title = "Reports"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        cardRepoStatus = findViewById(R.id.cardViewVehicle)
        cardFinance = findViewById(R.id.cardSearchHistory)
        cardExecutive = findViewById(R.id.cardBulkUpload)
        cardMonthly = findViewById(R.id.cardReports)

        val agencyId = SessionManager(this).getAgencyId()

        userViewModel.getReportSummary(agencyId).observe(this) { response ->

            if (response.isSuccessful && response.body() != null) {

                val report = response.body()!!

                cardRepoStatus.setOnClickListener {

                    showDialog(
                        "Repo Status",
                        """
Total Vehicles:${report.totalVehicles}

Open List : ${report.openlist}

Contacted : ${report.contacted}

Repo Mark : ${report.repoMark}

Parked : ${report.parked}

Released : ${report.released}
                        """.trimIndent()
                    )

                }


                cardFinance.setOnClickListener {

                    startActivity(
                        Intent(this, ReportListActivity::class.java)
                            .putExtra("TYPE","FINANCE")
                    )

                }

                cardExecutive.setOnClickListener {

                    startActivity(
                        Intent(this, UserActivityReportActivity::class.java)

                    )

                }

                cardMonthly.setOnClickListener {

                    startActivity(
                        Intent(this, ReportListActivity::class.java)
                            .putExtra("TYPE","MONTHLY")
                    )

                }

            }

        }

    }

    private fun showDialog(title: String, message: String) {

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }



        }

