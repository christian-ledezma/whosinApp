package com.ucb.whosin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrCodeGenerator {

    fun generateQrCode(
        content: String,
        size: Int = 512,
        primaryColor: Int = Color.parseColor("#003D3D"), // DarkTeal
        backgroundColor: Int = Color.WHITE,
        logo: Bitmap? = null
    ): Bitmap {
        val hints = hashMapOf<EncodeHintType, Any>().apply {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H) // Alta corrección para permitir logo
            put(EncodeHintType.MARGIN, 1) // Margen mínimo
        }

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fondo
        canvas.drawColor(backgroundColor)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            style = Paint.Style.FILL
        }

        // Tamaño de cada módulo (pixel del QR)
        val pixelSize = size.toFloat() / bitMatrix.width
        val cornerRadius = pixelSize * 0.45f // Radio de esquinas redondeadas

        // Dibujar módulos del QR con esquinas redondeadas (estilo de tu imagen)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                if (bitMatrix[x, y]) {
                    val left = x * pixelSize
                    val top = y * pixelSize
                    val rect = RectF(left, top, left + pixelSize, top + pixelSize)
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                }
            }
        }

        // Agregar logo en el centro si se proporciona
        logo?.let {
            val logoSize = (size * 0.2f).toInt() // Logo ocupa 20% del QR
            val logoX = (size - logoSize) / 2f
            val logoY = (size - logoSize) / 2f

            // Fondo blanco para el logo
            val logoPadding = 8f
            val logoBgRect = RectF(
                logoX - logoPadding,
                logoY - logoPadding,
                logoX + logoSize + logoPadding,
                logoY + logoSize + logoPadding
            )
            paint.color = Color.WHITE
            canvas.drawRoundRect(logoBgRect, 12f, 12f, paint)

            // Dibujar logo escalado
            val scaledLogo = Bitmap.createScaledBitmap(it, logoSize, logoSize, true)
            canvas.drawBitmap(scaledLogo, logoX, logoY, null)
        }

        return bitmap
    }

    // Convierte un Drawable (incluyendo adaptive icons) a Bitmap del tamaño indicado
    private fun drawableToBitmap(drawable: Drawable, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return bitmap
    }

    // Obtiene el icono de la app como Bitmap
    fun getAppIconBitmap(context: Context, size: Int): Bitmap {
        val pm = context.packageManager
        val drawable = pm.getApplicationIcon(context.applicationInfo)
        return drawableToBitmap(drawable, size)
    }

    // Sobrecarga que genera el QR usando el icono de la app como logo
    fun generateQrCodeWithAppIcon(
        context: Context,
        content: String,
        size: Int = 512,
        primaryColor: Int = Color.parseColor("#003D3D"),
        backgroundColor: Int = Color.WHITE
    ): Bitmap {
        val logoSize = (size * 0.2f).toInt()
        val appIcon = getAppIconBitmap(context, logoSize)
        return generateQrCode(content, size, primaryColor, backgroundColor, appIcon)
    }

    fun generateSimpleQrCode(
        content: String,
        size: Int = 512,
        primaryColor: Int = Color.parseColor("#003D3D"),
        backgroundColor: Int = Color.WHITE
    ): Bitmap {
        return generateQrCode(content, size, primaryColor, backgroundColor, null)
    }
}