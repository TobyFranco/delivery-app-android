package com.example.deliveryapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.deliveryapp.data.local.entity.PedidoEntity
import com.example.deliveryapp.databinding.ActivityAgregarPedidoBinding
import com.example.deliveryapp.util.ImageUtils
import com.example.deliveryapp.util.LogUtils
import com.example.deliveryapp.viewmodel.PedidoViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AgregarPedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarPedidoBinding
    private val viewModel: PedidoViewModel by viewModels()
    private var imagenPath: String? = null

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isCameraOpen = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        LogUtils.logInfo("AgregarPedidoActivity iniciada")

        title = "Nuevo Pedido"

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.btnTomarFoto.setOnClickListener {
            if (isCameraOpen) {
                capturarFoto()
            } else {
                if (allPermissionsGranted()) {
                    abrirCamara()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        binding.btnGuardar.setOnClickListener {
            guardarPedido()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.operationStatus.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(this, "Pedido guardado exitosamente", Toast.LENGTH_SHORT).show()
                    LogUtils.logInfo("Pedido guardado desde AgregarPedidoActivity")
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Error: ${it.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun guardarPedido() {
        val nombre = binding.etNombre.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val montoStr = binding.etMonto.text.toString().trim()

        binding.tilNombre.error = null
        binding.tilTelefono.error = null
        binding.tilDireccion.error = null
        binding.tilDescripcion.error = null
        binding.tilMonto.error = null

        if (nombre.isEmpty()) {
            binding.tilNombre.error = "Campo requerido"
            return
        }
        if (telefono.isEmpty()) {
            binding.tilTelefono.error = "Campo requerido"
            return
        }
        if (direccion.isEmpty()) {
            binding.tilDireccion.error = "Campo requerido"
            return
        }
        if (descripcion.isEmpty()) {
            binding.tilDescripcion.error = "Campo requerido"
            return
        }
        if (montoStr.isEmpty()) {
            binding.tilMonto.error = "Campo requerido"
            return
        }

        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            binding.tilMonto.error = "Monto inválido"
            return
        }

        val pedido = PedidoEntity(
            nombreCliente = nombre,
            telefonoCliente = telefono,
            direccionCliente = direccion,
            emailCliente = email.ifEmpty { null },
            descripcionPedido = descripcion,
            montoPedido = monto,
            estadoPedido = "Pendiente",
            imagenPath = imagenPath,
            sincronizado = false,
            fechaCreacion = System.currentTimeMillis(),
            fechaModificacion = System.currentTimeMillis()
        )

        LogUtils.logPedidoEvent("CREACION", 0, "Nuevo pedido para $nombre")
        viewModel.insertPedido(pedido)
    }

    private fun abrirCamara() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.previewView.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                isCameraOpen = true
                binding.btnTomarFoto.text = "📸 Capturar Foto"

                // Ocultar el preview de imagen y mostrar la cámara
                binding.ivPreviewImagen.visibility = android.view.View.GONE
                binding.previewView.visibility = android.view.View.VISIBLE

                Toast.makeText(this, "Cámara lista. Presiona el botón para capturar", Toast.LENGTH_SHORT).show()
                LogUtils.logInfo("Cámara inicializada correctamente")

            } catch (e: Exception) {
                LogUtils.logError("Error al iniciar cámara", e)
                Toast.makeText(this, "Error al abrir cámara: ${e.message}", Toast.LENGTH_LONG).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturarFoto() {
        val imageCapture = imageCapture ?: run {
            Toast.makeText(this, "Error: Cámara no inicializada", Toast.LENGTH_SHORT).show()
            return
        }

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        val bitmap = image.toBitmap()
                        imagenPath = ImageUtils.saveBitmapToInternalStorage(
                            this@AgregarPedidoActivity,
                            bitmap
                        )

                        // Cerrar cámara y mostrar preview
                        cerrarCamara()
                        binding.previewView.visibility = android.view.View.GONE
                        binding.ivPreviewImagen.visibility = android.view.View.VISIBLE
                        binding.ivPreviewImagen.setImageBitmap(bitmap)

                        binding.btnTomarFoto.text = "🔄 Tomar Otra Foto"

                        Toast.makeText(
                            this@AgregarPedidoActivity,
                            "✅ Foto capturada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        LogUtils.logInfo("Foto capturada y guardada: $imagenPath")

                    } catch (e: Exception) {
                        LogUtils.logError("Error al procesar foto", e)
                        Toast.makeText(
                            this@AgregarPedidoActivity,
                            "Error al guardar foto",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        image.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    LogUtils.logError("Error al capturar imagen", exception)
                    Toast.makeText(
                        this@AgregarPedidoActivity,
                        "Error al capturar: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    private fun cerrarCamara() {
        try {
            val cameraProvider = ProcessCameraProvider.getInstance(this).get()
            cameraProvider.unbindAll()
            isCameraOpen = false
        } catch (e: Exception) {
            LogUtils.logError("Error al cerrar cámara", e)
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cerrarCamara()
        cameraExecutor.shutdown()
    }
}