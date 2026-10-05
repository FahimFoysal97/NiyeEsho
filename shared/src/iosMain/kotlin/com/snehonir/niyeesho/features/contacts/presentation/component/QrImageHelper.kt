package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class IosQrImageHelper : QrImageHelper {
    override fun shareQrCodeImage(content: String) {}
    override fun saveQrCodeImage(content: String) {}
    override fun pickAndDecodeQrImage(onResult: (String?) -> Unit) {}
}

@Composable
actual fun rememberQrImageHelper(): QrImageHelper {
    return remember { IosQrImageHelper() }
}
