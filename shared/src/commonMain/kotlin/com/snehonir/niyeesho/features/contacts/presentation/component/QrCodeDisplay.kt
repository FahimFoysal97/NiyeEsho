package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun QrCodeDisplay(
    value: String,
    modifier: Modifier = Modifier,
)
