package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.SearchHistory
import com.google.android.material.checkbox.MaterialCheckBox

class AdminSearchHistoryAdapter(
    private var list: List<SearchHistory>,
    private val onSelectionChanged:
        (selectedCount: Int, totalCount: Int) -> Unit,
    private val onSelectionModeChanged:
        (selectionMode: Boolean) -> Unit
) : RecyclerView.Adapter<AdminSearchHistoryAdapter.ViewHolder>() {

    private val selectedIds = mutableSetOf<Long>()

    private var selectionMode = false

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtUserName: TextView =
            itemView.findViewById(R.id.txtUserName)

        val txtEmail: TextView =
            itemView.findViewById(R.id.txtEmail)

        val txtVehicle: TextView =
            itemView.findViewById(R.id.txtVehicle)

        val txtTime: TextView =
            itemView.findViewById(R.id.txtTime)

        val checkSelect: MaterialCheckBox =
            itemView.findViewById(R.id.checkSelect)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_admin_search_history,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.txtUserName.text = item.userName
        holder.txtEmail.text = item.userEmail
        holder.txtVehicle.text = item.vehicleNumber
        holder.txtTime.text = item.searchTime


        // =====================================================
        // CHECKBOX VISIBILITY
        // =====================================================

        holder.checkSelect.visibility =
            if (selectionMode)
                View.VISIBLE
            else
                View.GONE


        // =====================================================
        // CHECKBOX STATE
        // =====================================================

        holder.checkSelect.setOnCheckedChangeListener(null)

        holder.checkSelect.isChecked =
            item.id != null &&
                    selectedIds.contains(item.id)


        holder.checkSelect.setOnCheckedChangeListener { _, checked ->

            val id = item.id
                ?: return@setOnCheckedChangeListener

            if (checked) {
                selectedIds.add(id)
            } else {
                selectedIds.remove(id)
            }

            onSelectionChanged(
                selectedIds.size,
                list.size
            )

            // If nothing is selected anymore,
            // leave selection mode.
            if (selectedIds.isEmpty()) {

                exitSelectionMode()
            }
        }


        // =====================================================
        // LONG PRESS
        // =====================================================

        holder.itemView.setOnLongClickListener {

            val id = item.id

            if (id != null) {

                if (!selectionMode) {

                    selectionMode = true

                    selectedIds.add(id)

                    notifyDataSetChanged()

                    onSelectionModeChanged(true)

                    onSelectionChanged(
                        selectedIds.size,
                        list.size
                    )
                }

                true

            } else {

                false
            }
        }


        // =====================================================
        // NORMAL CLICK IN SELECTION MODE
        // =====================================================

        holder.itemView.setOnClickListener {

            if (!selectionMode) {
                return@setOnClickListener
            }

            val id = item.id
                ?: return@setOnClickListener

            if (selectedIds.contains(id)) {

                selectedIds.remove(id)

            } else {

                selectedIds.add(id)
            }

            notifyItemChanged(position)

            onSelectionChanged(
                selectedIds.size,
                list.size
            )

            if (selectedIds.isEmpty()) {
                exitSelectionMode()
            }
        }
    }


    override fun getItemCount(): Int =
        list.size


    // =========================================================
    // UPDATE DATA
    // =========================================================

    fun updateData(
        newList: List<SearchHistory>
    ) {

        list = newList

        selectedIds.clear()

        selectionMode = false

        notifyDataSetChanged()

        onSelectionModeChanged(false)

        onSelectionChanged(
            0,
            list.size
        )
    }


    // =========================================================
    // SELECT ALL
    // =========================================================

    fun selectAll() {

        if (!selectionMode) {
            return
        }

        selectedIds.clear()

        list.forEach { item ->

            item.id?.let { id ->
                selectedIds.add(id)
            }
        }

        notifyDataSetChanged()

        onSelectionChanged(
            selectedIds.size,
            list.size
        )
    }


    // =========================================================
    // CLEAR SELECTION
    // =========================================================

    fun clearSelection() {

        selectedIds.clear()

        selectionMode = false

        notifyDataSetChanged()

        onSelectionModeChanged(false)

        onSelectionChanged(
            0,
            list.size
        )
    }


    // =========================================================
    // EXIT SELECTION MODE
    // =========================================================

    private fun exitSelectionMode() {

        selectedIds.clear()

        selectionMode = false

        notifyDataSetChanged()

        onSelectionModeChanged(false)

        onSelectionChanged(
            0,
            list.size
        )
    }


    // =========================================================
    // GET SELECTED IDS
    // =========================================================

    fun getSelectedIds(): List<Long> =
        selectedIds.toList()
}