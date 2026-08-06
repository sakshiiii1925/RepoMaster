package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.User
import android.widget.*

class PendingUserAdapter(
    private val users: List<User>,
    private val onApproveClick: (User) -> Unit,
    private val onRejectClick: (User) -> Unit
) : RecyclerView.Adapter<PendingUserAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)
        val txtMobile: TextView = itemView.findViewById(R.id.txtMobile)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)

        val btnApprove: Button = itemView.findViewById(R.id.btnApprove)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_user, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = users[position]

        holder.txtName.text = user.fullName
        holder.txtEmail.text = user.email
        holder.txtMobile.text = user.mobile
        holder.txtAddress.text = user.address

        //  add Approve and Reject button logic.
        holder.btnApprove.setOnClickListener {

            user.id?.let { id ->

                onApproveClick(user)

            } ?: Toast.makeText(
                holder.itemView.context,
                "Invalid User ID",
                Toast.LENGTH_SHORT
            ).show()
        }
        holder.btnReject.setOnClickListener {
            user.id?.let { id ->

                onRejectClick(user)

            } ?: Toast.makeText(
                holder.itemView.context,
                "Invalid User ID",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}