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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoButtonGradientEnd
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoButtonGradientStart
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoEmeraldAccent
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenLight
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoLightTeal
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoSoftGreen
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoTextMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoTextSubtle

@Composable
fun SignInContent(
    uiState: SignInUiState,
    onAction: (SignInUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(NiyeEshoGreenLight, NiyeEshoEmeraldAccent),
    )

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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Upper Logo & Hero Title Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Logo Icon Box (60px x 60px, rounded 20dp, gradient background)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(AppShapes.surfaceLarge)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NiyeEshoButtonGradientStart, NiyeEshoButtonGradientEnd),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Niye Esho Logo",
                        tint = NiyeEshoLightTeal,
                        modifier = Modifier.size(30.dp),
                    )
                }

                Spacer(Modifier.height(28.dp))

                // h1 Bengali Headline: নিয়ে \n এসো। (with gradient on "এসো।")
                Text(
                    text = "নিয়ে",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 56.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "এসো।",
                    style = TextStyle(
                        brush = gradientBrush,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 56.sp,
                        textAlign = TextAlign.Center,
                    ),
                )

                Spacer(Modifier.height(16.dp))

                // Tagline with bold emphasis on "Your data stays in your Drive."
                val tagText = buildAnnotatedString {
                    append("Share lists with family and friends in real time. ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                        append("Your data stays in your Drive.")
                    }
                }

                Text(
                    text = tagText,
                    fontSize = 15.sp,
                    color = NiyeEshoSoftGreen,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                )
            }

            Spacer(Modifier.height(24.dp))

            // Lower Action & Features Container
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                uiState.error?.let { error ->
                    ErrorView(message = error, modifier = Modifier.fillMaxWidth())
                }

                // 3 Feature Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RefinedFeatureCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Saved to",
                        subtitle = "your Drive",
                        modifier = Modifier.weight(1f),
                    )
                    RefinedFeatureCard(
                        icon = Icons.Default.Star,
                        title = "Real-time",
                        subtitle = "sync",
                        modifier = Modifier.weight(1f),
                    )
                    RefinedFeatureCard(
                        icon = Icons.Default.Lock,
                        title = "Private",
                        subtitle = "P2P",
                        modifier = Modifier.weight(1f),
                    )
                }

                // Google Button
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

                // Fine print footer note
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "No account needed beyond Google. We store nothing about you.",
                        fontSize = 11.5.sp,
                        color = NiyeEshoTextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun RefinedFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = AppShapes.card,
        color = NiyeEshoDarkSurface.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NiyeEshoEmeraldAccent,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "$title\n$subtitle",
                style = MaterialTheme.typography.labelSmall,
                color = NiyeEshoTextSubtle,
                textAlign = TextAlign.Center,
                fontSize = 10.5.sp,
                lineHeight = 14.sp,
            )
        }
    }
}
