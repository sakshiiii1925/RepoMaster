
package com.example.repomaster.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.adapters.YardAdapter
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.YardViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.example.repomaster.models.Yard
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
class YardManagement : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var yardAdapter: YardAdapter
    private lateinit var yardViewModel: YardViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var fabAddYard: FloatingActionButton
    private lateinit var etSearchYard: TextInputEditText

    private var allYards: List<Yard> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_yard_management)

        // =========================
        // Toolbar
        // =========================

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(
            getColor(R.color.white)
        )
        supportActionBar?.title = "Yard Management"

        // =========================
        // RecyclerView
        // =========================
        etSearchYard = findViewById(R.id.etSearchYard)
        recyclerView = findViewById(R.id.recyclerYards)
        fabAddYard = findViewById(R.id.fabAddYard)
        fabAddYard.setOnClickListener {

            Toast.makeText(
                this,
                "Add Yard",
                Toast.LENGTH_SHORT
            ).show()
            val intent= Intent(this, AddYard::class.java)
            startActivity(intent)
        }
        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.setHasFixedSize(true)

        // =========================
        // Session
        // =========================

        sessionManager = SessionManager(this)

        // =========================
        // ViewModel
        // =========================

        yardViewModel =
            ViewModelProvider(this)[YardViewModel::class.java]
        yardViewModel.deleteYardResponse
            .observe(this) { response ->

                if (response.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Yard deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadYards()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to delete yard: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        // =========================
        // Adapter
        // =========================

        yardAdapter = YardAdapter(
            emptyList(),

                onDeleteClick = { yard ->

                    showDeleteConfirmation(
                        yard.id,
                        yard.yardName
                    )
                }

        )

        recyclerView.adapter = yardAdapter

        // =========================
        // Observe Yards
        // =========================

        yardViewModel.yards.observe(this) { response ->

            if (response.isSuccessful) {

                val yards = response.body() ?: emptyList()

                // IMPORTANT: Save complete list for searching
                allYards = yards

                // Show complete list initially
                yardAdapter.updateList(allYards)

                if (yards.isEmpty()) {

                    Toast.makeText(
                        this,
                        "No yards found",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                Toast.makeText(
                    this,
                    "Failed to load yards",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        etSearchYard.addTextChangedListener(object : TextWatcher {

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
                    s.toString().trim()

                if (searchText.isEmpty()) {

                    yardAdapter.updateList(allYards)

                } else {

                    val filteredList =
                        allYards.filter { yard ->

                            yard.yardName.contains(
                                searchText,
                                ignoreCase = true
                            ) ||

                                    yard.yardAddress?.contains(
                                        searchText,
                                        ignoreCase = true
                                    ) == true ||

                                    yard.yardManagerName?.contains(
                                        searchText,
                                        ignoreCase = true
                                    ) == true ||

                                    yard.yardContactNo?.contains(
                                        searchText,
                                        ignoreCase = true
                                    ) == true
                        }

                    yardAdapter.updateList(filteredList)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // =========================
        // Load Yards
        // =========================

        loadYards()
    }

    private fun loadYards() {

        val agencyId =
            sessionManager.getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // getYards() only triggers the API call.
        // The result is received through yards LiveData.

        yardViewModel.getYards(agencyId)
    }
    //delete Yard
    private fun showDeleteConfirmation(
        yardId: Long?,
        yardName: String
    ) {

        if (yardId == null) {

            Toast.makeText(
                this,
                "Invalid yard ID",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Yard")
            .setMessage(
                "Are you sure you want to delete \"$yardName\"?"
            )
            .setPositiveButton("Delete") { _, _ ->

                deleteYard(yardId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    //delete yard
    private fun deleteYard(yardId: Long) {

        val agencyId =
            sessionManager.getAgencyId()

        if (agencyId.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "Agency ID not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        yardViewModel.deleteYard(
            yardId,
            agencyId
        )
    }
    override fun onResume() {
        super.onResume()

        if (::yardViewModel.isInitialized) {
            loadYards()
        }
    }
    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}

