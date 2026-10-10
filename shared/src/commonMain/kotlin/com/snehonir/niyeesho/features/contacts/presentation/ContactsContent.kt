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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AppBottomBar
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGoldSecondary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoIndigoTertiary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog

@Composable
fun ContactsContent(
    uiState: ContactsUiState,
    onAction: (ContactsUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToLists: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToInvite: () -> Unit = {},
) {
    val avatarColors = listOf(NiyeEshoGreenPrimary, NiyeEshoGoldSecondary, NiyeEshoIndigoTertiary)
    var contactPendingDelete by remember { mutableStateOf<Contact?>(null) }

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

                // Pending Incoming Requests Section (User A view)
                if (uiState.pendingIncomingContacts.isNotEmpty()) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "PENDING REQUESTS (${uiState.pendingIncomingContacts.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoGreenPrimary,
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                itemsIndexed(uiState.pendingIncomingContacts, key = { _, item -> "pending_inc_${item.uid}" }) { _, contact ->
                                    PendingRequestCardRow(
                                        contact = contact,
                                        onAccept = { onAction(ContactsUiAction.AcceptContactRequest(contact)) },
                                        onDecline = { onAction(ContactsUiAction.DeclineContactRequest(contact)) },
                                        onBlock = { onAction(ContactsUiAction.BlockContact(contact)) },
                                    )
                                }
                            }
                        }
                    }
                }

                // Pending Outgoing Requests Section (User B view)
                if (uiState.pendingOutgoingContacts.isNotEmpty()) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "AWAITING APPROVAL (${uiState.pendingOutgoingContacts.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NiyeEshoGoldSecondary,
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 160.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                itemsIndexed(uiState.pendingOutgoingContacts, key = { _, item -> "pending_out_${item.uid}" }) { _, contact ->
                                    PendingOutgoingCardRow(contact = contact)
                                }
                            }
                        }
                    }
                }

                // Active Contacts Section Header
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
                                text = "No active contacts yet. Share your invite link to connect!",
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
                            onDelete = { contactPendingDelete = contact },
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
                    AppNavTab.INVITE -> onNavigateToInvite()
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

    contactPendingDelete?.let { contact ->
        ConfirmDeleteContactDialog(
            contactName = contact.displayName,
            onConfirm = {
                onAction(ContactsUiAction.RemoveContact(contact.uid))
                contactPendingDelete = null
            },
            onDismiss = {
                contactPendingDelete = null
            },
        )
    }
}

@Composable
private fun PendingRequestCardRow(
    contact: Contact,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onBlock: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
        border = BorderStroke(1.dp, NiyeEshoGreenPrimary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                AvatarImage(
                    avatarUrl = contact.avatarUrl,
                    contentDescription = contact.displayName,
                    size = 44.dp,
                    backgroundColor = NiyeEshoGreenPrimary,
                )
                Column {
                    Text(
                        text = contact.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Wants to connect",
                        style = MaterialTheme.typography.bodySmall,
                        color = NiyeEshoGreenMuted,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NiyeEshoGreenPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text("Accept", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onDecline,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    Text("Decline", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCFE3D8))
                }

                IconButton(onClick = onBlock, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Block",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingOutgoingCardRow(contact: Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                AvatarImage(
                    avatarUrl = contact.avatarUrl,
                    contentDescription = contact.displayName,
                    size = 36.dp,
                    backgroundColor = NiyeEshoGoldSecondary,
                )
                Column {
                    Text(
                        text = contact.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Awaiting approval...",
                        style = MaterialTheme.typography.bodySmall,
                        color = NiyeEshoGoldSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteContactDialog(
    contactName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NiyeEshoDarkSurface,
        title = {
            Text(
                text = "Delete Contact",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to remove $contactName from your contacts?",
                style = MaterialTheme.typography.bodyMedium,
                color = NiyeEshoGreenDarkMuted,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE05252),
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFCFE3D8),
                ),
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
    )
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
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick),
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

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove contact",
                    tint = Color(0xFFE05252),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
