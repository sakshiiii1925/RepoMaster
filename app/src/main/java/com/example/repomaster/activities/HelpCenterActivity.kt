
package com.example.repomaster.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.repomaster.R

class HelpCenterActivity : AppCompatActivity() {

    private lateinit var layoutHelpItems: LinearLayout
    private lateinit var searchHelp: SearchView
    private lateinit var tvNoResults: TextView

    private val helpItems = listOf(
        HelpItem(
            "Login & Registration",
            "How to register, login and reset your password?",
            "account"
        ),
        HelpItem(
            "Vehicle Search",
            "Search vehicles using vehicle number, loan number or chassis number.",
            "search"
        ),
        HelpItem(
            "Vehicle Details",
            "View customer, vehicle, bank and loan information.",
            "vehicle"
        ),
        HelpItem(
            "Repo Status Update",
            "Understand Repo Mark, On the Way, Parked and Released statuses.",
            "status"
        ),
        HelpItem(
            "Vehicle Images",
            "Capture and upload vehicle and inventory images.",
            "images"
        ),
        HelpItem(
            "Excel Bulk Upload",
            "Download the Excel template and upload vehicle records.",
            "excel"
        ),
        HelpItem(
            "Invoices & Payments",
            "Create invoices and understand payment statuses.",
            "invoice"
        ),
        HelpItem(
            "Reports",
            "View recovery, parked, released and user reports.",
            "reports"
        ),
        HelpItem(
            "Offline Mode",
            "Save status updates offline and synchronize when internet returns.",
            "offline"
        ),
        HelpItem(
            "Notifications",
            "Understand admin notifications and account updates.",
            "notifications"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_center)

        layoutHelpItems = findViewById(R.id.layoutHelpItems)
        searchHelp = findViewById(R.id.searchHelp)
        tvNoResults = findViewById(R.id.tvNoResults)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        renderHelpItems(helpItems)

        searchHelp.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    filterHelp(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterHelp(newText)
                    return true
                }
            }
        )

        findViewById<View>(R.id.btnSupportEmail).setOnClickListener {
            openEmailSupport()
        }

        findViewById<View>(R.id.btnSupportWhatsApp).setOnClickListener {
            openWhatsAppSupport()
        }
    }

    private fun filterHelp(query: String?) {

        val filtered = helpItems.filter {
            it.title.contains(query.orEmpty(), ignoreCase = true) ||
                    it.description.contains(query.orEmpty(), ignoreCase = true)
        }

        renderHelpItems(filtered)

        tvNoResults.visibility =
            if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun renderHelpItems(items: List<HelpItem>) {

        layoutHelpItems.removeAllViews()

        items.forEach { item ->

            val view = LayoutInflater.from(this).inflate(
                R.layout.item_help_category,
                layoutHelpItems,
                false
            )

            val title = view.findViewById<TextView>(R.id.tvHelpTitle)
            val description =
                view.findViewById<TextView>(R.id.tvHelpDescription)
            val icon = view.findViewById<TextView>(R.id.tvHelpIcon)

            title.text = item.title
            description.text = item.description

            icon.text = when (item.category) {
                "account" -> "👤"
                "search" -> "🔍"
                "vehicle" -> "🚗"
                "status" -> "📋"
                "images" -> "📷"
                "excel" -> "📊"
                "invoice" -> "🧾"
                "reports" -> "📈"
                "offline" -> "📡"
                "notifications" -> "🔔"
                else -> "❓"
            }

            view.setOnClickListener {
                openHelpDetails(item)
            }

            layoutHelpItems.addView(view)
        }
    }

    private fun openHelpDetails(item: HelpItem) {

        val intent = Intent(this, HelpDetailActivity::class.java)

        intent.putExtra("title", item.title)
        intent.putExtra("description", item.description)
        intent.putExtra("category", item.category)

        startActivity(intent)
    }

    private fun openEmailSupport() {

        val supportEmail = "sakshipawarr19@gmail.com"

        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:$supportEmail")
        ).apply {

            putExtra(
                Intent.EXTRA_SUBJECT,
                "RepoMaster Help & Support"
            )

            putExtra(
                Intent.EXTRA_TEXT,
                """
                Hello RepoMaster Support Team,

                I need assistance with the RepoMaster application.

                My issue:

                Registered Email:

                Thank you.
                """.trimIndent()
            )
        }

        try {
            startActivity(emailIntent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No email application found.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openWhatsAppSupport() {

        val supportNumber = "919172851538"

        val message = """
            Hello RepoMaster Support Team,

            I need assistance with the RepoMaster application.

            My issue:

            Registered Email:
        """.trimIndent()

        val whatsappUrl =
            "https://wa.me/$supportNumber?text=${Uri.encode(message)}"

        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(whatsappUrl)
            )
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Unable to open WhatsApp.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

data class HelpItem(
    val title: String,
    val description: String,
    val category: String
)
