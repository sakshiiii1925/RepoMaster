package com.example.repomaster.models





data class Invoice(

    val id: Long? = null,

    val invoiceNumber: String? = null,
    val invoiceDate: String? = null,

    val repoYear: String? = null,
    val repoMonth: String? = null,

    val invoiceBank: String? = null,
    val invoiceAddress: String? = null,

    val loanNumber: String? = null,
    val customerName: String? = null,
    val vehicleNumber: String? = null,

    val vehicleType: String? = null,
    val vehicleMake: String? = null,
    val vehicleModel: String? = null,

    val engineNumber: String? = null,
    val chassisNumber: String? = null,

    val description1: String? = null,
    val basic1Amount: Double? = null,

    val description2: String? = null,
    val basic2Amount: Double? = null,

    val cgst: Double? = null,
    val sgst: Double? = null,
    val igst: Double? = null,

    val totalBasic: Double? = null,
    val gst: Double? = null,
    val invoiceTotal: Double? = null,

    val remarks: String? = null,

    val createdBy: String? = null,
    val createdDate: String? = null,

    val gstPercent: Double? = null,

    val paymentDate: String? = null,
    val paymentReceived: Double? = null,

    val paymentStatus: String? = null,

    val agencyId: String? = null
)