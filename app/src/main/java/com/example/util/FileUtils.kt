package com.example.util

import java.text.DecimalFormat
import java.util.Locale

object FileUtils {
    fun getExtension(name: String): String {
        val dot = name.lastIndexOf('.')
        return if (dot >= 0 && dot < name.length - 1) {
            name.substring(dot + 1).lowercase(Locale.ROOT)
        } else {
            ""
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
        return DecimalFormat("#,##0.#").format(value) + " " + units[digitGroups]
    }
}
