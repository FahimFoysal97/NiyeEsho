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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .widthIn(max = 400.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl, vertical = Spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Hero Brand Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xl),
                horizontalAlignment = Alignment.Start,
            ) {
                // Brand Icon Box (56px x 56px, radius 18dp)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NiyeEshoGreenPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "🛍️",
                        fontSize = 28.sp,
                    )
                }

                Spacer(Modifier.height(Spacing.xl))

                Text(
                    text = "নিয়ে\nএসো.",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 40.sp,
                )

                Spacer(Modifier.height(Spacing.md))

                Text(
                    text = UiText.Plain(
                        "Share lists with family and friends in real time.\nYour data stays in your Drive.",
                    ).resolve(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NiyeEshoGreenLight,
                    lineHeight = 22.sp,
                )
            }

            Spacer(Modifier.height(Spacing.xxl))

            // Action & Feature Section
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

                // Google Sign In Button
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
                    text = UiText.Plain("No account needed beyond Google.\nWe store nothing about you.").resolve(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NiyeEshoGreenDarkMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
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
                .padding(vertical = Spacing.sm, horizontal = Spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(text = icon, fontSize = 18.sp)
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = NiyeEshoGreenMuted,
                textAlign = TextAlign.Center,
                fontSize = 9.sp,
                lineHeight = 12.sp,
            )
        }
    }
}
