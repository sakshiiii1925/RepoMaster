
package com.example.repomaster.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.repomaster.R

class HelpDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_detail)

        val title = intent.getStringExtra("title") ?: "Help"
        val category = intent.getStringExtra("category") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        findViewById<TextView>(R.id.tvDetailTitle).text = title
        findViewById<TextView>(R.id.tvDetailDescription).text =
            getInstructions(category, description)

        findViewById<TextView>(R.id.btnDetailBack).setOnClickListener {
            finish()
        }
    }

    private fun getInstructions(category: String, description: String): String {
        return when (category) {
            "account" -> """
                $description

                1. Open the RepoMaster application.
                2. Enter your registered email address.
                3. Enter your password.
                4. Tap LOGIN.

                New users can select Create Account.
                Use Forgot Password if you cannot access your account.
                
                Note: New user accounts may require admin approval.
                *Contact your administrator if you have an account access issue.*
            """.trimIndent()

            "search" -> """
                $description

                1. Open the dashboard.
                2. Enter a vehicle number, loan number or chassis number.
                3. Suggested vehicle seen
                4. Select the matching vehicle.
                5. When click on vehicle Number Automatically Open the vehicle details to view its information.

                Check the selected agency before searching.
            """.trimIndent()

            "vehicle" -> """
                $description

                1. Search for the required vehicle.
                2. Open its details.
                3. user can change status and assign yard
                4. Check customer, vehicle, loan, finance and yard information.
                5. Review its current recovery status.
            """.trimIndent()

            "status" -> """
                $description

                1. Search and open the vehicle.
                2. Select the appropriate recovery status.
                
                3. Save the update.
                4. when user click on repo init and Parked upload images file automatically open.
                5. when user don't have network then save status only status save using local database.when network come do check pending images menu on agent dashboard.
                6. Check whether the update was synchronized.

                Follow your agency's process before changing a status.
            """.trimIndent()

            "images" -> """
                $description

                1. when status change to repo init and Parked image upload file automatically open.
                2. Select the image option.
                3. Capture or select the required image.
                4. Upload and wait for confirmation.

                Make sure the images are clear and belong to the correct vehicle.
            """.trimIndent()

            "excel" -> """
                $description

                1. Open the Excel Bulk Upload section.
                2. Download the official RepoMaster template.
                3. Enter vehicle information in the correct columns.
                4. Save the Excel file.
                5. Select the file and upload it.
                6. Review the upload result.

                Do not rename or remove required template columns.
            """.trimIndent()

            "invoice" -> """
                $description

                1. Open the Invoice section.
                2. Search and select the vehicle.
                3. Verify customer and vehicle details.
                4. Enter charges and applicable GST.
                5. Enter payment information.
                6. add DPD charges in %.
                7. Save and review the invoice.

                Verify all amounts before saving.
            """.trimIndent()

            "reports" -> """
                $description

                1. Open the Reports section.
                2. Select the required report.
                3. Choose the available filters.
                4. Review the report data.
                5. Export if an export option is available.

                Reports depend on the data and permissions of your account.
            """.trimIndent()

            "offline" -> """
                $description

                1. Open the vehicle while using the application.
                2. Save the supported status update.
                3. If internet is unavailable, the update may be saved locally.
                4. Restore your internet connection.
                5. Allow synchronization to complete.

                Do not clear application data before pending updates synchronize.
            """.trimIndent()

            "notifications" -> """
                $description

                1. Open the notification section.
                2. notification section only for Admin not for Agent.
                3. Select a notification to review its details.
                4. Follow the requested action if necessary.
                5. admin can see Agent profile request and status change by Agent.
            """.trimIndent()

            else -> description
        }
    }
}
