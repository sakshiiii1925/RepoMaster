package com.example.repomaster.adapters


import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.activities.UserVehicleDetails
import com.example.repomaster.activities.VehicleDetailsActivity
import com.example.repomaster.models.Vehicle
import com.example.repomaster.viewmodel.HomeViewModel
import com.example.repomaster.utils.SessionManager



class VehicleAdapter(
    private var vehicleList: List<Vehicle>,
    private val homeViewModel: HomeViewModel
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {



    class VehicleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {


        val txtVehicleNo: TextView =
            itemView.findViewById(R.id.txtVehicleNumber)


        val txtEngNum: TextView =
            itemView.findViewById(R.id.txtEngine)


        val txtchassnum: TextView =
            itemView.findViewById(R.id.txtChassis)


        val btnView: ImageButton =
            itemView.findViewById(R.id.btnView)



        val btnDelete: ImageButton =
            itemView.findViewById(R.id.btnDelete)

    }




    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleViewHolder {


        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_vehicle,
                    parent,
                    false
                )


        return VehicleViewHolder(view)

    }




    override fun getItemCount(): Int {

        return vehicleList.size

    }





    fun updateList(newList: List<Vehicle>) {

        vehicleList = newList

        notifyDataSetChanged()

    }





    override fun onBindViewHolder(
        holder: VehicleViewHolder,
        position: Int
    ) {


        val vehicle =
            vehicleList[position]



        holder.txtVehicleNo.text =
            vehicle.vehicleNumber ?: ""



        holder.txtEngNum.text =
            vehicle.engineNumber ?: ""



        // Changed status -> repoStatus

        holder.txtchassnum.text =
            vehicle.chassisNumber ?: ""


        // View Button

        holder.btnView.setOnClickListener {

            val sessionManager = SessionManager(holder.itemView.context)

            val role = sessionManager.getRole()

            val intent = if (role.equals("ADMIN", ignoreCase = true)) {

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
                vehicle.vehicleNumber ?: ""
            )

            holder.itemView.context.startActivity(intent)
        }

        // Delete Button
        val sessionManager = SessionManager(holder.itemView.context)
        val role = sessionManager.getRole()

        if (role.equals("ADMIN", ignoreCase = true)) {

            holder.btnDelete.visibility = View.VISIBLE

            holder.btnDelete.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete Vehicle")
                    .setMessage("Are you sure you want to delete this vehicle?")
                    .setPositiveButton("Delete") { _, _ ->
                        homeViewModel.deleteVehicle(vehicle.vehicleNumber ?: "")
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

        } else {

            holder.btnDelete.visibility = View.GONE

        }



    }


}