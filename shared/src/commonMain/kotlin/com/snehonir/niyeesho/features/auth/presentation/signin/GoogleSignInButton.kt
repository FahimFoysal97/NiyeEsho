package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect @Composable
fun GoogleSignInButton(
    enabled: Boolean,
    onResult: (Result<String>) -> Unit,
    modifier: Modifier = Modifier,
)
