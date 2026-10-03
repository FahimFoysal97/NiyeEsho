package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.snehonir.niyeesho.BuildKonfig
import com.snehonir.niyeesho.core.designsystem.theme.Spacing

@Composable
actual fun GoogleSignInButton(
    enabled: Boolean,
    onResult: (Result<String>) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val signInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildKonfig.GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }
    val signInClient = remember(signInOptions) {
        GoogleSignIn.getClient(context, signInOptions)
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        runCatching {
            task.getResult(ApiException::class.java)?.idToken
                ?: error("Google did not return an ID token.")
        }.fold(
            onSuccess = { token -> onResult(Result.success(token)) },
            onFailure = { error -> onResult(Result.failure(error)) },
        )
    }

    Button(
        onClick = {
            if (BuildKonfig.GOOGLE_WEB_CLIENT_ID.isBlank()) {
                onResult(
                    Result.failure(
                        IllegalStateException(
                            "Missing googleWebClientId Gradle property.",
                        ),
                    ),
                )
            } else {
                signInClient.signOut().addOnCompleteListener {
                    launcher.launch(signInClient.signInIntent)
                }
            }
        },
        enabled = enabled,
        modifier = modifier.height(Spacing.xxl + Spacing.xs),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F),
            disabledContainerColor = Color.White.copy(alpha = 0.6f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Spacing.xs / 2,
            pressedElevation = Spacing.xs,
        ),
    ) {
        if (!enabled) {
            CircularProgressIndicator(
                modifier = Modifier.size(Spacing.lg),
                strokeWidth = Spacing.xs / 2,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                GoogleGlyph()
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun GoogleGlyph(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(Spacing.lg)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "G",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF4285F4),
        )
    }
}
