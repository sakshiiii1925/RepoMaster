package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.SearchHistory
import com.google.android.material.checkbox.MaterialCheckBox
import android.widget.TextView

class AdminSearchHistoryAdapter(
    private var list: List<SearchHistory>
) : RecyclerView.Adapter<AdminSearchHistoryAdapter.ViewHolder>() {

    private val selectedIds =
        mutableSetOf<Long>()

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

        val view =
            LayoutInflater.from(parent.context)
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

        holder.txtUserName.text =
            item.userName

        holder.txtEmail.text =
            item.userEmail

        holder.txtVehicle.text =
            item.vehicleNumber

        holder.txtTime.text =
            item.searchTime

        holder.checkSelect.setOnCheckedChangeListener(null)

        holder.checkSelect.isChecked =
            item.id != null &&
                    selectedIds.contains(item.id)

        holder.checkSelect.setOnCheckedChangeListener { _, checked ->

            val id = item.id ?: return@setOnCheckedChangeListener

            if (checked) {
                selectedIds.add(id)
            } else {
                selectedIds.remove(id)
            }
        }
    }

    override fun getItemCount(): Int =
        list.size

    fun updateData(
        newList: List<SearchHistory>
    ) {

        list = newList

        selectedIds.clear()

        notifyDataSetChanged()
    }

    fun getSelectedIds(): List<Long> =
        selectedIds.toList()

    fun clearSelection() {

        selectedIds.clear()

        notifyDataSetChanged()
    }
}