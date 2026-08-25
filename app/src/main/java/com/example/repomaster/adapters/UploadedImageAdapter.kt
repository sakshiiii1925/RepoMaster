package com.example.repomaster.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.UploadedImage

class UploadedImageAdapter(
    private val onViewClick: (UploadedImage) -> Unit,
    private val onDeleteClick: (UploadedImage) -> Unit

) : ListAdapter<UploadedImage, UploadedImageAdapter.ViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_uploaded_image,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(
            getItem(position)
        )
    }

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtVehicleNumber =
            itemView.findViewById<TextView>(
                R.id.txtVehicleNumber
            )

        private val txtUserName =
            itemView.findViewById<TextView>(
                R.id.txtUserName
            )

        private val txtUserEmail =
            itemView.findViewById<TextView>(
                R.id.txtUserEmail
            )

        private val txtStatus =
            itemView.findViewById<TextView>(
                R.id.txtStatus
            )

        private val txtUploadedAt =
            itemView.findViewById<TextView>(
                R.id.txtUploadedAt
            )
        private val btnView =
            itemView.findViewById<ImageButton>(
                R.id.btnView
            )
        private val btnDelete =
            itemView.findViewById<ImageButton>(
                R.id.btnDelete
            )
        fun bind(
            item: UploadedImage
        ) {

            txtVehicleNumber.text =
                item.vehicle_number

            txtUserName.text =
                "User: ${
                    item.user_name ?: "Unknown"
                }"

            txtUserEmail.text =
                "Email: ${
                    item.user_email ?: "Unknown"
                }"

            txtStatus.text =
                "Status: ${
                    item.status ?: "Unknown"
                }"

            txtUploadedAt.text =
                "Uploaded: ${
                    item.uploaded_at ?: "-"
                }"

            btnView.setOnClickListener {

                onViewClick(item)
            }
            btnDelete.setOnClickListener {

                onDeleteClick(item)
            }
        }
    }

    class DiffCallback :
        DiffUtil.ItemCallback<UploadedImage>() {

        override fun areItemsTheSame(
            oldItem: UploadedImage,
            newItem: UploadedImage
        ): Boolean {

            return oldItem.id ==
                    newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UploadedImage,
            newItem: UploadedImage
        ): Boolean {

            return oldItem == newItem
        }
    }
}