package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoAccentSoft
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGradientBottom
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGradientTop
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun SignInContent(
    uiState: SignInUiState,
    onAction: (SignInUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val heroGradient = Brush.verticalGradient(
        colors = listOf(NiyeEshoGradientTop, NiyeEshoGradientBottom),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(heroGradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BrandHeader()

            Spacer(Modifier.height(Spacing.xl))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = Spacing.xxxl * 7),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = Spacing.md),
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = UiText.Plain("Welcome back").resolve(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = UiText.Plain("Sign in to sync your shared lists with family and friends.").resolve(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    uiState.error?.let { error ->
                        ErrorView(message = error, modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(Modifier.height(Spacing.xs))

                    GoogleSignInButton(
                        enabled = !uiState.isLoading,
                        onResult = { result ->
                            result.fold(
                                onSuccess = { token ->
                                    onAction(SignInUiAction.GoogleSignInClicked(token))
                                },
                                onFailure = { error ->
                                    onAction(SignInUiAction.GoogleSignInFailed(error))
                                },
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Text(
                        text = UiText.Plain(
                            "We only use your Google account to identify you. Your lists stay in your own Drive.",
                        ).resolve(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = UiText.Plain("Bari ferar age, shob mone koriye dei.").resolve(),
                style = MaterialTheme.typography.bodyMedium,
                color = NiyeEshoAccentSoft,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun BrandHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(Spacing.xxxl + Spacing.md)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\uD83D\uDED2",
                style = MaterialTheme.typography.displaySmall,
            )
        }
        Text(
            text = UiText.Plain("Niye Esho").resolve(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = UiText.Plain("Shared shopping lists, remembered for you.").resolve(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
        )
    }
}
