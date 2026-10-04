package com.snehonir.niyeesho.features.contacts.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog

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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Contacts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
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
                                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = NiyeEshoDarkSurface,
                                        modifier = Modifier.clickable { onAction(ContactsUiAction.ScanQrClicked) },
                                    ) {
                                        Text(
                                            text = "📷 QR",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NiyeEshoGreenPrimary,
                                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = NiyeEshoDarkSurface,
                                        modifier = Modifier.clickable { onAction(ContactsUiAction.InviteClicked) },
                                    ) {
                                        Text(
                                            text = "🔗 Link",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NiyeEshoGreenPrimary,
                                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                                        )
                                    }
                                }
                            }

                            // QR Display Box
                            uiState.inviteLink?.let { link ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color.White, RoundedCornerShape(10.dp))
                                        .padding(Spacing.xs),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    QrCodeDisplay(value = link, modifier = Modifier.fillMaxSize())
                                }
                            }

                            Text(
                                text = "Friend scans to connect with you",
                                style = MaterialTheme.typography.bodySmall,
                                color = NiyeEshoGreenDarkMuted,
                                textAlign = TextAlign.Center,
                            )
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
                /*IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = NiyeEshoGreenPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }*/
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
