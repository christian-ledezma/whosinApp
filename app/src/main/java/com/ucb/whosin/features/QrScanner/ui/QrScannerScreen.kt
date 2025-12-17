package com.ucb.whosin.features.qrscanner.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QrScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        val options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                        val scanner = BarcodeScanning.getClient(options)

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            val image = imageProxy.image
                            if (image != null) {
                                val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                                scanner.process(inputImage)
                                    .addOnSuccessListener {
                                        if (it.isNotEmpty()) {
                                            val qrContent = it[0].rawValue
                                            if (qrContent != null) {
                                                // Set result and navigate back
                                                navController.previousBackStackEntry
                                                    ?.savedStateHandle
                                                    ?.set("qr_code_result", qrContent)
                                                navController.popBackStack()
                                            }
                                        }
                                    }
                                    .addOnFailureListener { 
                                        // Handle failure
                                     }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            }
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            QrScannerOverlay()
        } else {
            Text(text = "Camera permission is required", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun QrScannerOverlay() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo oscuro semi-transparente con recorte
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Tamaño del recuadro de escaneo
            val scanSize = canvasWidth * 0.7f
            val left = (canvasWidth - scanSize) / 2
            val top = (canvasHeight - scanSize) / 2

            // Dibujar fondo oscuro
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )

            // Recortar el área de escaneo (hacer transparente)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(scanSize, scanSize),
                cornerRadius = CornerRadius(16.dp.toPx()),
                blendMode = BlendMode.Clear
            )

            // Dibujar esquinas decorativas (opcional)
            val cornerLength = 40.dp.toPx()
            val cornerWidth = 6.dp.toPx()

            // Esquina superior izquierda
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left, top + cornerLength),
                end = Offset(left, top),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left, top),
                end = Offset(left + cornerLength, top),
                strokeWidth = cornerWidth
            )

            // Esquina superior derecha
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left + scanSize, top + cornerLength),
                end = Offset(left + scanSize, top),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left + scanSize, top),
                end = Offset(left + scanSize - cornerLength, top),
                strokeWidth = cornerWidth
            )

            // Esquina inferior izquierda
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left, top + scanSize - cornerLength),
                end = Offset(left, top + scanSize),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left, top + scanSize),
                end = Offset(left + cornerLength, top + scanSize),
                strokeWidth = cornerWidth
            )

            // Esquina inferior derecha
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left + scanSize, top + scanSize - cornerLength),
                end = Offset(left + scanSize, top + scanSize),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color(0xFF9EFF00),
                start = Offset(left + scanSize, top + scanSize),
                end = Offset(left + scanSize - cornerLength, top + scanSize),
                strokeWidth = cornerWidth
            )
        }

        // Instrucciones
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Escanear Código QR",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "El código QR de estar dentro del recuadro",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}