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


    // =========================================================
    // SELECTED VEHICLES
    // =========================================================

    private val selectedVehicles =
        mutableSetOf<String>()


    // =========================================================
    // SELECTION MODE
    // =========================================================

    private var selectionMode = false


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    class VehicleViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val checkVehicle: CheckBox =
            itemView.findViewById(
                R.id.checkVehicle
            )

        val txtVehicleNo: TextView =
            itemView.findViewById(
                R.id.txtVehicleNumber
            )

        val txtEngNum: TextView =
            itemView.findViewById(
                R.id.txtEngine
            )

        val txtChassNum: TextView =
            itemView.findViewById(
                R.id.txtChassis
            )

        val btnView: ImageButton =
            itemView.findViewById(
                R.id.btnView
            )

        val btnDelete: ImageButton =
            itemView.findViewById(
                R.id.btnDelete
            )

        val checkboxvisibility: LinearLayout =
            itemView.findViewById(
                R.id.checkboxvisibility
            )
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_vehicle,
                parent,
                false
            )

        return VehicleViewHolder(view)
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    override fun getItemCount(): Int =
        vehicleList.size


    // =========================================================
    // UPDATE LIST
    // =========================================================

    fun updateList(
        newList: List<Vehicle>
    ) {

        vehicleList =
            newList

        val visibleVehicles =
            newList
                .mapNotNull {
                    it.vehicleNumber
                }
                .toSet()

        selectedVehicles.retainAll(
            visibleVehicles
        )

        /*
         * If filtering/searching removes
         * the selected items, check selection mode.
         */

        if (
            selectedVehicles.isEmpty() &&
            selectionMode
        ) {

            exitSelectionMode()

        } else {

            notifyDataSetChanged()

            onSelectionChanged(
                selectedVehicles.size,
                vehicleList.size
            )
        }
    }


    // =========================================================
    // GET SELECTED VEHICLES
    // =========================================================

    fun getSelectedVehicles(): List<String> {

        return selectedVehicles.toList()
    }


    // =========================================================
    // SELECT ALL
    // =========================================================

    fun selectAll() {

        if (!selectionMode) {
            return
        }

        selectedVehicles.clear()

        vehicleList.forEach { vehicle ->

            vehicle.vehicleNumber?.let {

                selectedVehicles.add(it)
            }
        }

        notifyDataSetChanged()

        onSelectionChanged(
            selectedVehicles.size,
            vehicleList.size
        )
    }


    // =========================================================
    // DESELECT ALL
    // =========================================================

    fun deselectAll() {

        selectedVehicles.clear()

        notifyDataSetChanged()

        exitSelectionMode()
    }


    // =========================================================
    // CLEAR SELECTION
    // =========================================================

    fun clearSelection() {

        if (
            selectedVehicles.isEmpty() &&
            !selectionMode
        ) {
            return
        }

        selectedVehicles.clear()

        notifyDataSetChanged()

        exitSelectionMode()
    }


    // =========================================================
    // ENTER SELECTION MODE
    // =========================================================

    private fun enterSelectionMode(
        vehicleNumber: String
    ) {

        if (!selectionMode) {

            selectionMode = true

            onSelectionModeChanged(true)
        }

        selectedVehicles.add(
            vehicleNumber
        )

        notifyDataSetChanged()

        onSelectionChanged(
            selectedVehicles.size,
            vehicleList.size
        )
    }


    // =========================================================
    // EXIT SELECTION MODE
    // =========================================================

    private fun exitSelectionMode() {

        selectedVehicles.clear()

        selectionMode = false

        notifyDataSetChanged()

        onSelectionModeChanged(false)

        onSelectionChanged(
            0,
            vehicleList.size
        )
    }


    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    override fun onBindViewHolder(
        holder: VehicleViewHolder,
        position: Int
    ) {

        val vehicle =
            vehicleList[position]

        val vehicleNumber =
            vehicle.vehicleNumber ?: ""


        // =====================================================
        // VEHICLE DATA
        // =====================================================

        holder.txtVehicleNo.text =
            vehicleNumber

        holder.txtEngNum.text =
            vehicle.engineNumber ?: ""

        holder.txtChassNum.text =
            vehicle.chassisNumber ?: ""


        // =====================================================
        // SESSION / ROLE
        // =====================================================

        val sessionManager =
            SessionManager(
                holder.itemView.context
            )

        val role =
            sessionManager.getRole()

        val isAdmin =
            role.equals(
                "ADMIN",
                ignoreCase = true
            )


        // =====================================================
        // CHECKBOX
        // ONLY SHOW DURING SELECTION MODE
        // =====================================================

        if (
            isAdmin &&
            selectionMode
        ) {

            holder.checkboxvisibility.visibility =
                View.VISIBLE

            holder.checkVehicle.visibility =
                View.VISIBLE

            holder.checkVehicle
                .setOnCheckedChangeListener(null)

            holder.checkVehicle.isChecked =
                selectedVehicles.contains(
                    vehicleNumber
                )

            holder.checkVehicle
                .setOnCheckedChangeListener {
                        _, checked ->

                    if (checked) {

                        selectedVehicles.add(
                            vehicleNumber
                        )

                    } else {

                        selectedVehicles.remove(
                            vehicleNumber
                        )
                    }

                    notifyDataSetChanged()

                    onSelectionChanged(
                        selectedVehicles.size,
                        vehicleList.size
                    )

                    /*
                     * If nothing is selected,
                     * automatically exit selection mode.
                     */

                    if (
                        selectedVehicles.isEmpty()
                    ) {

                        exitSelectionMode()
                    }
                }

        } else {

            holder.checkVehicle
                .setOnCheckedChangeListener(null)

            holder.checkVehicle.isChecked =
                false

            holder.checkVehicle.visibility =
                View.GONE

            holder.checkboxvisibility.visibility =
                View.GONE
        }


        // =====================================================
        // LONG PRESS
        // =====================================================

        if (isAdmin) {

            holder.txtVehicleNo
                .setOnLongClickListener {

                    if (!selectionMode) {

                        enterSelectionMode(
                            vehicleNumber
                        )

                    } else {

                        if (
                            selectedVehicles.contains(
                                vehicleNumber
                            )
                        ) {

                            selectedVehicles.remove(
                                vehicleNumber
                            )

                        } else {

                            selectedVehicles.add(
                                vehicleNumber
                            )
                        }

                        notifyDataSetChanged()

                        onSelectionChanged(
                            selectedVehicles.size,
                            vehicleList.size
                        )
                    }

                    true
                }


            /*
             * Long press anywhere on the row
             * except action buttons.
             */

            holder.itemView
                .setOnLongClickListener {

                    if (!selectionMode) {

                        enterSelectionMode(
                            vehicleNumber
                        )

                    } else {

                        if (
                            selectedVehicles.contains(
                                vehicleNumber
                            )
                        ) {

                            selectedVehicles.remove(
                                vehicleNumber
                            )

                        } else {

                            selectedVehicles.add(
                                vehicleNumber
                            )
                        }

                        notifyDataSetChanged()

                        onSelectionChanged(
                            selectedVehicles.size,
                            vehicleList.size
                        )
                    }

                    true
                }

        } else {

            holder.itemView
                .setOnLongClickListener(null)

            holder.txtVehicleNo
                .setOnLongClickListener(null)
        }


        // =====================================================
        // NORMAL ROW CLICK
        // =====================================================

        holder.itemView.setOnClickListener {

            if (selectionMode) {

                if (
                    selectedVehicles.contains(
                        vehicleNumber
                    )
                ) {

                    selectedVehicles.remove(
                        vehicleNumber
                    )

                } else {

                    selectedVehicles.add(
                        vehicleNumber
                    )
                }

                notifyDataSetChanged()

                onSelectionChanged(
                    selectedVehicles.size,
                    vehicleList.size
                )

                if (
                    selectedVehicles.isEmpty()
                ) {

                    exitSelectionMode()
                }

                return@setOnClickListener
            }


            // =================================================
            // NORMAL VIEW MODE
            // =================================================

            val intent =
                if (isAdmin) {

                    Intent(
                        holder.itemView.context,
                        VehicleDetailsActivity::class.java
                    )

                } else {

                    Intent(
                        holder.itemView.context,
                        UserVehicleDetails::class.java
                    )
                }


            intent.putExtra(
                "vehicleNumber",
                vehicleNumber
            )

            holder.itemView.context
                .startActivity(intent)
        }


        // =====================================================
        // VIEW BUTTON
        // =====================================================

        holder.btnView.setOnClickListener {

            if (selectionMode) {

                return@setOnClickListener
            }

            val intent =
                if (isAdmin) {

                    Intent(
                        holder.itemView.context,
                        VehicleDetailsActivity::class.java
                    )

                } else {

                    Intent(
                        holder.itemView.context,
                        UserVehicleDetails::class.java
                    )
                }

            intent.putExtra(
                "vehicleNumber",
                vehicleNumber
            )

            holder.itemView.context
                .startActivity(intent)
        }


        // =====================================================
        // DELETE BUTTON
        // =====================================================

        if (
            isAdmin &&
            !selectionMode
        ) {

            holder.btnDelete.visibility =
                View.VISIBLE

            holder.btnDelete.setOnClickListener {

                AlertDialog.Builder(
                    holder.itemView.context
                )
                    .setTitle(
                        "Delete Vehicle"
                    )
                    .setMessage(
                        "Are you sure you want to delete " +
                                "$vehicleNumber?"
                    )
                    .setPositiveButton(
                        "Delete"
                    ) { _, _ ->

                        homeViewModel
                            .deleteVehicle(
                                vehicleNumber
                            )
                    }
                    .setNegativeButton(
                        "Cancel",
                        null
                    )
                    .show()
            }

        } else {

            /*
             * Hide individual delete button
             * during selection mode.
             */

            holder.btnDelete.visibility =
                if (isAdmin) {
                    View.GONE
                } else {
                    View.GONE
                }
        }
    }
}