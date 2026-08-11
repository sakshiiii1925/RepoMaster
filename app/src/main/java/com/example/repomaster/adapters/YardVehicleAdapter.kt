package com.example.repomaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.Vehicle
import com.google.android.material.button.MaterialButton
class YardVehicleAdapter(
    private var vehicleList: List<Vehicle>,
    private val onVehicleClick: (Vehicle) -> Unit,
    private val onRemoveYardClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<YardVehicleAdapter.VehicleViewHolder>() {

    inner class VehicleViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtVehicleNumber: TextView =
            itemView.findViewById(R.id.txtVehicleNumber)

        val txtOwnerName: TextView =
            itemView.findViewById(R.id.txtOwnerName)

        val txtLoanNumber: TextView =
            itemView.findViewById(R.id.txtLoanNumber)

        val txtRepoStatus: TextView =
            itemView.findViewById(R.id.txtRepoStatus)
        val btnRemoveYard: MaterialButton =
            itemView.findViewById(R.id.btnRemoveYard)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_yard_vehicle,
                parent,
                false
            )

        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: VehicleViewHolder,
        position: Int
    ) {

        val vehicle = vehicleList[position]

        holder.txtVehicleNumber.text =
            vehicle.vehicleNumber ?: "N/A"

        holder.txtOwnerName.text =
            "Owner: ${vehicle.ownerName ?: "N/A"}"

        holder.txtLoanNumber.text =
            "Loan: ${vehicle.id?.loanNumber ?: "N/A"}"

        holder.txtRepoStatus.text =
            vehicle.repoStatus ?: "N/A"

        holder.itemView.setOnClickListener {
            onVehicleClick(vehicle)
        }
        holder.btnRemoveYard.setOnClickListener {
            onRemoveYardClick(vehicle)
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }

    fun updateList(
        newList: List<Vehicle>
    ) {

        vehicleList = newList

        notifyDataSetChanged()
    }
}