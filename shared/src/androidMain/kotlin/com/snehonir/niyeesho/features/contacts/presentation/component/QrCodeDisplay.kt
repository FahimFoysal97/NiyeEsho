package com.snehonir.niyeesho.features.contacts.presentation.component

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurfaceInner
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary

fun generateQrCodeBitmap(
    content: String,
    sizePx: Int,
    cornerRadiusPx: Float = 32f,
): Bitmap? {
    if (content.isBlank()) return null
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Clip outer canvas to rounded corners
        val outerPath = Path().apply {
            addRoundRect(
                RectF(0f, 0f, sizePx.toFloat(), sizePx.toFloat()),
                cornerRadiusPx,
                cornerRadiusPx,
                Path.Direction.CW,
            )
        }
        canvas.clipPath(outerPath)

        // Draw background color
        paint.color = NiyeEshoGreenPrimary.toArgb()
        canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), paint)

        // Draw QR modules (foreground color)
        paint.color = NiyeEshoDarkSurfaceInner.toArgb()
        val moduleWidth = sizePx.toFloat() / width
        val moduleHeight = sizePx.toFloat() / height

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (bitMatrix.get(x, y)) {
                    val left = x * moduleWidth
                    val top = y * moduleHeight
                    val right = left + moduleWidth
                    val bottom = top + moduleHeight
                    canvas.drawRect(left, top, right, bottom, paint)
                }
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
actual fun QrCodeDisplay(
    value: String,
    modifier: Modifier,
) {
    val sizeDp = 200
    val density = LocalDensity.current
    val sizePx = remember(sizeDp) { with(density) { sizeDp.dp.roundToPx() } }
    val qrBitmap = remember(value, sizePx) {
        generateQrCodeBitmap(value, sizePx)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code for $value",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
