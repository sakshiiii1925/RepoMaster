package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.UserActivityReport

class UserActivityAdapter(
    private var userList: List<UserActivityReport>,
    private val listener: OnUserActivityClickListener
) : RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder>() {

    interface OnUserActivityClickListener {

        fun onRepoMarkedClick(
            userEmail: String
        )

        fun onParkedClick(
            userEmail: String
        )

        fun onReleasedClick(
            userEmail: String
        )
    }

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
            item.userName

        holder.txtEmail.text =
            item.userEmail

        holder.txtTotalSearches.text =
            item.totalSearches.toString()

        holder.txtRepoMarked.text =
            item.RepoInitAndParkedCount.toString()


        holder.txtReleased.text =
            item.releasedCount.toString()

        holder.txtLastSearchTime.text =
            item.lastSearchTime ?: "-"


        // =========================================
        // Repo init and Parked CLICK
        // =========================================

        holder.txtRepoMarked.setOnClickListener {

            if (item.RepoInitAndParkedCount > 0) {

                listener.onRepoMarkedClick(
                    item.userEmail
                )
            }
        }


        // =========================================
        // PARKED CLICK
        // =========================================






        // =========================================
        // RELEASED CLICK
        // =========================================

        holder.txtReleased.setOnClickListener {

            if (item.releasedCount > 0) {

                listener.onReleasedClick(
                    item.userEmail
                )
            }
        }


        // =========================================
        // VISUAL CLICKABLE STATE
        // =========================================

        holder.txtRepoMarked.isClickable =
            item.RepoInitAndParkedCount > 0



        holder.txtReleased.isClickable =
            item.releasedCount > 0
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateList(
        newList: List<UserActivityReport>
    ) {

        userList = newList

        notifyDataSetChanged()
    }
}