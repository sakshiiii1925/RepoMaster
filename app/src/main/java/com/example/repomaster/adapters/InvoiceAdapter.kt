package com.example.repomaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.models.Invoice

class InvoiceAdapter(
    private var invoiceList: List<Invoice>,
    private val onInvoiceClick: (Invoice) -> Unit
) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    class InvoiceViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtInvoiceNumber: TextView =
            itemView.findViewById(R.id.txtInvoiceNumber)

        val txtCustomerName: TextView =
            itemView.findViewById(R.id.txtCustomerName)

        val txtVehicleNumber: TextView =
            itemView.findViewById(R.id.txtVehicleNumber)

        val txtInvoiceTotal: TextView =
            itemView.findViewById(R.id.txtInvoiceTotal)

        val txtPaymentStatus: TextView =
            itemView.findViewById(R.id.txtPaymentStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvoiceViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_invoice,
                parent,
                false
            )

        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: InvoiceViewHolder,
        position: Int
    ) {

        val invoice = invoiceList[position]

        holder.txtInvoiceNumber.text =
            "Invoice: ${invoice.invoiceNumber ?: "N/A"}"

        holder.txtCustomerName.text =
            "Customer: ${invoice.customerName ?: "N/A"}"

        holder.txtVehicleNumber.text =
            "Vehicle: ${invoice.vehicleNumber ?: "N/A"}"

        holder.txtInvoiceTotal.text =
            "Total: ₹${invoice.invoiceTotal ?: 0.0}"

        holder.txtPaymentStatus.text =
            "Status: ${invoice.paymentStatus ?: "N/A"}"

        holder.itemView.setOnClickListener {
            onInvoiceClick(invoice)
        }
    }

    override fun getItemCount(): Int {
        return invoiceList.size
    }

    fun updateList(
        newList: List<Invoice>
    ) {
        invoiceList = newList
        notifyDataSetChanged()
    }
}