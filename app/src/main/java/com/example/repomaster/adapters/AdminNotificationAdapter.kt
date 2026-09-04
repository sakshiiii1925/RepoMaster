package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.AdminNotificationItem

class AdminNotificationAdapter(
    private var notifications: List<AdminNotificationItem>,
    private val onClick: (AdminNotificationItem) -> Unit
) : RecyclerView.Adapter<AdminNotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val ivIcon: ImageView =
            itemView.findViewById(R.id.ivIcon)

        val tvTitle: TextView =
            itemView.findViewById(R.id.tvTitle)

        val tvMessage: TextView =
            itemView.findViewById(R.id.tvMessage)

        val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)

        val viewUnread: View =
            itemView.findViewById(R.id.viewUnread)
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

        holder.tvTitle.text =
            notification.title

        holder.tvMessage.text =
            notification.message

        holder.tvDate.text =
            notification.date

        if (notification.type == "USER") {

            holder.ivIcon.setImageResource(
                android.R.drawable.ic_menu_myplaces
            )

        } else {

            holder.ivIcon.setImageResource(
                android.R.drawable.ic_menu_directions
            )
        }

        holder.viewUnread.visibility =
            if (notification.isRead) {
                View.GONE
            } else {
                View.VISIBLE
            }

        holder.itemView.setOnClickListener {
            onClick(notification)
        }
    }

    override fun getItemCount(): Int =
        notifications.size

    fun updateList(
        newData: List<AdminNotificationItem>
    ) {

        notifications = newData
        notifyDataSetChanged()
    }

    fun removeNotification(
        notification: AdminNotificationItem
    ) {

        val mutableList =
            notifications.toMutableList()

        val index =
            mutableList.indexOfFirst {
                it == notification
            }

        if (index != -1) {

            mutableList.removeAt(index)

            notifications =
                mutableList

            notifyItemRemoved(index)
        }
    }
}