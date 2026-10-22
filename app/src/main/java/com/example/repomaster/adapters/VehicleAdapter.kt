
package com.example.repomaster.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.activities.UserVehicleDetails
import com.example.repomaster.activities.VehicleDetailsActivity
import com.example.repomaster.models.Vehicle
import com.example.repomaster.utils.SessionManager
import com.example.repomaster.viewmodel.HomeViewModel

class VehicleAdapter(
    private var vehicleList: List<Vehicle>,
    private val homeViewModel: HomeViewModel,
    private val onSelectionChanged: (selectedCount: Int, totalCount: Int) -> Unit,
    private val onSelectionModeChanged: (selectionMode: Boolean) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    private val selectedVehicles = mutableSetOf<String>()
    private var selectionMode = false

    /**
     * Uses the server's composite record identity first.
     * Fallback identifiers are used only when the composite key is incomplete.
     */
    private fun getVehicleSelectionKey(vehicle: Vehicle): String {
        val repoYear = vehicle.id?.repoYear?.trim().orEmpty()
        val repoMonth = vehicle.id?.repoMonth?.trim().orEmpty()
        val loanNumber = vehicle.id?.loanNumber?.trim().orEmpty()

        if (
            repoYear.isNotEmpty() &&
            repoMonth.isNotEmpty() &&
            loanNumber.isNotEmpty()
        ) {
            return "RECORD:$repoYear:$repoMonth:$loanNumber"
        }

        val vehicleNumber = vehicle.vehicleNumber?.trim().orEmpty()
        if (vehicleNumber.isNotEmpty()) return "VEHICLE:$vehicleNumber"

        val chassisNumber = vehicle.chassisNumber?.trim().orEmpty()
        if (chassisNumber.isNotEmpty()) return "CHASSIS:$chassisNumber"

        val engineNumber = vehicle.engineNumber?.trim().orEmpty()
        if (engineNumber.isNotEmpty()) return "ENGINE:$engineNumber"

        return ""
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkVehicle: CheckBox = itemView.findViewById(R.id.checkVehicle)
        val txtVehicleNo: TextView = itemView.findViewById(R.id.txtVehicleNumber)
        val txtEngNum: TextView = itemView.findViewById(R.id.txtEngine)
        val txtChassNum: TextView = itemView.findViewById(R.id.txtChassis)
        val btnView: ImageButton = itemView.findViewById(R.id.btnView)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val checkboxvisibility: LinearLayout =
            itemView.findViewById(R.id.checkboxvisibility)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun getItemCount(): Int = vehicleList.size

    fun updateList(newList: List<Vehicle>) {
        vehicleList = newList

        val visibleKeys = newList
            .map { getVehicleSelectionKey(it) }
            .filter { it.isNotBlank() }
            .toSet()

        selectedVehicles.retainAll(visibleKeys)

        if (selectedVehicles.isEmpty() && selectionMode) {
            exitSelectionMode()
        } else {
            notifyDataSetChanged()
            onSelectionChanged(selectedVehicles.size, vehicleList.size)
        }
    }

    /**
     * Kept for compatibility with code that needs vehicle numbers.
     * Do not use this for bulk deletion because vehicle numbers may be blank
     * or duplicated.
     */
    fun getSelectedVehicles(): List<String> {
        return getSelectedVehicleRecords()
            .mapNotNull { it.vehicleNumber?.trim()?.takeIf(String::isNotEmpty) }
            .distinct()
    }

    fun getSelectedVehicleRecords(): List<Vehicle> {
        return vehicleList.filter { vehicle ->
            val key = getVehicleSelectionKey(vehicle)
            key.isNotBlank() && key in selectedVehicles
        }
    }

    fun selectAll() {
        if (!selectionMode) return

        selectedVehicles.clear()

        vehicleList.forEach { vehicle ->
            val key = getVehicleSelectionKey(vehicle)
            if (key.isNotBlank()) {
                selectedVehicles.add(key)
            }
        }

        notifyDataSetChanged()
        onSelectionChanged(selectedVehicles.size, vehicleList.size)
    }

    fun deselectAll() {
        clearSelection()
    }

    fun clearSelection() {
        if (selectedVehicles.isEmpty() && !selectionMode) return
        selectedVehicles.clear()
        exitSelectionMode()
    }

    private fun enterSelectionMode(selectionKey: String) {
        if (selectionKey.isBlank()) return

        if (!selectionMode) {
            selectionMode = true
            onSelectionModeChanged(true)
        }

        selectedVehicles.add(selectionKey)
        notifyDataSetChanged()
        onSelectionChanged(selectedVehicles.size, vehicleList.size)
    }

    private fun toggleSelection(selectionKey: String) {
        if (selectionKey.isBlank()) return

        if (!selectionMode) {
            enterSelectionMode(selectionKey)
            return
        }

        if (selectionKey in selectedVehicles) {
            selectedVehicles.remove(selectionKey)
        } else {
            selectedVehicles.add(selectionKey)
        }

        if (selectedVehicles.isEmpty()) {
            exitSelectionMode()
        } else {
            notifyDataSetChanged()
            onSelectionChanged(selectedVehicles.size, vehicleList.size)
        }
    }

    private fun exitSelectionMode() {
        selectedVehicles.clear()
        selectionMode = false
        notifyDataSetChanged()
        onSelectionModeChanged(false)
        onSelectionChanged(0, vehicleList.size)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicleList[position]
        val vehicleNumber = vehicle.vehicleNumber.orEmpty()
        val selectionKey = getVehicleSelectionKey(vehicle)

        holder.txtVehicleNo.text = vehicleNumber
        holder.txtEngNum.text = vehicle.engineNumber.orEmpty()
        holder.txtChassNum.text = vehicle.chassisNumber.orEmpty()

        val sessionManager = SessionManager(holder.itemView.context)
        val isAdmin = sessionManager.getRole().equals("ADMIN", ignoreCase = true)

        // Checkbox visibility and state
        if (isAdmin && selectionMode) {
            holder.checkboxvisibility.visibility = View.VISIBLE
            holder.checkVehicle.visibility = View.VISIBLE
            holder.checkVehicle.setOnCheckedChangeListener(null)

            holder.checkVehicle.isChecked =
                selectionKey.isNotBlank() && selectionKey in selectedVehicles

            holder.checkVehicle.isEnabled = selectionKey.isNotBlank()

            holder.checkVehicle.setOnCheckedChangeListener { _, checked ->
                if (selectionKey.isBlank()) {
                    holder.checkVehicle.setOnCheckedChangeListener(null)
                    holder.checkVehicle.isChecked = false
                    return@setOnCheckedChangeListener
                }

                if (checked) {
                    selectedVehicles.add(selectionKey)
                } else {
                    selectedVehicles.remove(selectionKey)
                }

                if (selectedVehicles.isEmpty()) {
                    exitSelectionMode()
                } else {
                    notifyDataSetChanged()
                    onSelectionChanged(selectedVehicles.size, vehicleList.size)
                }
            }
        } else {
            holder.checkVehicle.setOnCheckedChangeListener(null)
            holder.checkVehicle.isChecked = false
            holder.checkVehicle.isEnabled = true
            holder.checkVehicle.visibility = View.GONE
            holder.checkboxvisibility.visibility = View.GONE
        }

        // Long press selection
        if (isAdmin) {
            holder.txtVehicleNo.setOnLongClickListener {
                toggleSelection(selectionKey)
                true
            }

            holder.itemView.setOnLongClickListener {
                toggleSelection(selectionKey)
                true
            }
        } else {
            holder.txtVehicleNo.setOnLongClickListener(null)
            holder.itemView.setOnLongClickListener(null)
        }

        // Row click
        holder.itemView.setOnClickListener {
            if (selectionMode) {
                toggleSelection(selectionKey)
                return@setOnClickListener
            }

            val intent = if (isAdmin) {
                Intent(holder.itemView.context, VehicleDetailsActivity::class.java)
            } else {
                Intent(holder.itemView.context, UserVehicleDetails::class.java)
            }

            intent.putExtra("vehicleNumber", vehicleNumber)
            intent.putExtra("loanNumber", vehicle.id?.loanNumber.orEmpty())
            holder.itemView.context.startActivity(intent)
        }

        // View button
        holder.btnView.setOnClickListener {
            if (selectionMode) return@setOnClickListener

            val intent = if (isAdmin) {
                Intent(holder.itemView.context, VehicleDetailsActivity::class.java)
            } else {
                Intent(holder.itemView.context, UserVehicleDetails::class.java)
            }

            intent.putExtra("vehicleNumber", vehicleNumber)
            intent.putExtra("loanNumber", vehicle.id?.loanNumber.orEmpty())
            holder.itemView.context.startActivity(intent)
        }

        // Individual delete button
        if (isAdmin && !selectionMode) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete Vehicle")
                    .setMessage(
                        "Are you sure you want to delete " +
                                vehicleNumber.ifBlank { "(vehicle number blank)" } +
                                "?"
                    )
                    .setPositiveButton("Delete") { _, _ ->
                        homeViewModel.deleteVehicle(vehicleNumber)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        } else {
            holder.btnDelete.setOnClickListener(null)
            holder.btnDelete.visibility = View.GONE
        }
    }
}
