package com.snehonir.niyeesho.features.auth.presentation.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenLight
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun SignInContent(
    uiState: SignInUiState,
    onAction: (SignInUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NiyeEshoDarkBg),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 440.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl, vertical = Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Brand Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.lg),
                horizontalAlignment = Alignment.Start,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NiyeEshoGreenPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "🛍️",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }

                Spacer(Modifier.height(Spacing.xl))

                Text(
                    text = "নিয়ে\nএসো।",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Spacer(Modifier.height(Spacing.md))

                Text(
                    text = UiText.Plain(
                        "Share lists with family and friends in real time. Your data stays in your Drive.",
                    ).resolve(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NiyeEshoGreenLight,
                )
            }

            Spacer(Modifier.height(Spacing.xxl))

            // Lower Action & Features Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                uiState.error?.let { error ->
                    ErrorView(message = error, modifier = Modifier.fillMaxWidth())
                }

                // 3 Feature Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    FeatureCard(
                        icon = "☁️",
                        text = "Saved to\nyour Drive",
                        modifier = Modifier.weight(1f),
                    )
                    FeatureCard(
                        icon = "⚡",
                        text = "Real-time\nsync",
                        modifier = Modifier.weight(1f),
                    )
                    FeatureCard(
                        icon = "🔒",
                        text = "Private\nP2P",
                        modifier = Modifier.weight(1f),
                    )
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
                    text = "No account needed beyond Google. We store nothing about you.",
                    style = MaterialTheme.typography.labelMedium,
                    color = NiyeEshoGreenDarkMuted,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = NiyeEshoDarkSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md, horizontal = Spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(text = icon, style = MaterialTheme.typography.titleLarge)
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = NiyeEshoGreenMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}
