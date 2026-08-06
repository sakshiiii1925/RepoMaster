package com.example.repomaster.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.SearchHistory

class RecentSearchAdapter(
    private var list: List<SearchHistory>,
    private val onClick: (SearchHistory) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtVehicle: TextView = itemView.findViewById(R.id.txtVehicleNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_vehicle, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.txtVehicle.text = item.vehicleNumber

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<SearchHistory>) {
        list = newList
        notifyDataSetChanged()
    }
}