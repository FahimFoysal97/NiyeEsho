package com.snehonir.niyeesho.features.invite.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snehonir.niyeesho.core.designsystem.component.AppBottomBar
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.InviteGradientBottom
import com.snehonir.niyeesho.core.designsystem.theme.InviteGradientTop
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoBorderSubtle
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoBrightRed
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurfaceInner
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurfaceVariant
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoEmeraldAccent
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoIndigoTertiary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoMutedGreenText
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoSoftRed
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoTextMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoTextSubtle
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.presentation.component.QrCodeDisplay
import com.snehonir.niyeesho.features.contacts.presentation.component.rememberQrImageHelper
import com.snehonir.niyeesho.features.invite.presentation.component.CameraQrScannerView
import niyeesho.shared.generated.resources.Res
import niyeesho.shared.generated.resources.content_copy
import niyeesho.shared.generated.resources.content_paste
import niyeesho.shared.generated.resources.download
import niyeesho.shared.generated.resources.scan
import org.jetbrains.compose.resources.painterResource

@Composable
fun InviteContent(
    uiState: InviteUiState,
    onAction: (InviteUiAction) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToLists: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val qrImageHelper = rememberQrImageHelper()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NiyeEshoDarkBg),
    ) {
        // Header & Sub-Tabs ("My code" vs "Scan")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = AppStrings.TITLE_INVITE,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            // Sub-Tabs (.tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NiyeEshoDarkSurfaceVariant, RoundedCornerShape(13.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (uiState.selectedSubTab == 0) NiyeEshoGreenPrimary else Color.Transparent)
                        .clickable { onAction(InviteUiAction.SubTabSelected(0)) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = AppStrings.TAB_MY_CODE,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.selectedSubTab == 0) Color.White else NiyeEshoTextMuted,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (uiState.selectedSubTab == 1) NiyeEshoGreenPrimary else Color.Transparent)
                        .clickable { onAction(InviteUiAction.SubTabSelected(1)) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = AppStrings.TAB_SCAN,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.selectedSubTab == 1) Color.White else NiyeEshoTextMuted,
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.selectedSubTab == 0) {
                // Sub-Tab 0: My code (QR on top-start with Save & Share on top-end; Link on bottom-start with Copy & Share on bottom-end)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    // Card 1: YOUR QR CODE
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurfaceInner),
                        border = BorderStroke(1.5.dp, NiyeEshoGreenPrimary),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = AppStrings.TITLE_YOUR_QR_CODE,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoGreenPrimary,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(bottom = 10.dp),
                            )

                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                QrCodeDisplay(
                                    value = uiState.inviteLink,
                                    sizeDp = maxWidth.value.toInt(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Surface(
                                    onClick = { qrImageHelper.saveQrCodeImage(uiState.inviteLink) },
                                    shape = RoundedCornerShape(11.dp),
                                    color = NiyeEshoDarkSurface,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(painterResource(Res.drawable.download), contentDescription = AppStrings.ACTION_SAVE, tint = NiyeEshoEmeraldAccent, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(AppStrings.ACTION_SAVE, style = MaterialTheme.typography.labelMedium, color = NiyeEshoMutedGreenText, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    onClick = { qrImageHelper.shareQrCodeImage(uiState.inviteLink) },
                                    shape = RoundedCornerShape(11.dp),
                                    color = NiyeEshoDarkSurface,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = AppStrings.ACTION_SHARE, tint = NiyeEshoEmeraldAccent, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(AppStrings.ACTION_SHARE, style = MaterialTheme.typography.labelMedium, color = NiyeEshoMutedGreenText, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Card 2: YOUR INVITE LINK
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurfaceInner),
                        border = BorderStroke(1.5.dp, NiyeEshoGreenPrimary),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                        ) {
                            Text(
                                text = AppStrings.TITLE_YOUR_INVITE_LINK,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoGreenPrimary,
                                modifier = Modifier.padding(bottom = 10.dp),
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(NiyeEshoDarkBg)
                                    .border(1.dp, NiyeEshoDarkSurface, RoundedCornerShape(11.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        pushStyle(SpanStyle(color = NiyeEshoTextSubtle, fontWeight = FontWeight.Bold))
                                        append(uiState.inviteLink)
                                        pop()
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NiyeEshoMutedGreenText,
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Surface(
                                    onClick = { clipboardManager.setText(AnnotatedString(uiState.inviteLink)) },
                                    shape = RoundedCornerShape(11.dp),
                                    color = NiyeEshoDarkSurface,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(painterResource(Res.drawable.content_copy), contentDescription = AppStrings.ACTION_COPY, tint = NiyeEshoEmeraldAccent, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(AppStrings.ACTION_COPY, style = MaterialTheme.typography.labelMedium, color = NiyeEshoMutedGreenText, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    onClick = { onAction(InviteUiAction.ShareLink) },
                                    shape = RoundedCornerShape(11.dp),
                                    color = NiyeEshoDarkSurface,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = AppStrings.ACTION_SHARE, tint = NiyeEshoEmeraldAccent, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(AppStrings.ACTION_SHARE, style = MaterialTheme.typography.labelMedium, color = NiyeEshoMutedGreenText, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Sub-Tab 1: Scan
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    when (uiState.scannerStep) {
                        ScannerStep.SCAN -> {
                            if (uiState.isCameraActive) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(InviteGradientTop, InviteGradientBottom)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CameraQrScannerView(
                                        isScanningEnabled = true,
                                        onQrCodeScanned = { scannedLink ->
                                            if (scannedLink.isNotBlank()) {
                                                onAction(InviteUiAction.PastedLinkClicked(scannedLink))
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                    )

                                    QrScannerOverlay(
                                        modifier = Modifier.size(240.dp),
                                    )

                                    // Stop Camera button
                                    Button(
                                        onClick = { onAction(InviteUiAction.StopCameraClicked) },
                                        shape = AppShapes.button,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NiyeEshoDarkBg.copy(alpha = 0.85f),
                                            contentColor = Color.White,
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(Spacing.sm),
                                    ) {
                                        Text(AppStrings.ACTION_CLOSE_CAMERA, style = MaterialTheme.typography.labelSmall)
                                    }

                                    // Choose from photos badge inside camera view
                                    Surface(
                                        onClick = {
                                            qrImageHelper.pickAndDecodeQrImage { decodedLink ->
                                                if (!decodedLink.isNullOrBlank()) {
                                                    onAction(InviteUiAction.PastedLinkClicked(decodedLink))
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        color = NiyeEshoDarkBg.copy(alpha = 0.85f),
                                        border = BorderStroke(1.dp, NiyeEshoBorderSubtle),
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 16.dp),
                                    ) {
                                        Text(
                                            text = AppStrings.ACTION_CHOOSE_PHOTOS,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NiyeEshoMutedGreenText,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        )
                                    }
                                }
                            } else {
                                // Camera Scanner Off Card with Start Button
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    shape = AppShapes.cardLarge,
                                    colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(Spacing.lg),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = NiyeEshoDarkBg,
                                            modifier = Modifier.size(72.dp),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    painter = painterResource(Res.drawable.scan),
                                                    contentDescription = null,
                                                    tint = NiyeEshoGreenPrimary,
                                                    modifier = Modifier.size(36.dp),
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(Spacing.md))

                                        Text(
                                            text = AppStrings.LABEL_CAMERA_OFF_TITLE,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                        )

                                        Spacer(Modifier.height(Spacing.xs))

                                        Text(
                                            text = AppStrings.LABEL_CAMERA_OFF_SUBTITLE,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NiyeEshoGreenMuted,
                                            textAlign = TextAlign.Center,
                                        )

                                        Spacer(Modifier.height(Spacing.lg))

                                        Button(
                                            onClick = { onAction(InviteUiAction.StartCameraClicked) },
                                            shape = AppShapes.button,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = NiyeEshoGreenPrimary,
                                                contentColor = Color.White,
                                            ),
                                            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm),
                                        ) {
                                            Text(
                                                text = AppStrings.ACTION_START_CAMERA_SCANNER,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = AppStrings.HINT_POINT_CAMERA,
                                style = MaterialTheme.typography.bodySmall,
                                color = NiyeEshoTextSubtle,
                                textAlign = TextAlign.Center,
                            )

                            // Divider "or paste invite link"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f), color = NiyeEshoDarkSurface)
                                Text(AppStrings.HINT_PASTE_INVITE_LINK, style = MaterialTheme.typography.labelSmall, color = NiyeEshoTextMuted)
                                HorizontalDivider(modifier = Modifier.weight(1f), color = NiyeEshoDarkSurface)
                            }

                            // Read-only link box (.lf) with Paste button on the right
                            val hasLink = uiState.pastedLink.isNotBlank()
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = NiyeEshoDarkSurfaceInner,
                                border = BorderStroke(1.5.dp, if (hasLink) NiyeEshoGreenPrimary else NiyeEshoBorderSubtle),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 14.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = if (hasLink) uiState.pastedLink else "niyeesho.app/i/...",
                                        color = if (hasLink) Color.White else NiyeEshoGreenDarkMuted,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f),
                                    )

                                    Surface(
                                        onClick = {
                                            val clipboardText = clipboardManager.getText()?.text.orEmpty()
                                            onAction(InviteUiAction.PastedLinkClicked(clipboardText))
                                        },
                                        shape = RoundedCornerShape(9.dp),
                                        color = NiyeEshoDarkSurface,
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(8.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(painterResource(Res.drawable.content_paste), contentDescription = AppStrings.ACTION_PASTE, modifier = Modifier.size(18.dp), tint = NiyeEshoEmeraldAccent)
                                        }
                                    }
                                }
                            }

                            val isValidLink = uiState.pastedLink.isNotBlank() && uiState.pastedLink.contains("niyeesho")
                            if (uiState.pastedLink.isBlank()) {
                                Text(
                                    text = ".",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Transparent,
                                )
                            } else {
                                Text(
                                    text = if (isValidLink) AppStrings.MSG_LINK_VALID else AppStrings.MSG_LINK_INVALID,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isValidLink) NiyeEshoEmeraldAccent else NiyeEshoSoftRed,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }

                            Button(
                                onClick = { if (isValidLink && !uiState.isConnecting) onAction(InviteUiAction.ConnectInviteClicked) },
                                enabled = isValidLink && !uiState.isConnecting,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isValidLink) NiyeEshoGreenPrimary else NiyeEshoDarkSurface,
                                    contentColor = if (isValidLink) Color.White else NiyeEshoGreenDarkMuted,
                                    disabledContainerColor = if (uiState.isConnecting) NiyeEshoGreenPrimary.copy(alpha = 0.7f) else NiyeEshoDarkSurface,
                                    disabledContentColor = Color.White,
                                ),
                                contentPadding = PaddingValues(12.dp),
                            ) {
                                if (uiState.isConnecting) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp,
                                        )
                                        Text(
                                            text = AppStrings.LABEL_CONNECTING,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                } else {
                                    Text(
                                        text = AppStrings.ACTION_CONNECT,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }

                        ScannerStep.CONNECTED -> {
                            ConnectedSuccessView(uiState = uiState)
                        }

                        ScannerStep.FAILED -> {
                            ConnectionFailedView(
                                uiState = uiState,
                                onTryAgain = { onAction(InviteUiAction.SwitchToScan) },
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        AppBottomBar(
            currentTab = AppNavTab.INVITE,
            onTabSelected = { tab ->
                when (tab) {
                    AppNavTab.LISTS -> onNavigateToLists()
                    AppNavTab.CONTACTS -> onNavigateToContacts()
                    AppNavTab.PROFILE -> onNavigateToProfile()
                    AppNavTab.INVITE -> Unit
                }
            },
        )
    }
}

@Composable
fun QrScannerOverlay(
    modifier: Modifier = Modifier,
    cornerLength: Dp = 34.dp,
    cornerRadius: Dp = 14.dp,
    strokeWidth: Dp = 3.5.dp,
    color: Color = NiyeEshoEmeraldAccent,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ScannerLineAnimation")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ScanProgress",
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val armLen = cornerLength.toPx()
        val radius = cornerRadius.toPx()
        val stroke = strokeWidth.toPx()

        // 1. Top-Left Corner
        val topLeftPath = Path().apply {
            moveTo(0f, armLen)
            lineTo(0f, radius)
            quadraticTo(0f, 0f, radius, 0f)
            lineTo(armLen, 0f)
        }
        drawPath(topLeftPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // 2. Top-Right Corner
        val topRightPath = Path().apply {
            moveTo(width - armLen, 0f)
            lineTo(width - radius, 0f)
            quadraticTo(width, 0f, width, radius)
            lineTo(width, armLen)
        }
        drawPath(topRightPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // 3. Bottom-Left Corner
        val bottomLeftPath = Path().apply {
            moveTo(0f, height - armLen)
            lineTo(0f, height - radius)
            quadraticTo(0f, height, radius, height)
            lineTo(armLen, height)
        }
        drawPath(bottomLeftPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // 4. Bottom-Right Corner
        val bottomRightPath = Path().apply {
            moveTo(width - armLen, height)
            lineTo(width - radius, height)
            quadraticTo(width, height, width, height - radius)
            lineTo(width, height - armLen)
        }
        drawPath(bottomRightPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))

        // 5. Moving Laser Line
        val yPos = height * scanProgress
        val lineMargin = 10.dp.toPx()
        val lineLeft = lineMargin
        val lineRight = width - lineMargin
        val glowHeight = 16.dp.toPx()

        // Symmetric laser glow effect (top & bottom shadow)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.0f),
                    color.copy(alpha = 0.35f),
                    color.copy(alpha = 0.0f),
                ),
                startY = yPos - glowHeight,
                endY = yPos + glowHeight,
            ),
            topLeft = Offset(lineLeft, yPos - glowHeight),
            size = Size(lineRight - lineLeft, glowHeight * 2f),
        )

        // Laser line
        drawLine(
            color = color,
            start = Offset(lineLeft, yPos),
            end = Offset(lineRight, yPos),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun ConnectedSuccessView(
    uiState: InviteUiState,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    val returnProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        visible = true
        returnProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 4000,
                easing = LinearEasing,
            ),
        )
    }

    val checkScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "CheckScale",
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 150),
        label = "ContentAlpha",
    )

    val contentOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "ContentOffsetY",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "ConnectedPulse")
    val ringPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "RingPulseScale",
    )
    val ringPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "RingPulseAlpha",
    )

    val arrowShift by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ArrowShift",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Concentric Glowing Checkmark Circles
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .graphicsLayer {
                            scaleX = checkScale
                            scaleY = checkScale
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // Outer Ring 2
                    Box(
                        modifier = Modifier
                            .size(164.dp * ringPulseScale)
                            .clip(CircleShape)
                            .background(NiyeEshoGreenPrimary.copy(alpha = 0.12f * ringPulseAlpha)),
                    )
                    // Inner Ring 1
                    Box(
                        modifier = Modifier
                            .size(128.dp * ringPulseScale)
                            .clip(CircleShape)
                            .background(NiyeEshoGreenPrimary.copy(alpha = 0.28f * ringPulseAlpha)),
                    )
                    // Solid Center Circle
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(NiyeEshoGreenPrimary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = AppStrings.DESC_CONNECTED,
                            tint = Color.White,
                            modifier = Modifier.size(46.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Avatars Row + Text (Animated Entrance)
                Column(
                    modifier = Modifier
                        .offset(y = contentOffsetY)
                        .graphicsLayer { alpha = contentAlpha },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // My Avatar
                        AvatarImage(
                            avatarUrl = uiState.myAvatarUrl,
                            contentDescription = uiState.myName,
                            size = 58.dp,
                            backgroundColor = NiyeEshoGreenPrimary,
                            textColor = Color.White,
                        )

                        // Bidirectional Exchange Arrows
                        Text(
                            text = "⇄",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoEmeraldAccent,
                            ),
                            modifier = Modifier.offset(x = arrowShift.dp),
                        )

                        // Friend Avatar
                        AvatarImage(
                            avatarUrl = uiState.connectedFriendAvatarUrl,
                            contentDescription = uiState.connectedFriendName,
                            size = 58.dp,
                            backgroundColor = NiyeEshoIndigoTertiary,
                            textColor = Color.White,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = AppStrings.TITLE_CONNECTED,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 26.sp,
                        )

                        Text(
                            text = "You and ${uiState.connectedFriendName} can now share lists.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NiyeEshoTextSubtle,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }

        // Bottom Progress Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LinearProgressIndicator(
                progress = { returnProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = NiyeEshoGreenPrimary,
                trackColor = NiyeEshoDarkSurfaceVariant,
            )
        }
    }
}

@Composable
fun ConnectionFailedView(
    uiState: InviteUiState,
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    val returnProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        visible = true
        returnProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 4000,
                easing = LinearEasing,
            ),
        )
    }

    val iconScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "ErrorIconScale",
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 150),
        label = "ContentAlpha",
    )

    val contentOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "ContentOffsetY",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "FailedPulse")
    val ringPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "RingPulseScale",
    )
    val ringPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "RingPulseAlpha",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Concentric Glowing Red Error Circles
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // Outer Red Ring 2
                    Box(
                        modifier = Modifier
                            .size(164.dp * ringPulseScale)
                            .clip(CircleShape)
                            .background(NiyeEshoBrightRed.copy(alpha = 0.12f * ringPulseAlpha)),
                    )
                    // Inner Red Ring 1
                    Box(
                        modifier = Modifier
                            .size(128.dp * ringPulseScale)
                            .clip(CircleShape)
                            .background(NiyeEshoBrightRed.copy(alpha = 0.28f * ringPulseAlpha)),
                    )
                    // Solid Center Red Circle
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(NiyeEshoBrightRed),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.DESC_FAILED,
                            tint = Color.White,
                            modifier = Modifier.size(46.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Text + Try Again Button
                Column(
                    modifier = Modifier
                        .offset(y = contentOffsetY)
                        .graphicsLayer { alpha = contentAlpha },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = AppStrings.TITLE_CONNECTION_FAILED,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 26.sp,
                        )

                        Text(
                            text = uiState.errorMessage ?: AppStrings.ERROR_INVALID_INVITE_LINK,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NiyeEshoTextSubtle,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                        )
                    }

                    Button(
                        onClick = onTryAgain,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NiyeEshoDarkSurface,
                            contentColor = NiyeEshoMutedGreenText,
                        ),
                    ) {
                        Text(
                            text = AppStrings.ACTION_TRY_AGAIN,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // Bottom Progress Bar (Red/Dark track)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LinearProgressIndicator(
                progress = { returnProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = NiyeEshoBrightRed,
                trackColor = NiyeEshoDarkSurfaceVariant,
            )
        }
    }
}
