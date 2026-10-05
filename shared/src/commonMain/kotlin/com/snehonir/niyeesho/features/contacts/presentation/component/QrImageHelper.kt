package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.runtime.Composable

@Composable
expect fun rememberQrImageHelper(): QrImageHelper

interface QrImageHelper {
    fun shareQrCodeImage(content: String)
    fun saveQrCodeImage(content: String)
    fun pickAndDecodeQrImage(onResult: (String?) -> Unit)
}
