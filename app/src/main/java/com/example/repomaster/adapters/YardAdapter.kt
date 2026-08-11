package com.example.repomaster.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.activities.EditYard
import com.example.repomaster.models.Yard
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat.startActivity
import com.example.repomaster.activities.YardVehiclesActivity

class YardAdapter(
    private var yards: List<Yard>,
    private val onDeleteClick: (Yard) -> Unit
) : RecyclerView.Adapter<YardAdapter.YardViewHolder>() {

    class YardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtYardName: TextView =
            itemView.findViewById(R.id.txtYardName)

        val txtYardAddress: TextView =
            itemView.findViewById(R.id.txtYardAddress)

        val txtYardManager: TextView =
            itemView.findViewById(R.id.txtYardManager)

        val txtYardContact: TextView =
            itemView.findViewById(R.id.txtYardContact)

        val btnEditYard: MaterialButton =
            itemView.findViewById(R.id.btnEditYard)

        val btnDeleteYard: MaterialButton =
            itemView.findViewById(R.id.btnDeleteYard)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_yard,
                parent,
                false
            )

        return YardViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: YardViewHolder,
        position: Int
    ) {

        val yard = yards[position]

        // -------------------------
        // Display Yard Information
        // -------------------------

        holder.txtYardName.text =
            yard.yardName

        holder.txtYardAddress.text =
            "Address: ${yard.yardAddress ?: "N/A"}"

        holder.txtYardManager.text =
            "Manager: ${yard.yardManagerName ?: "N/A"}"

        holder.txtYardContact.text =
            "Contact: ${yard.yardContactNo ?: "N/A"}"

        // -------------------------
        // EDIT YARD
        // -------------------------

        holder.btnEditYard.setOnClickListener {

            val intent = Intent(
                holder.itemView.context,
                EditYard::class.java
            )

            intent.putExtra(
                "yard_id",
                yard.id
            )

            intent.putExtra(
                "yard_name",
                yard.yardName
            )

            intent.putExtra(
                "yard_address",
                yard.yardAddress
            )

            intent.putExtra(
                "yard_manager_name",
                yard.yardManagerName
            )

            intent.putExtra(
                "yard_contact_no",
                yard.yardContactNo
            )

            intent.putExtra(
                "agency_id",
                yard.agencyId
            )

            holder.itemView.context.startActivity(intent)
        }

        // -------------------------
        // DELETE YARD
        // -------------------------

        holder.btnDeleteYard.setOnClickListener {

            onDeleteClick(yard)
        }
        holder.itemView.setOnClickListener {

            val intent = Intent(
                holder.itemView.context,
                YardVehiclesActivity::class.java
            )

            intent.putExtra(
                "yardId",
                yard.id
            )

            intent.putExtra(
                "yardName",
                yard.yardName
            )

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return yards.size
    }

    fun updateList(newList: List<Yard>) {

        yards = newList

        notifyDataSetChanged()
    }
    fun removeYard(yardId: Long) {

        val updatedList =
            yards.filter { it.id != yardId }

        yards = updatedList

        notifyDataSetChanged()
    }
}