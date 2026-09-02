
package com.example.repomaster.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.User

class UsersAdapter(
    private var list: List<User>,
    private val onDeleteClick: (User) -> Unit,
    private val onStatusClick: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtName: TextView =
            view.findViewById(R.id.txtusername)

        val txtEmail: TextView =
            view.findViewById(R.id.txtuseremail)

        val txtStatus: TextView =
            view.findViewById(R.id.txtStatus)

        val btnToggleStatus: Button =
            view.findViewById(R.id.btnToggleStatus)

        val btnDelete: ImageButton =
            view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_user,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val user = list[position]

        holder.txtName.text =
            user.fullName ?: "-"

        holder.txtEmail.text =
            user.email ?: "-"

        /*
         * ACTIVE USER
         *
         * Database status = ACTIVE
         * Button = Deactivate
         * Button color = GREEN
         */
        if (
            user.status.equals(
                "ACTIVE",
                ignoreCase = true
            )
        ) {

            holder.txtStatus.text =
                "ACTIVE"

            holder.btnToggleStatus.text =
                "Deactivate"

            holder.btnToggleStatus.setBackgroundColor(
                Color.parseColor("#4CAF50")
            )

        }

        /*
         * INACTIVE USER
         *
         * Database status = INACTIVE
         * Button = Activate
         * Button color = RED
         */
        else {

            holder.txtStatus.text =
                "INACTIVE"

            holder.btnToggleStatus.text =
                "Activate"

            holder.btnToggleStatus.setBackgroundColor(
                Color.parseColor("#F44336")
            )
        }

        /*
         * Activate / Deactivate
         */
        holder.btnToggleStatus.setOnClickListener {

            onStatusClick(user)
        }

        /*
         * Delete
         */
        holder.btnDelete.setOnClickListener {

            onDeleteClick(user)
        }
    }

    override fun getItemCount(): Int =
        list.size

    fun updateData(
        newList: List<User>
    ) {

        list = newList

        notifyDataSetChanged()
    }

    fun updateUser(updatedUser: User) {

        val index = list.indexOfFirst {
            it.id == updatedUser.id
        }

        if (index != -1) {

            val updatedList =
                list.toMutableList()

            updatedList[index] = updatedUser

            list = updatedList

            notifyItemChanged(index)
        }
    }


}

