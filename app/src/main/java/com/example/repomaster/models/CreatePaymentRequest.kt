
package com.example.repomaster.models

data class CreatePaymentRequest(
    val user_id: Int,
    val repo_year: String,
    val repo_month: String,
    val loan_number: String,
    val work_type: String,
    val payment_method: String,
    val remarks: String?
)

