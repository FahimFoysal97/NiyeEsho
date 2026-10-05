package com.snehonir.niyeesho.features.contacts.presentation.component

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.io.File
import java.io.FileOutputStream

open class AndroidQrImageHelper(private val context: Context) : QrImageHelper {
    override fun shareQrCodeImage(content: String) {
        if (content.isBlank()) return
        val bitmap = generateQrCodeBitmap(content, 600) ?: return
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "qr_invite.png")
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            if (contentUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, "image/png")
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    type = "image/png"
                }
                val chooser = Intent.createChooser(shareIntent, "Share QR Code Image")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun saveQrCodeImage(content: String) {
        if (content.isBlank()) return
        val bitmap = generateQrCodeBitmap(content, 600) ?: return
        try {
            val filename = "NiyeEsho_QR_${System.currentTimeMillis()}.png"
            var saved = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NiyeEsho")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    resolver.openOutputStream(imageUri).use { stream ->
                        if (stream != null) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            saved = true
                        }
                    }
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                imagesDir.mkdirs()
                val image = File(imagesDir, filename)
                FileOutputStream(image).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    saved = true
                }
            }
            if (saved) {
                Toast.makeText(context, "QR code image saved to Pictures", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun pickAndDecodeQrImage(onResult: (String?) -> Unit) {
        onResult(null)
    }
}

private fun decodeQrFromUri(context: Context, uri: Uri): String? {
    return try {
        val rawBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } ?: return null

        val bitmap = if (rawBitmap.config == Bitmap.Config.HARDWARE) {
            rawBitmap.copy(Bitmap.Config.ARGB_8888, false) ?: rawBitmap
        } else {
            rawBitmap
        }

        val width = bitmap.width
        val height = bitmap.height
        val intArray = IntArray(width * height)
        bitmap.getPixels(intArray, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader().apply {
            setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
        }
        val result = reader.decode(binaryBitmap)
        result.text
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
actual fun rememberQrImageHelper(): QrImageHelper {
    val context = LocalContext.current
    var onQrDecodedCallback by remember { mutableStateOf<((String?) -> Unit)?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val callback = onQrDecodedCallback
        if (uri != null) {
            val decodedResult = decodeQrFromUri(context, uri)
            if (decodedResult != null) {
                callback?.invoke(decodedResult)
            } else {
                Toast.makeText(context, "No valid QR code found in photo", Toast.LENGTH_SHORT).show()
                callback?.invoke(null)
            }
        } else {
            callback?.invoke(null)
        }
    }

    return remember(context, launcher) {
        object : AndroidQrImageHelper(context) {
            override fun pickAndDecodeQrImage(onResult: (String?) -> Unit) {
                onQrDecodedCallback = onResult
                launcher.launch("image/*")
            }
        }
    }
}
