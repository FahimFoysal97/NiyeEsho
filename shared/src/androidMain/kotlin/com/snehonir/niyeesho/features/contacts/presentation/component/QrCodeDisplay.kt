package com.snehonir.niyeesho.features.contacts.presentation.component

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

fun generateQrCodeBitmap(content: String, sizePx: Int): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                bitmap[x, y] = color
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
        contentAlignment = Alignment.Center
    ) {
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code for $value",
                modifier = Modifier.size(sizeDp.dp)
            )
        }
    }
}
