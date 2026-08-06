package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.Vehicle

class SearchSuggestionAdapter(
    private var vehicleList: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<SearchSuggestionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtVehicleNumber: TextView =
            itemView.findViewById(R.id.txtVehicleNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_vehicle, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val vehicle = vehicleList[position]

        holder.txtVehicleNumber.text = vehicle.vehicleNumber

        holder.itemView.setOnClickListener {
            onItemClick(vehicle)
        }
    }

    override fun getItemCount(): Int = vehicleList.size

    fun updateList(newList: List<Vehicle>) {
        vehicleList = newList
        notifyDataSetChanged()
    }
}