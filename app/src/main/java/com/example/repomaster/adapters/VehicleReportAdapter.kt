package com.example.repomaster.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.activities.UserVehicleDetails
import com.example.repomaster.activities.VehicleDetailsActivity
import com.example.repomaster.activities.VehicleInfo
import com.example.repomaster.models.VehicleReport
import com.example.repomaster.utils.SessionManager

class VehicleReportAdapter(
    private var vehicleList: List<VehicleReport>
) : RecyclerView.Adapter<VehicleReportAdapter.VehicleViewHolder>() {

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtVehicleNumber: TextView =
            itemView.findViewById(R.id.txtVehicleNumber)


        val txtLoan: TextView =
            itemView.findViewById(R.id.txtLoan)

        val txtStatus: TextView =
            itemView.findViewById(R.id.txtStatus)
        val btnView: ImageButton =
            itemView.findViewById(R.id.btnView)




    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle_report, parent, false)

        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: VehicleViewHolder,
        position: Int
    ) {

        val vehicle = vehicleList[position]

        holder.txtVehicleNumber.text = vehicle.vehicleNumber

        holder.txtLoan.text = "${vehicle.loanNumber}"
        holder.txtStatus.text = "${vehicle.repoStatus}"
        holder.btnView.setOnClickListener {


               val intent= Intent(
                    holder.itemView.context,
                   VehicleInfo::class.java
                )

            intent.putExtra(
                "vehicleNumber",
                vehicle.vehicleNumber ?: ""
            )

            holder.itemView.context.startActivity(intent)
        }
    }
    fun updateList(newList: List<VehicleReport>) {
        vehicleList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}