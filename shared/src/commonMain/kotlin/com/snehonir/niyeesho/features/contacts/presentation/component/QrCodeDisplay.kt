package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun QrCodeDisplay(
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = UiText.Resource("niyeesho_qr_code").resolve() + "\n$value",
        modifier = modifier.border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline)
            .padding(16.dp),
    )
}
