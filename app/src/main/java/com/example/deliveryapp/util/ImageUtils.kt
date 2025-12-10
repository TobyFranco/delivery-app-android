package com.example.deliveryapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

    private const val MAX_WIDTH = 1024
    private const val MAX_HEIGHT = 1024
    private const val JPEG_QUALITY = 80

    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val resizedBitmap = resizeBitmap(bitmap)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "pedido_$timestamp.jpg"
            val file = File(context.filesDir, fileName)

            FileOutputStream(file).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }

            LogUtils.logInfo("Imagen guardada: ${file.absolutePath}")
            file.absolutePath

        } catch (e: Exception) {
            LogUtils.logError("Error al guardar imagen", e)
            null
        }
    }

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
        return try {
            val resizedBitmap = resizeBitmap(bitmap)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "pedido_$timestamp.jpg"
            val file = File(context.filesDir, fileName)

            FileOutputStream(file).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }

            LogUtils.logInfo("Bitmap guardado: ${file.absolutePath}")
            file.absolutePath

        } catch (e: Exception) {
            LogUtils.logError("Error al guardar bitmap", e)
            null
        }
    }

    fun imageToBase64(imagePath: String): String? {
        return try {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, baos)
            val imageBytes = baos.toByteArray()
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            LogUtils.logError("Error al convertir imagen a Base64", e)
            null
        }
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) {
            newWidth = MAX_WIDTH
            newHeight = (MAX_WIDTH / ratio).toInt()
        } else {
            newHeight = MAX_HEIGHT
            newWidth = (MAX_HEIGHT * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun deleteImage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            val deleted = file.delete()
            if (deleted) {
                LogUtils.logInfo("Imagen eliminada: $imagePath")
            }
            deleted
        } catch (e: Exception) {
            LogUtils.logError("Error al eliminar imagen", e)
            false
        }
    }
}