package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun QrScannerSheet(
    onInviteScanned: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = UiText.Resource("niyeesho_qr_scanner_unavailable").resolve(),
        modifier = modifier,
    )
}

