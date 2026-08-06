package com.example.repomaster.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapter.ReportAdapter
import com.example.repomaster.model.ReportRow
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import android.widget.*
import com.example.repomaster.utils.PdfReportGenerator
import android.view.View
import com.example.repomaster.utils.FileDownloader
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.Intent
import com.example.repomaster.adapters.OnReportClickListener
class ReportListActivity : AppCompatActivity(), OnReportClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var btnDownloadPdf: MaterialButton
    private lateinit var btnDownloadExcel: MaterialButton
    private lateinit var userViewModel: UserViewModel
    private lateinit var header1: TextView
    private lateinit var header2: TextView
    private lateinit var header3: TextView
    private lateinit var header4: TextView
    private lateinit var header5: TextView
    private lateinit var header6: TextView
    private lateinit var spMonth: AutoCompleteTextView
    private lateinit var spYear: AutoCompleteTextView
    private lateinit var btnSearch: MaterialButton
    private lateinit var btnSearch2: MaterialButton
    private lateinit var txtInfo: TextView
    private var selectedYear = ""
    private var selectedMonth = ""
    private lateinit var spFinance: AutoCompleteTextView
    private lateinit var spBranch: AutoCompleteTextView

    private var selectedFinance: String? = null
    private var selectedBranch: String? = null
    private val reportRows = ArrayList<ReportRow>()
    private var reportType = ""
private lateinit var layout1: LinearLayout
    private lateinit var layout2: LinearLayout
    private lateinit var agencyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_list)
        //initialize userview Model
        agencyId = SessionManager(this).getAgencyId()

        reportType = intent.getStringExtra("TYPE") ?: ""

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

//load agencyName

        //toolbar
        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
//initializaton
        recyclerView = findViewById(R.id.rvUserActivity)
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)
        btnDownloadExcel = findViewById(R.id.btnDownloadExcel)

        header1=findViewById(R.id.header1)
        header2=findViewById(R.id.header2)
        header3=findViewById(R.id.header3)
        header4=findViewById(R.id.header4)
        header5=findViewById(R.id.header5)
        header6=findViewById(R.id.header6)
        layout1=findViewById(R.id.layout1)
        layout2=findViewById(R.id.layout2)
        spMonth = findViewById(R.id.spMonth)
        spYear = findViewById(R.id.spYear)
        btnSearch = findViewById(R.id.btnSearch)
        spFinance = findViewById(R.id.spfinance)
        spBranch = findViewById(R.id.spbranch)
        btnSearch2=findViewById(R.id.btnSearch1)
        txtInfo = findViewById(R.id.txtInfo)
        //adpater for month report
        val months = arrayOf(
            "January","February","March","April",
            "May","June","July","August",
            "September","October","November","December"
        )

        val adapterMonth = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            months
        )

        spMonth.setAdapter(adapterMonth)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val years = ArrayList<String>()

        for (i in currentYear downTo currentYear - 10) {
            years.add(i.toString())
        }

        spYear.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                years
            )
        )
        spMonth.setOnItemClickListener { _, _, position, _ ->
            selectedMonth = (position + 1).toString()
        }

        spYear.setOnItemClickListener { _, _, position, _ ->
            selectedYear = years[position]
        }
        btnSearch.setOnClickListener {

            if (selectedMonth.isEmpty()) {
                Toast.makeText(this, "Select Month", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedYear.isEmpty()) {
                Toast.makeText(this, "Select Year", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Year=$selectedYear Month=$selectedMonth",
                Toast.LENGTH_LONG
            ).show()
            txtInfo.visibility = View.GONE
            loadMonthlyReport(
                agencyId,
                selectedYear,
                selectedMonth
            )
        }
        //financereport
        spFinance.setOnItemClickListener { _, _, position, _ ->

            selectedFinance =
                spFinance.adapter.getItem(position).toString()

            userViewModel.getbranchList(
                agencyId,
                selectedFinance!!
            ).observe(this) { response ->

                if (response.isSuccessful) {

                    val branches = response.body() ?: emptyList()

                    spBranch.setAdapter(
                        ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            branches
                        )
                    )
                    spBranch.setOnItemClickListener { _, _, position, _ ->

                        selectedBranch =
                            spBranch.adapter.getItem(position).toString()
                    }
                }
            }
        }
        btnSearch2.setOnClickListener {

            if (selectedFinance == null) {
                Toast.makeText(this, "Select Finance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedBranch == null) {
                Toast.makeText(this, "Select Branch", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            txtInfo.visibility = View.GONE
            loadFinanceReport(
                agencyId,
                selectedFinance,
                selectedBranch
            )
        }
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(
            reportRows,
            reportType,
            this
        )

        recyclerView.adapter = adapter

        when(intent.getStringExtra("TYPE")){



            "FINANCE" -> {
                loadfinanceList()

                layout2.visibility = View.VISIBLE

                txtInfo.visibility = View.VISIBLE
                txtInfo.text = "Select Finance and Branch, then tap Search to view the report."
                loadFinanceReport(
                    agencyId,
                    null,
                    null
                )
            }



            "MONTHLY" -> {
                    supportActionBar?.title = "Monthly Report"

                    reportRows.clear()
                    adapter.notifyDataSetChanged()
                layout2.visibility= View.GONE
                layout1.visibility = View.VISIBLE

                txtInfo.visibility = View.VISIBLE
                txtInfo.text = "Select Month and Year, then tap Search to view the report."
                    spMonth.visibility = View.VISIBLE
                    spYear.visibility = View.VISIBLE
                    btnSearch.visibility = View.VISIBLE

            }
        }
        btnDownloadPdf.setOnClickListener {

            when (reportType) {


                "FINANCE" -> downloadFinancePdf()



                "MONTHLY" -> downloadMonthlyPdf()

            }

        }
        btnDownloadExcel.setOnClickListener {

            when (reportType) {



                "FINANCE" -> downloadFinanceExcel()


                "MONTHLY" -> downloadMonthlyExcel()

            }

        }

    }

    //Finance  Report
    private fun loadFinanceReport(agencyId:String,finance: String?,branch: String?){

        supportActionBar?.title="Finance Report"

        header1.text = "Finance"
        spMonth.visibility = View.GONE
        layout1.visibility= View.GONE
        spMonth.visibility = View.GONE
        spYear.visibility = View.GONE
        btnSearch.visibility = View.GONE
        header2.text = "Branch"
        header6.text = "Released"
        header3.text="Vehicles"
        header4.text="Repo Mark"
        header5.text="Parked"
        userViewModel.getfinanceReport(
            agencyId,
            finance,
            branch
        ).observe(this) { response ->
            if(response.isSuccessful){

                reportRows.clear()

                response.body()?.forEach {

                    reportRows.add(
                        ReportRow(
                            listOf(
                                it.finance,
                                it.branch,
                                it.totalVehicles.toString(),
                                it.repoMarkedCount.toString(),
                                it.parkedCount.toString(),
                                it.releasedCount.toString()
                            )
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
        }
    }


    //monthly report
    private fun loadMonthlyReport(
        agencyId: String,
        year: String,
        month: String
    ) {
        header1.text = "Repo Year"

        header2.text = "Repo Month"
        header6.text = "Released"
        header3.text="Vehicles"
        header4.text="Repo Mark"
        header5.text="Parked"

        userViewModel.getMonthlyReport(
            agencyId,
            year,
            month
        ).observe(this) { response ->

            if (response.isSuccessful) {

                val list = response.body() ?: emptyList()

                reportRows.clear()

                if (list.isEmpty()) {

                    adapter.notifyDataSetChanged()

                    if (list.isEmpty()) {

                        reportRows.clear()
                        adapter.notifyDataSetChanged()

                        showNoRecordDialog(
                            spMonth.text.toString(),
                            selectedYear
                        )

                        return@observe
                    }

                    return@observe
                }

                list.forEach {

                    reportRows.add(
                        ReportRow(
                            listOf(
                                it.repoYear,
                                it.repoMonth,
                                it.totalVehicles.toString(),
                                it.repoMarkedCount.toString(),
                                it.parkedCount.toString(),
                                it.releasedCount.toString()
                            )
                        )
                    )
                }

                adapter.notifyDataSetChanged()

            } else {

                Toast.makeText(
                    this,
                    "Failed to load report",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    //FianacePdf
    private fun downloadFinancePdf() {

        userViewModel.getfinanceReport(agencyId,selectedFinance,selectedBranch).observe(this) { response ->

            if (response.isSuccessful) {

                val reports = response.body() ?: emptyList()

                val rows = reports.map {

                    listOf(
                        it.finance,
                        it.branch,
                        it.totalVehicles.toString(),
                        it.repoMarkedCount.toString(),
                        it.parkedCount.toString(),
                        it.releasedCount.toString()
                    )

                }

                PdfReportGenerator(this).generateReport(

                    title = "Finance Report",

                    agencyId = agencyId,
                    headers = listOf(
                        "Finance",
                        "Branch",
                        "Vehicles",
                        "Repo Mark",
                        "Parked",
                        "Released"
                    ),

                    rows = rows,

                    fileName = "Finance_Report.pdf"

                )

                Toast.makeText(
                    this,
                    "PDF saved successfully",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }
    //ExecutivePdf

    //MonthlyReportPdf
    private fun downloadMonthlyPdf() {

        userViewModel.getMonthlyReport(
            agencyId,
            selectedYear,
            selectedMonth
        ).observe(this) { response ->

            if (response.isSuccessful) {

                val reports = response.body() ?: emptyList()

                val rows = reports.map {
                    listOf(
                        it.repoYear,
                        it.repoMonth,
                        it.totalVehicles.toString(),
                        it.repoMarkedCount.toString(),
                        it.parkedCount.toString(),
                        it.releasedCount.toString()
                    )
                }

                PdfReportGenerator(this).generateReport(
                    title = "Monthly Report",
                    agencyId = agencyId,

                    headers = listOf(
                        "Year",
                        "Month",
                        "Vehicles",
                        "Repo Mark",
                        "Parked",
                        "Released"
                    ),
                    rows = rows,
                    fileName = "Monthly_Report.pdf"
                )

                Toast.makeText(
                    this,
                    "PDF saved successfully",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }
   //finance excel

    private fun downloadFinanceExcel() {

        userViewModel.downloadFinanceExcel(
            agencyId,
            selectedFinance,
            selectedBranch
        )
            .observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    val uri = FileDownloader(this).saveExcel(
                        response.body()!!,
                        "Finance_Report.xlsx"
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
    //ExecutiveExcel


    //Monthlyexcel
    private fun downloadMonthlyExcel() {

        userViewModel.downloadMonthlyExcel(
            agencyId,
            selectedYear,
            selectedMonth
        )
            .observe(this) { response ->

                if (response.isSuccessful && response.body() != null) {

                    val uri = FileDownloader(this).saveExcel(
                        response.body()!!,
                        "Monthly_Report.xlsx"
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



//dialogue box function
    private fun showNoRecordDialog(month: String, year: String) {

        MaterialAlertDialogBuilder(this)
            .setTitle("No Records Found")
            .setMessage("There are no records available for $month $year.")
            .setIcon(R.drawable.outline_info_24) // or android.R.drawable.ic_dialog_info
            .setPositiveButton("OK", null)
            .setCancelable(false)
            .show()
    }
    //loadFinancelist
    private fun loadfinanceList() {

        userViewModel.getfinanceList(agencyId)
            .observe(this) { response ->

                if (response.isSuccessful) {

                    val list = response.body() ?: emptyList()

                    spFinance.setAdapter(
                        ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            list
                        )
                    )
                }
            }
    }
    //financereportclick
    override fun onFinanceReportClick(
        finance: String,
        branch: String,
        status: String
    ) {

        val intent = Intent(
            this,
            VehicleReportActivity::class.java
        )

        intent.putExtra("FINANCE", finance)
        intent.putExtra("BRANCH", branch)
        intent.putExtra("STATUS", status)

        startActivity(intent)
    }
    override fun onMonthlyReportClick(
        year: String,
        month: String,
        status: String
    ) {

        val intent = Intent(
            this,
            VehicleReportActivity::class.java
        )

        intent.putExtra("YEAR", year)
        intent.putExtra("MONTH", month)
        intent.putExtra("STATUS", status)

        startActivity(intent)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}