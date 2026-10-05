package com.snehonir.niyeesho.features.invite.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AppBottomBar
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
import com.snehonir.niyeesho.core.designsystem.theme.*
import com.snehonir.niyeesho.features.contacts.presentation.component.QrCodeDisplay
import com.snehonir.niyeesho.features.contacts.presentation.component.rememberQrImageHelper
import com.snehonir.niyeesho.features.invite.presentation.component.CameraQrScannerView
import niyeesho.shared.generated.resources.Res
import niyeesho.shared.generated.resources.content_copy
import niyeesho.shared.generated.resources.content_paste
import niyeesho.shared.generated.resources.download
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
                text = "Invite",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            // Sub-Tabs (.tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16241C), RoundedCornerShape(13.dp))
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
                        text = "My code",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.selectedSubTab == 0) Color.White else Color(0xFF6F8A7C),
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
                        text = "Scan",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.selectedSubTab == 1) Color.White else Color(0xFF6F8A7C),
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
                                text = "YOUR QR CODE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoGreenPrimary,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(bottom = 10.dp),
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                QrCodeDisplay(
                                    value = uiState.inviteLink,
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
                                    color = Color(0xFF1F2D24),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(painterResource(Res.drawable.download), contentDescription = "Save", tint = Color(0xFF34D399), modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save", style = MaterialTheme.typography.labelMedium, color = Color(0xFFCFE3D8), fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    onClick = { qrImageHelper.shareQrCodeImage(uiState.inviteLink) },
                                    shape = RoundedCornerShape(11.dp),
                                    color = Color(0xFF1F2D24),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF34D399), modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share", style = MaterialTheme.typography.labelMedium, color = Color(0xFFCFE3D8), fontWeight = FontWeight.Bold)
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
                                text = "YOUR INVITE LINK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoGreenPrimary,
                                modifier = Modifier.padding(bottom = 10.dp),
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(Color(0xFF0F1A14))
                                    .border(1.dp, NiyeEshoDarkSurface, RoundedCornerShape(11.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        pushStyle(SpanStyle(color = Color(0xFF7F9D8E), fontWeight = FontWeight.Bold))
                                        append(uiState.inviteLink)
                                        pop()
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFCFE3D8),
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
                                    color = Color(0xFF1F2D24),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(painterResource(Res.drawable.content_copy), contentDescription = "Copy", tint = Color(0xFF34D399), modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Copy", style = MaterialTheme.typography.labelMedium, color = Color(0xFFCFE3D8), fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    onClick = { onAction(InviteUiAction.ShareLink) },
                                    shape = RoundedCornerShape(11.dp),
                                    color = Color(0xFF1F2D24),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF34D399), modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share", style = MaterialTheme.typography.labelMedium, color = Color(0xFFCFE3D8), fontWeight = FontWeight.Bold)
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1C2A22), Color(0xFF0A120D))
                                        )
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                CameraQrScannerView(
                                    onQrCodeScanned = { scannedLink ->
                                        if (scannedLink.isNotBlank()) {
                                            onAction(InviteUiAction.PastedLinkClicked(scannedLink))
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                )

                                Box(modifier = Modifier.size(180.dp)) {
                                    Box(modifier = Modifier.size(30.dp).align(Alignment.TopStart).border(3.5.dp, NiyeEshoGreenPrimary, RoundedCornerShape(topStart = 10.dp)))
                                    Box(modifier = Modifier.size(30.dp).align(Alignment.TopEnd).border(3.5.dp, NiyeEshoGreenPrimary, RoundedCornerShape(topEnd = 10.dp)))
                                    Box(modifier = Modifier.size(30.dp).align(Alignment.BottomStart).border(3.5.dp, NiyeEshoGreenPrimary, RoundedCornerShape(bottomStart = 10.dp)))
                                    Box(modifier = Modifier.size(30.dp).align(Alignment.BottomEnd).border(3.5.dp, NiyeEshoGreenPrimary, RoundedCornerShape(bottomEnd = 10.dp)))
                                }
                                // Pill badge inside camera view
                                Surface(
                                    onClick = {
                                        qrImageHelper.pickAndDecodeQrImage { decodedLink ->
                                            if (!decodedLink.isNullOrBlank()) {
                                                onAction(InviteUiAction.PastedLinkClicked(decodedLink))
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF0F1A14).copy(alpha = 0.85f),
                                    border = BorderStroke(1.dp, Color(0xFF2A3D32)),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 16.dp),
                                ) {
                                    Text(
                                        text = "Choose from photos",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFCFE3D8),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    )
                                }
                            }

                            Text(
                                text = "Point your camera at your friend's QR code",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7F9D8E),
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
                                Text("or paste invite link", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6F8A7C))
                                HorizontalDivider(modifier = Modifier.weight(1f), color = NiyeEshoDarkSurface)
                            }

                            // Read-only link box (.lf) with Paste button on the right
                            val hasLink = uiState.pastedLink.isNotBlank()
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF111F18),
                                border = BorderStroke(1.5.dp, if (hasLink) NiyeEshoGreenPrimary else Color(0xFF2A3D32)),
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
                                        color = if (hasLink) Color.White else Color(0xFF4A6358),
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
                                        color = Color(0xFF1F2D24),
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(8.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(painterResource(Res.drawable.content_paste), contentDescription = "Paste", modifier = Modifier.size(18.dp), tint = Color(0xFF34D399))
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
                                    text = if (isValidLink) "Link looks good" else "That doesn't look like a Niye Esho invite link",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isValidLink) Color(0xFF34D399) else Color(0xFFFF8083),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }

                            Button(
                                onClick = { if (isValidLink) onAction(InviteUiAction.ConnectInviteClicked) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isValidLink) NiyeEshoGreenPrimary else NiyeEshoDarkSurface,
                                    contentColor = if (isValidLink) Color.White else Color(0xFF4A6358),
                                ),
                                contentPadding = PaddingValues(12.dp),
                            ) {
                                Text("Connect", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        ScannerStep.CONNECTED -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = Spacing.xxl),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(84.dp)
                                        .clip(CircleShape)
                                        .background(NiyeEshoGreenPrimary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Connected", tint = Color.White, modifier = Modifier.size(40.dp))
                                }

                                Row(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(NiyeEshoGreenPrimary),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("M", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Connected",
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(NiyeEshoIndigoTertiary),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("N", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Text(
                                    text = "You're connected!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )

                                Text(
                                    text = "You and ${uiState.connectedFriendName} can now share lists.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF7F9D8E),
                                    textAlign = TextAlign.Center,
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Returning to Contacts…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF7F9D8E),
                                )
                            }
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
