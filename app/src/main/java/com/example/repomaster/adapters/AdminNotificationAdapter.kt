package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.AdminNotification

class AdminNotificationAdapter(
    private var notifications: List<AdminNotification>,
    private val onClick: (AdminNotification) -> Unit
) : RecyclerView.Adapter<AdminNotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvVehicleNumber: TextView =
            itemView.findViewById(R.id.tvVehicleNumber)

        val tvMessage: TextView =
            itemView.findViewById(R.id.tvMessage)


        val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_admin_notification,
                    parent,
                    false
                )

        return NotificationViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {

        val notification =
            notifications[position]

        holder.tvVehicleNumber.text =
            notification.vehicle_number

        holder.tvMessage.text =
            notification.message


        holder.tvDate.text =
            notification.created_at

        holder.itemView.setOnClickListener {

            onClick(notification)

        }
    }


    override fun getItemCount(): Int =
        notifications.size


    fun updateList(
        newData: List<AdminNotification>
    ) {

        notifications = newData

        notifyDataSetChanged()
    }
}