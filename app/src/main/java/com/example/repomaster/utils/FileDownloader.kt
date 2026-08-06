package com.example.repomaster.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class FileDownloader(private val context: Context) {

    fun saveExcel(
        body: ResponseBody,
        fileName: String
    ): Uri? {

        return try {

            var fileUri: Uri? = null
            val finalFileName =
                fileName.replace(
                    ".xlsx",
                    "_${currentDateTime()}.xlsx"
                )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val values = ContentValues().apply {

                    put(
                        MediaStore.Downloads.DISPLAY_NAME,
                        finalFileName
                    )

                    put(
                        MediaStore.Downloads.MIME_TYPE,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )

                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                }

                fileUri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                )

                fileUri?.let { uri ->

                    context.contentResolver.openOutputStream(uri)?.use { output ->

                        body.byteStream().copyTo(output)

                    }

                }

            } else {

                val file = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ),
                    finalFileName
                )

                FileOutputStream(file).use {

                    body.byteStream().copyTo(it)

                }

                fileUri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

            }

            fileUri?.let {

                showNotification(
                    fileUri,
                    finalFileName
                )

            }

            fileUri

        } catch (e: Exception) {

            e.printStackTrace()

            null

        }

    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "excel_channel",
                "Excel Downloads",
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
            setDataAndType(
                uri,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
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
            "excel_channel"
        )
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Excel Downloaded Successfully")
            .setContentText("Saved as $fileName")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(1001, notification)
    }


    private fun currentDateTime(): String {

            return SimpleDateFormat(
                "dd-MM-yyyy_HH-mm-ss",
                Locale.getDefault()
            ).format(Date())


    }
}