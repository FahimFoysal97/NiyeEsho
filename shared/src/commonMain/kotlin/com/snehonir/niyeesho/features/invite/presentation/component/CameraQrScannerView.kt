package com.snehonir.niyeesho.features.invite.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraQrScannerView(
    onQrCodeScanned: (String) -> Unit,
    isScanningEnabled: Boolean = true,
    modifier: Modifier = Modifier,
)
