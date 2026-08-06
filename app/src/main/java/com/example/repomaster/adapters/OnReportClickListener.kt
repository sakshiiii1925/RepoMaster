package com.example.repomaster.adapters



interface OnReportClickListener {

    fun onFinanceReportClick(
        finance: String,
        branch: String,
        status: String
    )

    fun onMonthlyReportClick(
        year: String,
        month: String,
        status: String
    )
}