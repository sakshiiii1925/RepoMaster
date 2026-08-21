package com.example.repomaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.Payment

class PaymentHistoryAdapter(
    private var payments: List<Payment>,
    private val onDeleteClick: (Payment) -> Unit
) : RecyclerView.Adapter<PaymentHistoryAdapter.PaymentViewHolder>() {

    class PaymentViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtPaymentDate: TextView =
            itemView.findViewById(R.id.txtPaymentDate)

        val txtPaymentAmount: TextView =
            itemView.findViewById(R.id.txtPaymentAmount)

        val txtPaymentRemarks: TextView =
            itemView.findViewById(R.id.txtPaymentRemarks)

        val txtPaymentCreatedBy: TextView =
            itemView.findViewById(R.id.txtPaymentCreatedBy)

        val btnDeletePayment: MaterialButton =
            itemView.findViewById(R.id.btnDeletePayment)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_payment_history,
                    parent,
                    false
                )

        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PaymentViewHolder,
        position: Int
    ) {

        val payment =
            payments[position]

        holder.txtPaymentDate.text =
            "Date: ${payment.paymentDate ?: "N/A"}"

        holder.txtPaymentAmount.text =
            "Amount: ₹%.2f".format(
                payment.paymentAmount ?: 0.0
            )

        holder.txtPaymentRemarks.text =
            "Remarks: ${payment.remarks ?: "N/A"}"

        holder.txtPaymentCreatedBy.text =
            "Created By: ${payment.createdBy ?: "N/A"}"

        holder.btnDeletePayment.setOnClickListener {

            onDeleteClick(payment)
        }
    }

    override fun getItemCount(): Int =
        payments.size

    fun updateList(
        newPayments: List<Payment>
    ) {

        payments =
            newPayments

        notifyDataSetChanged()
    }
}