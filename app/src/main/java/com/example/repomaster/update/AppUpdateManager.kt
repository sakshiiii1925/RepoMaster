package com.example.repomaster.update
import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AppUpdateManager(
    private val context: Context
) {

    suspend fun checkForUpdate(): UpdateInfo? =
        withContext(Dispatchers.IO) {

            try {
                val url = URL(
                    "https://okisplsoftware.com/RepoMasterUpdates/version.json"
                )

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"

                try {
                    if (connection.responseCode != 200) {
                        return@withContext null
                    }

                    val response =
                        connection.inputStream.bufferedReader().use {
                            it.readText()
                        }

                    val json = JSONObject(response)

                    val latestVersion = UpdateInfo(
                        versionCode = json.getInt("versionCode"),
                        versionName = json.getString("versionName"),
                        apkUrl = json.getString("apkUrl"),
                        forceUpdate = json.getBoolean("forceUpdate"),
                        releaseNotes = json.getString("releaseNotes")
                    )

                    val packageInfo =
                        context.packageManager.getPackageInfo(
                            context.packageName,
                            0
                        )

                    val installedVersion =
                        if (android.os.Build.VERSION.SDK_INT >= 28) {
                            packageInfo.longVersionCode
                        } else {
                            @Suppress("DEPRECATION")
                            packageInfo.versionCode.toLong()
                        }

                    if (latestVersion.versionCode > installedVersion) {
                        latestVersion
                    } else {
                        null
                    }

                } finally {
                    connection.disconnect()
                }

            } catch (e: Exception) {
                null
            }
        }
}