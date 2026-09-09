package com.example.repomaster.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import java.io.File
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import android.net.Uri
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.element.Image
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.example.repomaster.R
import com.example.repomaster.models.Invoice
class PdfReportGenerator(
    private val context: Context
) {
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "pdf_channel",
                "PDF Downloads",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(channel)
        }
    }
    private fun showNotification(
        uri: Uri,
        fileName: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (context.checkSelfPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        createNotificationChannel()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            "pdf_channel"
        )
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Report Downloaded")
            .setContentText(fileName)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(1001, notification)
    }


    private fun createOutputStream(fileName: String): PdfFile {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val values = ContentValues().apply {

                put(
                    MediaStore.Downloads.DISPLAY_NAME,
                    fileName
                )

                put(
                    MediaStore.Downloads.MIME_TYPE,
                    "application/pdf"
                )

                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )

            }

            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )!!

            val stream = context.contentResolver.openOutputStream(uri)!!

            return PdfFile(uri, stream)

        } else {

            val file = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ),
                fileName
            )

            val stream = FileOutputStream(file)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            PdfFile(uri, stream)
        }

    }

    private fun currentDate(): String {

        return SimpleDateFormat(
            "dd-MM-yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

    }

    fun generateReport(
        title: String,
        agencyId: String,
        headers: List<String>,
        rows: List<List<String>>,
        fileName: String
    ) {

        try {

            val pdfFile = createOutputStream(fileName)

            val writer = PdfWriter(pdfFile.outputStream)
            val pdfDocument = PdfDocument(writer)
            pdfDocument.addEventHandler(
                PdfDocumentEvent.END_PAGE,
                PageNumberEventHandler()
            )
            val document = Document(pdfDocument)

            val bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.launchlogo1
            )

            val stream = ByteArrayOutputStream()

            bitmap.compress(
                android.graphics.Bitmap.CompressFormat.PNG,
                100,
                stream
            )

            val imageData = ImageDataFactory.create(stream.toByteArray())

            val logo = Image(imageData)

            logo.setWidth(70f)
            logo.setHeight(70f)
            logo.setHorizontalAlignment(
                com.itextpdf.layout.properties.HorizontalAlignment.CENTER
            )

            document.add(logo)





            document.add(
                Paragraph("REPO MASTER")
                    .setBold()
                    .setFontSize(22f)
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.ORANGE)
                    .setPadding(8f)
            )

            document.add(
                Paragraph(title)
                    .setBold()
                    .setFontSize(18f)
            )

            document.add(
                Paragraph("Agency ID : $agencyId")
            )

            document.add(
                Paragraph("Generated : ${currentDate()}")
            )

            document.add(
                Paragraph("Total Records : ${rows.size}")
            )

            document.add(
                Paragraph(" ")
            )

            val table = Table(headers.size)

            headers.forEach {

                table.addHeaderCell(
                    Cell().add(
                        Paragraph(it).setBold()
                    )
                )

            }

            rows.forEach { row ->

                row.forEach { value ->

                    table.addCell(
                        Cell().add(
                            Paragraph(value)
                        )
                    )

                }

            }

            document.add(table)

            document.close()
            pdfFile.outputStream.close()
            showNotification(
                pdfFile.uri,
                fileName
            )
            Toast.makeText(

                context,

                "$fileName saved in Downloads",

                Toast.LENGTH_LONG

            ).show()

        }

        catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(

                context,

                e.message,

                Toast.LENGTH_LONG

            ).show()

        }

    }



    fun generateInvoicePdf(invoice: Invoice) {

        try {

            val fileName =
                "Invoice_${invoice.invoiceNumber ?: invoice.id ?: "Unknown"}.pdf"

            val pdfFile =
                createOutputStream(fileName)

            val writer =
                PdfWriter(pdfFile.outputStream)

            val pdfDocument =
                PdfDocument(writer)
            pdfDocument.addEventHandler(
                PdfDocumentEvent.END_PAGE,
                PageNumberEventHandler()
            )
            val document =
                Document(pdfDocument)



            document.setMargins(
                25f,
                25f,
                25f,
                25f
            )


            // ============================================================
            // LOGO
            // ============================================================

            val bitmap =
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.launchlogo1
                )

            val stream =
                ByteArrayOutputStream()

            bitmap.compress(
                android.graphics.Bitmap.CompressFormat.PNG,
                100,
                stream
            )

            val imageData =
                ImageDataFactory.create(
                    stream.toByteArray()
                )

            val logo =
                Image(imageData)

            logo.setWidth(55f)
            logo.setHeight(55f)

            logo.setHorizontalAlignment(
                com.itextpdf.layout.properties.HorizontalAlignment.CENTER
            )

            document.add(logo)


            // ============================================================
            // COMPANY HEADER
            // ============================================================

            val companyTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(100f)
                    )
                )

            companyTable.setWidth(
                UnitValue.createPercentValue(100f)
            )

            companyTable.addCell(
                invoiceCell(
                    "REPO MASTER",
                    19f,
                    TextAlignment.CENTER,
                    true
                )
            )

            document.add(companyTable)

            document.add(
                Paragraph(
                    "Vehicle Recovery Management"
                )
                    .setFontSize(9f)
                    .setTextAlignment(
                        TextAlignment.CENTER
                    )
                    .setMarginTop(2f)
            )


            // ============================================================
            // INVOICE TITLE + BASIC INFORMATION
            // ============================================================

            val titleTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(65f, 35f)
                    )
                )

            titleTable.setWidth(
                UnitValue.createPercentValue(100f)
            )

            titleTable.addCell(
                invoiceCell(
                    """
                TO,
                THE MANAGER,
                ${invoice.invoiceBank ?: "FINANCE COMPANY"}
                """.trimIndent(),
                    9f,
                    TextAlignment.LEFT,
                    true
                )
            )

            titleTable.addCell(
                invoiceCell(
                    """
                INVOICE
                Invoice No : ${invoice.invoiceNumber ?: "N/A"}
                Date       : ${formatInvoiceDate(invoice.invoiceDate)}
                """.trimIndent(),
                    9f,
                    TextAlignment.LEFT,
                    true
                )
            )

            document.add(titleTable)


            // ============================================================
            // FINANCE + CUSTOMER INFORMATION
            // ============================================================

            val financeCustomerTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(50f, 50f)
                    )
                )

            financeCustomerTable.setWidth(
                UnitValue.createPercentValue(100f)
            )

            financeCustomerTable.addCell(
                invoiceCell(
                    """
                FINANCE DETAILS
                
                Finance Bank : ${invoice.invoiceBank ?: "N/A"}
                Branch       : ${invoice.branch ?: "N/A"}
             
                """.trimIndent(),
                    8f
                )
            )

            financeCustomerTable.addCell(
                invoiceCell(
                    """
                CUSTOMER DETAILS
                
                Customer Name : ${invoice.customerName ?: "N/A"}
                Loan Number   : ${invoice.loanNumber ?: "N/A"}
                Vehicle Number: ${invoice.vehicleNumber ?: "N/A"}
                """.trimIndent(),
                    8f
                )
            )

            document.add(financeCustomerTable)


            // ============================================================
            // VEHICLE DETAILS
            // Yard is shown ONLY here
            // ============================================================

            val vehicleTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(50f, 50f)
                    )
                )

            vehicleTable.setWidth(
                UnitValue.createPercentValue(100f)
            )

            vehicleTable.addCell(
                invoiceCell(
                    """
                VEHICLE DETAILS
                
                Vehicle Type : ${invoice.vehicleType ?: "N/A"}
                Vehicle Make : ${invoice.vehicleMake ?: "N/A"}
                Vehicle Model: ${invoice.vehicleModel ?: "N/A"}
                """.trimIndent(),
                    8f
                )
            )

            vehicleTable.addCell(
                invoiceCell(
                    """
                VEHICLE / YARD
                
                Engine No   : ${invoice.engineNumber ?: "N/A"}
                Chassis No  : ${invoice.chassisNumber ?: "N/A"}
                Yard Name   : ${invoice.yardName ?: "N/A"}
                Yard Address: ${invoice.yardAddress ?: "N/A"}
                """.trimIndent(),
                    8f
                )
            )

            document.add(vehicleTable)


            // ============================================================
            // DETAILS OF BILLING
            // ============================================================

            document.add(
                Paragraph(
                    "DETAILS OF BILLING"
                )
                    .setBold()
                    .setFontSize(11f)
                    .setMarginTop(10f)
                    .setMarginBottom(3f)
            )

            val billingTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(
                            8f,
                            67f,
                            25f
                        )
                    )
                )

            billingTable.setWidth(
                UnitValue.createPercentValue(100f)
            )


            // ----------------------------
            // HEADER
            // ----------------------------

            addBillingHeader(
                billingTable,
                "SR.NO"
            )

            addBillingHeader(
                billingTable,
                "DETAILS OF BILLING"
            )

            addBillingHeader(
                billingTable,
                "AMOUNT"
            )


            // ============================================================
            // 1. DESCRIPTION 1
            // ============================================================

            addBillingRow(
                billingTable,
                "1",
                invoice.description1 ?: "Basic Charges",
                money(invoice.basic1Amount)
            )


            // ============================================================
            // 2. DESCRIPTION 2
            // ============================================================

            addBillingRow(
                billingTable,
                "2",
                invoice.description2 ?: "Additional Charges",
                money(invoice.basic2Amount)
            )


            // ============================================================
            // TOTAL BASIC
            // ============================================================

            addBillingSummaryRow(
                billingTable,
                "",
                "TOTAL BASIC",
                money(invoice.totalBasic)
            )


            // ============================================================
            // GST DETAILS
            // ============================================================

            addBillingRow(
                billingTable,
                "",
                "CGST",
                money(invoice.cgst)
            )

            addBillingRow(
                billingTable,
                "",
                "SGST",
                money(invoice.sgst)
            )

            addBillingRow(
                billingTable,
                "",
                "IGST",
                money(invoice.igst)
            )

            addBillingRow(
                billingTable,
                "",
                "GST",
                money(invoice.gst)
            )


            // ============================================================
            // INVOICE TOTAL
            // ============================================================

            addBillingSummaryRow(
                billingTable,
                "",
                "INVOICE TOTAL",
                money(invoice.invoiceTotal)
            )


            // ============================================================
            // DPD DETAILS
            // ============================================================

            addBillingRow(
                billingTable,
                "",
                "DPD",
                "${invoice.dpd ?: 0} Days"
            )

            addBillingRow(
                billingTable,
                "",
                "DPD CHARGE RATE",
                String.format(
                    Locale.getDefault(),
                    "%.2f%%",
                    invoice.dpdChargePercent ?: 0.0
                )
            )

            addBillingRow(
                billingTable,
                "",
                "DPD EXTRA CHARGE",
                money(invoice.dpdExtraCharge)
            )


            // ============================================================
            // GRAND TOTAL
            // ============================================================

            addBillingGrandTotalRow(
                billingTable,
                "TOTAL AMOUNT INCLUDING DPD",
                money(
                    invoice.dpdTotalAmount
                        ?: invoice.invoiceTotal
                )
            )


            document.add(billingTable)

// --------------------------------
// PAYMENT SUMMARY
// --------------------------------

            document.add(
                Paragraph("PAYMENT SUMMARY")
                    .setBold()
                    .setFontSize(11f)
                    .setMarginTop(10f)
                    .setMarginBottom(3f)
            )

            val paymentTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(50f, 50f)
                    )
                )

            paymentTable.setWidth(
                UnitValue.createPercentValue(100f)
            )


// --------------------------------
// LEFT SIDE
// --------------------------------

            val leftPaymentCell =
                Cell()

            leftPaymentCell.add(
                Paragraph(
                    "TOTAL BILL AMOUNT: ${
                        money(
                            invoice.dpdTotalAmount
                                ?: invoice.invoiceTotal
                        )
                    }"
                )
                    .setFontSize(9f)
                    .setBold()
            )

            leftPaymentCell.add(
                Paragraph(
                    "PAYMENT RECEIVED: ${
                        money(
                            invoice.paymentReceived
                        )
                    }"
                )
                    .setFontSize(9f)
                    .setBold()
                    .setMarginTop(5f)
            )


// --------------------------------
// RIGHT SIDE
// --------------------------------

            val rightPaymentCell =
                Cell()

            rightPaymentCell.add(
                Paragraph(
                    "REMAINING AMOUNT: ${
                        money(
                            invoice.remainingAmount
                        )
                    }"
                )
                    .setFontSize(9f)
                    .setBold()
            )

            rightPaymentCell.add(
                Paragraph(
                    "PAYMENT STATUS: ${
                        invoice.paymentStatus ?: "Pending"
                    }"
                )
                    .setFontSize(9f)
                    .setBold()
                    .setMarginTop(5f)
            )

            rightPaymentCell.add(
                Paragraph(
                    "PAYMENT DATE: ${
                        invoice.paymentDate ?: "N/A"
                    }"
                )
                    .setFontSize(9f)
                    .setBold()
                    .setMarginTop(5f)
            )


// --------------------------------
// ADD CELLS
// --------------------------------

            paymentTable.addCell(leftPaymentCell)
            paymentTable.addCell(rightPaymentCell)

            document.add(paymentTable)







            // ============================================================
            // REMARKS
            // ============================================================

            document.add(
                Paragraph(
                    "REMARKS"
                )
                    .setBold()
                    .setFontSize(11f)
                    .setMarginTop(10f)
                    .setMarginBottom(3f)
            )

            val remarksTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(100f)
                    )
                )

            remarksTable.setWidth(
                UnitValue.createPercentValue(100f)
            )

            remarksTable.addCell(
                invoiceCell(
                    invoice.remarks ?: "N/A",
                    8f
                )
            )

            document.add(remarksTable)


            // ============================================================
            // CREATED INFORMATION
            // ============================================================

            val createdTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(50f, 50f)
                    )
                )

            createdTable.setWidth(
                UnitValue.createPercentValue(100f)
            )

            createdTable.addCell(
                invoiceCell(
                    """
                Created By
                ${invoice.createdBy ?: "N/A"}
                """.trimIndent(),
                    8f
                )
            )

            createdTable.addCell(
                invoiceCell(
                    """
                Created Date
                ${invoice.createdDate ?: "N/A"}
                """.trimIndent(),
                    8f
                )
            )

            document.add(createdTable)


            // ============================================================
            // FOOTER
            // ============================================================

            // --------------------------------
// SIGNATURE SECTION
// --------------------------------

            document.add(
                Paragraph(
                    "Kindly process the bill as soon as possible."
                )
                    .setFontSize(8f)
                    .setMarginTop(12f)
            )

            document.add(
                Paragraph(
                    "Thanking you,"
                )
                    .setFontSize(8f)
            )

            val signatureTable =
                Table(
                    UnitValue.createPercentArray(
                        floatArrayOf(50f, 50f)
                    )
                )

            signatureTable.setWidth(
                UnitValue.createPercentValue(100f)
            )


// --------------------------------
// LEFT - AUTHORIZED BY
// --------------------------------

            signatureTable.addCell(
                invoiceCell(
                    "\n\n\nAuthorized By",
                    8f,
                    TextAlignment.LEFT
                )
            )


// --------------------------------
// RIGHT - DIGITAL SIGNATURE
// --------------------------------

            val signatureCell =
                Cell()

            signatureCell.setBorder(Border.NO_BORDER)
            signatureCell.setTextAlignment(
                TextAlignment.CENTER
            )

            signatureCell.add(
                Paragraph(" ")
                    .setFontSize(4f)
            )


// Load signature image
            val signatureBitmap =
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.digital_sign
                )

            val signatureStream =
                ByteArrayOutputStream()

            signatureBitmap.compress(
                android.graphics.Bitmap.CompressFormat.PNG,
                100,
                signatureStream
            )

            val signatureImageData =
                ImageDataFactory.create(
                    signatureStream.toByteArray()
                )

            val signatureImage =
                Image(signatureImageData)

            signatureImage
                .setWidth(110f)
                .setHeight(45f)
                .setHorizontalAlignment(
                    com.itextpdf.layout.properties.HorizontalAlignment.CENTER
                )

            signatureCell.add(signatureImage)

            signatureCell.add(
                Paragraph("Authorized Signature")
                    .setFontSize(8f)
                    .setBold()
                    .setTextAlignment(
                        TextAlignment.CENTER
                    )
            )

            signatureTable.addCell(signatureCell)

            document.add(signatureTable)

            // ============================================================
            // CLOSE PDF
            // ============================================================

            document.close()

            pdfFile.outputStream.close()

            showNotification(
                pdfFile.uri,
                fileName
            )

            Toast.makeText(
                context,
                "$fileName saved in Downloads",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                context,
                "PDF generation failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    private fun invoiceCell(
        text: String,
        fontSize: Float = 8f,
        alignment: TextAlignment = TextAlignment.LEFT,
        bold: Boolean = false
    ): Cell {

        val paragraph =
            Paragraph(text)
                .setFontSize(fontSize)
                .setTextAlignment(alignment)
                .setMargin(0f)

        if (bold) {
            paragraph.setBold()
        }

        return Cell()
            .add(paragraph)
            .setPadding(5f)
            .setVerticalAlignment(
                VerticalAlignment.MIDDLE
            )
            .setBorder(
                SolidBorder(1f)
            )
    }


    private fun addBillingHeader(
        table: Table,
        text: String
    ) {

        table.addHeaderCell(
            Cell()
                .add(
                    Paragraph(text)
                        .setBold()
                        .setFontSize(8f)
                        .setTextAlignment(
                            TextAlignment.CENTER
                        )
                )
                .setPadding(5f)
                .setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                )
                .setBorder(
                    SolidBorder(1f)
                )
        )
    }
    private fun addBillingRow(
        table: Table,
        serial: String,
        description: String,
        amount: String
    ) {

        table.addCell(
            invoiceCell(
                serial,
                8f,
                TextAlignment.CENTER
            )
        )

        table.addCell(
            invoiceCell(
                description,
                8f
            )
        )

        table.addCell(
            invoiceCell(
                amount,
                8f,
                TextAlignment.RIGHT
            )
        )
    }
    private fun addBillingSummaryRow(
        table: Table,
        serial: String,
        description: String,
        amount: String
    ) {

        table.addCell(
            invoiceCell(
                serial,
                8f,
                TextAlignment.CENTER,
                true
            )
        )

        table.addCell(
            invoiceCell(
                description,
                8f,
                TextAlignment.RIGHT,
                true
            )
        )

        table.addCell(
            invoiceCell(
                amount,
                8f,
                TextAlignment.RIGHT,
                true
            )
        )
    }
    private fun addBillingGrandTotalRow(
        table: Table,
        description: String,
        amount: String
    ) {

        val labelCell =
            Cell(1, 2)
                .add(
                    Paragraph(description)
                        .setBold()
                        .setFontSize(9f)
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
                .setPadding(6f)
                .setBorder(
                    SolidBorder(1f)
                )

        table.addCell(labelCell)

        table.addCell(
            invoiceCell(
                amount,
                9f,
                TextAlignment.RIGHT,
                true
            )
        )
    }
    private fun money(
        amount: Double?
    ): String {

        return String.format(
            Locale.getDefault(),
            "₹%.2f",
            amount ?: 0.0
        )
    }
    private fun formatInvoiceDate(
        date: String?
    ): String {

        if (date.isNullOrBlank()) {
            return "N/A"
        }

        return try {

            val input =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val output =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

            output.format(
                input.parse(date)!!
            )

        } catch (e: Exception) {

            date
        }
    }

}