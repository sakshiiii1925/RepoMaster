package com.example.repomaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repomaster.R
import com.example.repomaster.model.ReportRow
import com.example.repomaster.adapters.OnReportClickListener

class ReportAdapter(
    private val list: List<ReportRow>,
    private val reportType: String,
    private val listener: OnReportClickListener
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)

        val col1 = view.findViewById<TextView>(R.id.txtCol1)
        val col2 = view.findViewById<TextView>(R.id.txtCol2)
        val col3 = view.findViewById<TextView>(R.id.txtCol3)
        val col4 = view.findViewById<TextView>(R.id.txtCol4)
        val col5 = view.findViewById<TextView>(R.id.txtCol5)
        val col6= view.findViewById<TextView>(R.id.txtCol6)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.col1.text = item.columns.getOrNull(0) ?: ""
        holder.col2.text = item.columns.getOrNull(1) ?: ""
        holder.col3.text = item.columns.getOrNull(2) ?: ""
        holder.col4.text = item.columns.getOrNull(3) ?: ""
        holder.col5.text = item.columns.getOrNull(4) ?: ""
        holder.col6.text = item.columns.getOrNull(5) ?: ""

        if (reportType == "FINANCE") {

            val finance = item.columns.getOrNull(0) ?: ""
            val branch = item.columns.getOrNull(1) ?: ""

            holder.col3.setOnClickListener {
                listener.onFinanceReportClick(finance, branch, "ALL")
            }

            holder.col4.setOnClickListener {
                listener.onFinanceReportClick(finance, branch, "repo mark")
            }

            holder.col5.setOnClickListener {
                listener.onFinanceReportClick(finance, branch, "Parked")
            }

            holder.col6.setOnClickListener {
                listener.onFinanceReportClick(finance, branch, "Released")
            }

        } else if (reportType == "MONTHLY") {

            val year = item.columns.getOrNull(0) ?: ""
            val month = item.columns.getOrNull(1) ?: ""

            holder.col3.setOnClickListener {
                listener.onMonthlyReportClick(year, month, "ALL")
            }

            holder.col4.setOnClickListener {
                listener.onMonthlyReportClick(year, month, "repo mark")
            }

            holder.col5.setOnClickListener {
                listener.onMonthlyReportClick(year, month, "Parked")
            }

            holder.col6.setOnClickListener {
                listener.onMonthlyReportClick(year, month, "Released")
            }
        }


    }
}