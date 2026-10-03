package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun SignInContent(
    uiState: SignInUiState,
    onAction: (SignInUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(Spacing.lg), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Card(
            modifier = Modifier.widthIn(max = Spacing.xxxl * 14),
            shape = androidx.compose.material3.MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.sm),
        ) {
            Column(
                modifier = Modifier.padding(Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text(
                    UiText.Resource("niyeesho_sign_in_title").resolve(),
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                )
                Text(
                    UiText.Resource("niyeesho_sign_in_subtitle").resolve(),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
                uiState.error?.let { ErrorView(message = it, modifier = Modifier.fillMaxWidth()) }
                GoogleSignInButton(
                    enabled = !uiState.isLoading,
                    onResult = { result ->
                        result.fold(
                            onSuccess = { token -> onAction(SignInUiAction.GoogleSignInClicked(token)) },
                            onFailure = { error -> onAction(SignInUiAction.GoogleSignInFailed(error)) },
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
