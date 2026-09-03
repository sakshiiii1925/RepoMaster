package com.example.repomaster.activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.databinding.ItemPaymentHistory1Binding
import com.example.repomaster.models.AdminPayment

class PaymentHistoryAdapter1(
    private var items: List<AdminPayment>,
    private val onDeleteClick: (AdminPayment) -> Unit
) : RecyclerView.Adapter<PaymentHistoryAdapter1.ViewHolder>() {

    class ViewHolder(
        val binding: ItemPaymentHistory1Binding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemPaymentHistory1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = items[position]
        val binding = holder.binding

        binding.txtVehicleNumber.text =
            item.vehicle_number

        binding.txtWorkType.text =
            "Work: ${item.repo_status ?: "-"}"

        binding.txtVehicleType.text =
            "Vehicle Type: ${item.vehicle_type ?: "-"}"

        binding.txtAmount.text =
            "Amount Paid: ₹${item.amount}"

        binding.txtPaymentMethod.text =
            "Payment Method: ${item.payment_method}"

        binding.txtPaymentDate.text =
            "Payment Date: ${item.payment_date}"

        binding.txtRemarks.text =
            "Remarks: ${item.remarks ?: "-"}"

        binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int =
        items.size

    fun updateData(
        newItems: List<AdminPayment>
    ) {
        items = newItems
        notifyDataSetChanged()
    }
}