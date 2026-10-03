package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun GoogleSignInButton(
    enabled: Boolean,
    onResult: (Result<String>) -> Unit,
    modifier: Modifier,
) {
    Button(
        onClick = {
            onResult(
                Result.failure(
                    UnsupportedOperationException(
                        "Configure the GoogleSignIn iOS SDK and connect its callback to this bridge.",
                    ),
                ),
            )
        },
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text("Continue with Google")
    }
}
