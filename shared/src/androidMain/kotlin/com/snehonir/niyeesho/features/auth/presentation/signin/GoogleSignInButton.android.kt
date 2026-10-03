package com.snehonir.niyeesho.features.auth.presentation.signin

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.snehonir.niyeesho.BuildKonfig

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
                launcher.launch(signInClient.signInIntent)
            }
        },
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        if (!enabled) CircularProgressIndicator()
        else Text("Continue with Google")
    }
}
