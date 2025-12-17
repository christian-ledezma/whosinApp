package com.ucb.whosin.features.Guest.presentation

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ucb.whosin.ui.components.WhosInPrimaryButton
import com.ucb.whosin.ui.theme.WhosInColors
import com.ucb.whosin.utils.QrCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun QrCodeDialog(
    qrCode: String,
    eventName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isGenerating by remember { mutableStateOf(true) }
    var downloadStatus by remember { mutableStateOf<String?>(null) }

    // Generar el QR al abrir el dialog
    LaunchedEffect(qrCode) {
        scope.launch(Dispatchers.Default) {
            try {
                val bitmap = QrCodeGenerator.generateSimpleQrCode(
                    content = qrCode,
                    size = 512,
                    primaryColor = android.graphics.Color.parseColor("#003D3D"), // DarkTeal
                    backgroundColor = android.graphics.Color.WHITE
                )
                withContext(Dispatchers.Main) {
                    qrBitmap = bitmap
                    isGenerating = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isGenerating = false
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = WhosInColors.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tu Código QR",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = WhosInColors.DarkTeal
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = WhosInColors.GrayBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = eventName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhosInColors.GrayBlue,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // QR Code
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isGenerating -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = WhosInColors.LimeGreen,
                                strokeWidth = 4.dp
                            )
                        }
                        qrBitmap != null -> {
                            Image(
                                bitmap = qrBitmap!!.asImageBitmap(),
                                contentDescription = "Código QR",
                                modifier = Modifier.size(260.dp)
                            )
                        }
                        else -> {
                            Text(
                                text = "Error al generar QR",
                                color = WhosInColors.GrayBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Advertencia de seguridad
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = WhosInColors.Warning.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = WhosInColors.Warning,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "Código Personal",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = WhosInColors.Warning
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Este código es único y personal. No lo compartas con nadie más. Solo muéstralo al personal del evento.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WhosInColors.GrayBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón de descarga
                WhosInPrimaryButton(
                    text = "Descargar QR",
                    onClick = {
                        qrBitmap?.let { bitmap ->
                            scope.launch {
                                val result = saveQrCodeToGallery(context, bitmap, eventName)
                                downloadStatus = if (result) {
                                    "✅ QR guardado en Galería"
                                } else {
                                    "❌ Error al guardar"
                                }
                            }
                        }
                    },
                    enabled = qrBitmap != null && !isGenerating,
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensaje de estado de descarga
                downloadStatus?.let { status ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (status.startsWith("✅"))
                            WhosInColors.Success
                        else
                            WhosInColors.Warning,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private suspend fun saveQrCodeToGallery(
    context: Context,
    bitmap: Bitmap,
    eventName: String
): Boolean = withContext(Dispatchers.IO) {
    try {
        val filename = "WhosIn_QR_${eventName.replace(" ", "_")}_${System.currentTimeMillis()}.png"

        val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WhosIn")
            }
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let { resolver.openOutputStream(it) }
        } else {
            // Android 9 y anteriores
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val whosInDir = File(imagesDir, "WhosIn")
            if (!whosInDir.exists()) whosInDir.mkdirs()

            val image = File(whosInDir, filename)
            FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}