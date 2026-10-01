package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Locale

object ApkManager {

    fun getInstalledApkFile(context: Context): File {
        return File(context.applicationInfo.sourceDir)
    }

    fun getApkSizeFormatted(context: Context): String {
        val file = getInstalledApkFile(context)
        if (!file.exists()) return "Unknown"
        val bytes = file.length()
        val mb = bytes.toDouble() / (1024 * 1024)
        return String.format(Locale.US, "%.2f MB", mb)
    }

    fun getApkSha256(context: Context): String {
        return try {
            val file = getInstalledApkFile(context)
            if (!file.exists()) return "N/A"
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var read: Int
                while (fis.read(buffer).also { read = it } != -1) {
                    digest.update(buffer, 0, read)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Checksum unavailable"
        }
    }

    fun copyApkToDownloads(context: Context): Result<String> {
        return try {
            val sourceFile = getInstalledApkFile(context)
            if (!sourceFile.exists()) {
                return Result.failure(IllegalStateException("Source APK file not accessible"))
            }

            val filename = "aegis-android-v1.0.apk"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: return Result.failure(IllegalStateException("Failed to create download entry in MediaStore"))

                context.contentResolver.openOutputStream(uri)?.use { out ->
                    sourceFile.inputStream().use { input ->
                        input.copyTo(out)
                    }
                } ?: return Result.failure(IllegalStateException("Failed to write to destination"))

                Result.success("Saved to system Downloads: $filename")
            } else {
                val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                targetDir.mkdirs()
                val targetFile = File(targetDir, filename)
                sourceFile.copyTo(targetFile, overwrite = true)
                Result.success("Saved to ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            // Fallback to app external storage
            try {
                val extDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
                val fallbackFile = File(extDir, "aegis-android-v1.0.apk")
                getInstalledApkFile(context).copyTo(fallbackFile, overwrite = true)
                Result.success("Saved to ${fallbackFile.absolutePath}")
            } catch (fallbackEx: Exception) {
                Result.failure(e)
            }
        }
    }

    fun shareApk(context: Context): Result<Unit> {
        return try {
            val sourceFile = getInstalledApkFile(context)
            if (!sourceFile.exists()) {
                return Result.failure(IllegalStateException("Source APK file does not exist"))
            }

            // Copy to export cache to ensure readable content provider sharing
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val exportedApk = File(exportDir, "aegis-android-v1.0.apk")
            sourceFile.copyTo(exportedApk, overwrite = true)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                exportedApk
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "AEGIS Android Security Engine APK")
                putExtra(Intent.EXTRA_TEXT, "Here is the compiled AEGIS Android Security Engine APK package.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Download / Export APK via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
