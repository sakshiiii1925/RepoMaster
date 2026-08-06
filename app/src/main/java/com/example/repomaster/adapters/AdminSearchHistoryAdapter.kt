package com.example.repomaster.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.activities.VehicleDetailsActivity
import com.example.repomaster.models.SearchHistory

class AdminSearchHistoryAdapter(
    private var list: List<SearchHistory>
) : RecyclerView.Adapter<AdminSearchHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtUserName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)
        val txtVehicle: TextView = itemView.findViewById(R.id.txtVehicle)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_search_history, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.txtUserName.text = item.userName
        holder.txtEmail.text = item.userEmail
        holder.txtVehicle.text = item.vehicleNumber
        holder.txtTime.text = item.searchTime


    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<SearchHistory>) {
        list = newList
        notifyDataSetChanged()
    }
}