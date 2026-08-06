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

}