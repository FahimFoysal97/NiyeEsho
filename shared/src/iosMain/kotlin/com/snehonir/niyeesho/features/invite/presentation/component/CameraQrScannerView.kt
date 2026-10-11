package com.snehonir.niyeesho.features.invite.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun CameraQrScannerView(
    onQrCodeScanned: (String) -> Unit,
    isScanningEnabled: Boolean,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.background(Color(0xFF0A120D)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Camera preview",
            color = Color(0xFF7F9D8E),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
