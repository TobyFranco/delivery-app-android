package com.example.deliveryapp.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object LogUtils {

    private const val TAG = "DeliveryApp"
    private const val LOG_FILE_NAME = "delivery_logs.txt"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private var logFile: File? = null

    fun initialize(context: Context) {
        logFile = File(context.filesDir, LOG_FILE_NAME)
        if (!logFile!!.exists()) {
            logFile!!.createNewFile()
        }
        logInfo("Sistema de logs inicializado")
    }

    fun logInfo(message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] [INFO] $message"

        Log.i(TAG, message)
        writeToFile(logMessage)
    }

    fun logError(message: String, exception: Exception? = null) {
        val timestamp = dateFormat.format(Date())
        val logMessage = if (exception != null) {
            "[$timestamp] [ERROR] $message: ${exception.message}\n${exception.stackTraceToString()}"
        } else {
            "[$timestamp] [ERROR] $message"
        }

        Log.e(TAG, message, exception)
        writeToFile(logMessage)
    }

    fun logWarning(message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] [WARNING] $message"

        Log.w(TAG, message)
        writeToFile(logMessage)
    }

    fun logDebug(message: String) {
        val timestamp = dateFormat.format(Date())
        val logMessage = "[$timestamp] [DEBUG] $message"

        Log.d(TAG, message)
        writeToFile(logMessage)
    }

    fun logPedidoEvent(eventType: String, pedidoId: Int, details: String = "") {
        val message = "PEDIDO_EVENT | ID: $pedidoId | Tipo: $eventType | $details"
        logInfo(message)
    }

    fun logSyncEvent(eventType: String, details: String) {
        val message = "SYNC_EVENT | Tipo: $eventType | $details"
        logInfo(message)
    }

    private fun writeToFile(message: String) {
        try {
            logFile?.let { file ->
                FileOutputStream(file, true).use { fos ->
                    fos.write((message + "\n").toByteArray())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al escribir en archivo de log", e)
        }
    }

    fun getLogs(): String {
        return try {
            logFile?.readText() ?: "No hay logs disponibles"
        } catch (e: Exception) {
            Log.e(TAG, "Error al leer logs", e)
            "Error al leer logs: ${e.message}"
        }
    }

    fun clearLogs() {
        try {
            logFile?.writeText("")
            logInfo("Logs limpiados")
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar logs", e)
        }
    }
}