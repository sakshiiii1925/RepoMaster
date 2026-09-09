package com.example.repomaster.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.*
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
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.repomaster.models.UserActivityReport
import com.google.android.material.textfield.TextInputEditText
import com.example.repomaster.viewmodel.HomeViewModelFactory

class UserActivityReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserActivityAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var edtFromDate: TextInputEditText
    private lateinit var edtToDate: TextInputEditText
    private lateinit var autoUser: AutoCompleteTextView

    private lateinit var btnApplyFilter: MaterialButton
    private lateinit var btnClearFilter: MaterialButton

    private lateinit var txtTotalExecutives: TextView
    private lateinit var txtTotalSearches: TextView
    private lateinit var txtTotalRepoMarked: TextView
    private lateinit var txtTotalParked: TextView
    private lateinit var txtTotalReleased: TextView

    private var selectedUserEmail: String? = null

    private var currentReports =
        emptyList<UserActivityReport>()


    private lateinit var btnDownloadPdf: MaterialButton
    private lateinit var btnDownloadExcel: MaterialButton

    private lateinit var agencyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_report)
        val factory =
            HomeViewModelFactory(applicationContext)

        homeViewModel =
            ViewModelProvider(
                this,
                factory
            )[HomeViewModel::class.java]
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.rvUserActivity)
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)
        btnDownloadExcel = findViewById(R.id.btnDownloadExcel)
        edtFromDate =
            findViewById(R.id.edtFromDate)

        edtToDate =
            findViewById(R.id.edtToDate)

        autoUser =
            findViewById(R.id.autoUser)

        btnApplyFilter =
            findViewById(R.id.btnApplyFilter)

        btnClearFilter =
            findViewById(R.id.btnClearFilter)


        txtTotalExecutives =
            findViewById(R.id.txtTotalExecutives)

        txtTotalSearches =
            findViewById(R.id.txtTotalSearches)

        txtTotalRepoMarked =
            findViewById(R.id.txtTotalRepoMarked)

        txtTotalParked =
            findViewById(R.id.txtTotalParked)

        txtTotalReleased =
            findViewById(R.id.txtTotalReleased)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title = "Agent Report"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
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
        edtFromDate.setOnClickListener {
            showDatePicker(edtFromDate)
        }

        edtToDate.setOnClickListener {
            showDatePicker(edtToDate)
        }
        btnApplyFilter.setOnClickListener {

            loadReport()
        }
        btnClearFilter.setOnClickListener {

            edtFromDate.text?.clear()

            edtToDate.text?.clear()

            autoUser.setText(
                "All Users",
                false
            )

            selectedUserEmail = null

            loadReport()
        }
    }
    private fun setupUserDropdown(
        reports: List<UserActivityReport>
    ) {

        val users =
            mutableListOf("All Users")

        users.addAll(
            reports
                .map {
                    "${it.userName} - ${it.userEmail}"
                }
                .distinct()
        )


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                users
            )

        autoUser.setAdapter(adapter)


        autoUser.setOnItemClickListener {
                _, _, position, _ ->

            if (position == 0) {

                selectedUserEmail = null

            } else {

                val selected =
                    users[position]

                selectedUserEmail =
                    selected
                        .substringAfter(" - ")
            }
        }
    }
    private fun showDatePicker(
        editText: TextInputEditText
    ) {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->

                val selected = Calendar.getInstance()

                selected.set(
                    year,
                    month,
                    day
                )

                val formatter =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                editText.setText(
                    formatter.format(
                        selected.time
                    )
                )

            },

            calendar.get(
                Calendar.YEAR
            ),

            calendar.get(
                Calendar.MONTH
            ),

            calendar.get(
                Calendar.DAY_OF_MONTH
            )

        ).show()
    }
    private fun loadReport() {

        userViewModel
            .getUserActivityReport(
                agencyId = agencyId,
                fromDate =
                    edtFromDate.text
                        ?.toString()
                        ?.ifBlank { null },
                toDate =
                    edtToDate.text
                        ?.toString()
                        ?.ifBlank { null },
                userEmail =
                    selectedUserEmail
            )
            .observe(this) { response ->

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    currentReports =
                        response.body()!!

                    adapter.updateList(
                        currentReports
                    )

                    updateSummary(
                        currentReports
                    )

                    setupUserDropdown(
                        currentReports
                    )

                } else {

                    currentReports =
                        emptyList()

                    adapter.updateList(
                        emptyList()
                    )

                    updateSummary(
                        emptyList()
                    )

                    Toast.makeText(
                        this,
                        "No report found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun downloadUserActivityPdf() {

        val reports = currentReports

        val rows = reports.map {

            listOf(
                it.userName,
                it.userEmail,
                it.totalSearches.toString(),
                it.repoMarkedCount.toString(),
                it.parkedCount.toString(),
                it.releasedCount.toString(),
                it.lastSearchTime ?: ""
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

            fileName = "Agent_Report.pdf"
        )

        Toast.makeText(
            this,
            "PDF saved successfully",
            Toast.LENGTH_LONG
        ).show()
    }




    private fun downloadUserActivityExcel() {

        userViewModel.downloadUserActivityExcel(agencyId)
            .observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    val uri = FileDownloader(this).saveExcel(
                        response.body()!!,
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

                }

            }
    }
    private fun updateSummary(
        reports: List<UserActivityReport>
    ) {

        txtTotalExecutives.text =
            reports.size.toString()

        txtTotalSearches.text =
            reports.sumOf {
                it.totalSearches
            }.toString()

        txtTotalRepoMarked.text =
            reports.sumOf {
                it.repoMarkedCount
            }.toString()


        txtTotalParked.text =
            reports.sumOf {
                it.parkedCount
            }.toString()

        txtTotalReleased.text =
            reports.sumOf {
                it.releasedCount
            }.toString()
    }

    override fun onSupportNavigateUp(): Boolean {


        finish()


        return true

    }
}