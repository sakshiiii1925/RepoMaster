package com.example.repomaster.utils

import android.net.Uri
import java.io.OutputStream

data class PdfFile(
    val uri: Uri,
    val outputStream: OutputStream
)