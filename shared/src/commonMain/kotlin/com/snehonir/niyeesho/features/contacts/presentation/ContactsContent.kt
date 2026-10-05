package com.snehonir.niyeesho.features.contacts.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.snehonir.niyeesho.core.designsystem.component.AppBottomBar
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurfaceInner
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGoldSecondary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoIndigoTertiary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.presentation.component.QrCodeDisplay
import com.snehonir.niyeesho.features.contacts.presentation.component.rememberQrImageHelper
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog
import niyeesho.shared.generated.resources.Res
import niyeesho.shared.generated.resources.content_copy
import niyeesho.shared.generated.resources.download
import org.jetbrains.compose.resources.painterResource

private enum class InviteTab { QR, LINK }

@Composable
fun ContactsContent(
    uiState: ContactsUiState,
    onAction: (ContactsUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToLists: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
) {
    val avatarColors = listOf(NiyeEshoGreenPrimary, NiyeEshoGoldSecondary, NiyeEshoIndigoTertiary)
    var selectedTab by remember { mutableStateOf(InviteTab.QR) }
    var isQrEnlarged by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val qrImageHelper = rememberQrImageHelper()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NiyeEshoDarkBg),
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Contacts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = NiyeEshoDarkSurface,
                modifier = Modifier.clickable { onAction(ContactsUiAction.ScanQrClicked) },
            ) {
                Text(
                    text = "⌖ Scan",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCFE3D8),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    top = Spacing.xs,
                    bottom = Spacing.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Your Invite Card (QR + Link)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurfaceInner),
                        border = BorderStroke(1.5.dp, NiyeEshoGreenPrimary),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            // Card Header: "Your invite" + Segmented toggle [QR | Link]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Your invite",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NiyeEshoGreenPrimary,
                                )

                                // Segmented Control
                                Row(
                                    modifier = Modifier
                                        .background(NiyeEshoDarkSurface, RoundedCornerShape(9.dp))
                                        .padding(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(if (selectedTab == InviteTab.QR) NiyeEshoGreenPrimary else Color.Transparent)
                                            .clickable { selectedTab = InviteTab.QR }
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "QR",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (selectedTab == InviteTab.QR) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTab == InviteTab.QR) Color.White else NiyeEshoGreenMuted,
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(if (selectedTab == InviteTab.LINK) NiyeEshoGreenPrimary else Color.Transparent)
                                            .clickable { selectedTab = InviteTab.LINK }
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Link",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (selectedTab == InviteTab.LINK) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTab == InviteTab.LINK) Color.White else NiyeEshoGreenMuted,
                                        )
                                    }
                                }
                            }
                            val link = uiState.inviteLink
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (selectedTab == InviteTab.QR) {
                                    // QR Tab (Start side: QR Code, End side: Vertical IconButtons)
                                    Box(
                                        modifier = Modifier
                                            .size(130.dp),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        QrCodeDisplay(
                                            value = link ?: "",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(12.dp)),
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            FilledTonalIconButton(
                                                onClick = {
                                                    if (!link.isNullOrBlank()) {
                                                        qrImageHelper.saveQrCodeImage(link)
                                                    }
                                                },
                                                shape = CircleShape,
                                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                    containerColor = NiyeEshoGreenPrimary,
                                                    contentColor = Color.White,
                                                ),
                                            ) {
                                                Icon(
                                                    painter = painterResource(Res.drawable.download),
                                                    contentDescription = "Save Image",
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }

                                            FilledIconButton(
                                                onClick = {
                                                    if (!link.isNullOrBlank()) {
                                                        qrImageHelper.shareQrCodeImage(link)
                                                    }
                                                },
                                                shape = CircleShape,
                                                colors = IconButtonDefaults.filledIconButtonColors(
                                                    containerColor = NiyeEshoGreenPrimary,
                                                    contentColor = Color.White,
                                                ),
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share Image",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // Link Tab (Start side: Link Box, End side: Vertical IconButtons)
                                    val scrollState = rememberScrollState()
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(130.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(NiyeEshoDarkBg)
                                            .border(1.dp, Color(0xFF1F2D24), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 10.dp, vertical = 9.dp),
                                    ) {
                                        Text(
                                            text = link ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFCFE3D8),
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .verticalScroll(scrollState),
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            FilledIconButton(
                                                onClick = {
                                                    if (!link.isNullOrBlank()) {
                                                        clipboardManager.setText(AnnotatedString(link))
                                                    }
                                                },
                                                shape = CircleShape,
                                                colors = IconButtonDefaults.filledIconButtonColors(
                                                    containerColor = NiyeEshoGreenPrimary,
                                                    contentColor = Color.White,
                                                ),
                                            ) {
                                                Icon(
                                                    painter = painterResource(Res.drawable.content_copy),
                                                    contentDescription = "Copy Link",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }

                                            FilledIconButton(
                                                onClick = { onAction(ContactsUiAction.InviteClicked) },
                                                shape = CircleShape,
                                                colors = IconButtonDefaults.outlinedIconButtonColors(
                                                    containerColor = NiyeEshoGreenPrimary,
                                                    contentColor = Color.White,
                                                ),
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share Link",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = { onAction(ContactsUiAction.QueryChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search contacts...", style = MaterialTheme.typography.bodyMedium, color = NiyeEshoGreenDarkMuted) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = NiyeEshoGreenDarkMuted,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = NiyeEshoDarkSurface,
                            focusedContainerColor = NiyeEshoDarkSurface,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = NiyeEshoGreenPrimary,
                        ),
                    )
                }

                uiState.error?.let { err ->
                    item { ErrorView(UiText.Plain(err), modifier = Modifier.fillMaxWidth()) }
                }

                // ON NIYE ESHO Section Header
                item {
                    Text(
                        text = "ON NIYE ESHO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NiyeEshoGreenDarkMuted,
                    )
                }

                if (uiState.contacts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                        ) {
                            Text(
                                text = "No contacts yet. Share your invite link to connect!",
                                modifier = Modifier.padding(Spacing.lg),
                                style = MaterialTheme.typography.bodyMedium,
                                color = NiyeEshoGreenMuted,
                            )
                        }
                    }
                } else {
                    itemsIndexed(uiState.contacts, key = { _, item -> item.uid }) { index, contact ->
                        val avatarBg = avatarColors[index % avatarColors.size]
                        val subtitleText = contact.email

                        ContactCardRow(
                            contact = contact,
                            avatarBg = avatarBg,
                            subtitleText = subtitleText,
                            onClick = { onAction(ContactsUiAction.ContactSelected(contact)) },
                            onDelete = { onAction(ContactsUiAction.RemoveContact(contact.uid)) },
                        )
                    }
                }
            }
        }

        // Bottom Navigation Bar
        AppBottomBar(
            currentTab = AppNavTab.CONTACTS,
            onTabSelected = { tab ->
                when (tab) {
                    AppNavTab.LISTS -> onNavigateToLists()
                    AppNavTab.CONTACTS -> Unit
                    AppNavTab.PROFILE -> onNavigateToProfile()
                }
            },
        )
    }

    if (uiState.isCreateGroupDialogVisible) {
        CreateGroupDialog(
            availableContacts = uiState.contacts,
            initialTitle = uiState.selectedContactForGroup?.let { "Group with ${it.displayName}" } ?: "",
            initialSelectedContactUids = uiState.selectedContactForGroup?.let { listOf(it.uid) } ?: emptyList(),
            onConfirm = { title, members ->
                onAction(ContactsUiAction.CreateGroupConfirmed(title, members))
            },
            onDismiss = { onAction(ContactsUiAction.DismissCreateGroup) },
        )
    }

    val inviteLink = uiState.inviteLink
    if (isQrEnlarged && !inviteLink.isNullOrBlank()) {
        Dialog(onDismissRequest = { isQrEnlarged = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkBg),
                border = BorderStroke(1.dp, Color(0xFF1F2D24)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = { isQrEnlarged = false },
                            modifier = Modifier
                                .size(28.dp)
                                .background(NiyeEshoDarkSurface, CircleShape),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        QrCodeDisplay(
                            value = inviteLink,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Text(
                        text = "Screen brightness raised for easy scanning",
                        style = MaterialTheme.typography.bodySmall,
                        color = NiyeEshoGreenDarkMuted,
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = { qrImageHelper.saveQrCodeImage(inviteLink) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(11.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NiyeEshoDarkSurface),
                            contentPadding = PaddingValues(vertical = 9.dp),
                        ) {
                            Text("Save image", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFCFE3D8))
                        }

                        Button(
                            onClick = { qrImageHelper.shareQrCodeImage(inviteLink) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(11.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NiyeEshoDarkSurface),
                            contentPadding = PaddingValues(vertical = 9.dp),
                        ) {
                            Text("Share", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFCFE3D8))
                        }
                    }

                    Button(
                        onClick = {
                            isQrEnlarged = false
                            onAction(ContactsUiAction.ScanQrClicked)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(11.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NiyeEshoGreenPrimary),
                        contentPadding = PaddingValues(vertical = 9.dp),
                    ) {
                        Text("Scan a friend instead", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactCardRow(
    contact: Contact,
    avatarBg: Color,
    subtitleText: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                AvatarImage(
                    avatarUrl = contact.avatarUrl,
                    contentDescription = contact.displayName,
                    size = 36.dp,
                    backgroundColor = avatarBg,
                )
                Column {
                    Text(
                        text = contact.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall,
                        color = NiyeEshoGreenDarkMuted,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove contact",
                        tint = NiyeEshoGreenDarkMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
