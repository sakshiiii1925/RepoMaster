package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.UserActivityReport

class UserActivityAdapter(
    private var userList: List<UserActivityReport>
) : RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder>() {

    class UserActivityViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtUserName: TextView =
            itemView.findViewById(R.id.txtUserName)

        val txtEmail: TextView =
            itemView.findViewById(R.id.txtEmail)

        val txtTotalSearches: TextView =
            itemView.findViewById(R.id.txtTotalSearches)

        val txtRepoMarked: TextView =
            itemView.findViewById(R.id.txtRepoMarked)

        val txtParked: TextView =
            itemView.findViewById(R.id.txtParked)

        val txtReleased: TextView =
            itemView.findViewById(R.id.txtReleased)

        val txtLastSearchTime: TextView =
            itemView.findViewById(R.id.txtLastSearchTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserActivityViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_user_activity,
                parent,
                false
            )

        return UserActivityViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UserActivityViewHolder,
        position: Int
    ) {

        val item = userList[position]

        holder.txtUserName.text =
            " ${item.userName}"

        holder.txtEmail.text =
            "${item.userEmail}"

        holder.txtTotalSearches.text =
            "${item.totalSearches}"

        holder.txtRepoMarked.text =
            "${item.repoMarkedCount}"

        holder.txtParked.text =
            "${item.parkedCount}"

        holder.txtReleased.text =
            "${item.releasedCount}"

        holder.txtLastSearchTime.text =
            "${item.lastSearchTime}"
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateList(newList: List<UserActivityReport>) {
        userList = newList
        notifyDataSetChanged()
    }
}