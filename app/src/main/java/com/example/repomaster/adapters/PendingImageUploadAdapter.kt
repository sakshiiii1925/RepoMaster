package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.data.local.PendingImageUploadEntity
import com.google.android.material.button.MaterialButton

class PendingImageUploadAdapter(
    private var uploads: List<PendingImageUploadEntity>,
    private val onAddClick:
        (PendingImageUploadEntity) -> Unit
) : RecyclerView.Adapter<PendingImageUploadAdapter.ViewHolder>() {

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtVehicleNumber: TextView =
            itemView.findViewById(R.id.txtVehicleNumber)

        val txtStatus: TextView =
            itemView.findViewById(R.id.txtStatus)

        val btnAdd: MaterialButton =
            itemView.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_pending_image_upload,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val upload = uploads[position]

        holder.txtVehicleNumber.text =
            upload.vehicleNumber

        holder.txtStatus.text =
            upload.status

        holder.btnAdd.setOnClickListener {

            onAddClick(upload)
        }
    }

    override fun getItemCount(): Int =
        uploads.size

    fun updateList(
        newList: List<PendingImageUploadEntity>
    ) {

        uploads = newList

        notifyDataSetChanged()
    }
}