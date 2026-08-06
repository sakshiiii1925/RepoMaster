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
import com.example.repomaster.models.User
class UsersAdapter(
    private var list: List<User>,
    private val onDeleteClick:(User)->Unit
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtName = view.findViewById<TextView>(R.id.txtusername)
        val txtEmail = view.findViewById<TextView>(R.id.txtuseremail)
        val txtStatus = view.findViewById<TextView>(R.id.txtStatus)
        val btnDelete: ImageButton =
            itemView.findViewById(R.id.btnDelete)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = list[position]

        holder.txtName.text = user.fullName
        holder.txtEmail.text = user.email
        holder.txtStatus.text = user.status
        holder.btnDelete.setOnClickListener {

            onDeleteClick(user)

        }


    }

    override fun getItemCount() = list.size

}