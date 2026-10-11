package com.snehonir.niyeesho.features.contacts.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AppBottomBar
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.ErrorDark
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
import com.snehonir.niyeesho.features.contacts.presentation.component.ConfirmDeleteContactDialog
import com.snehonir.niyeesho.features.contacts.presentation.component.PendingOutgoingCardRow
import com.snehonir.niyeesho.features.contacts.presentation.component.PendingRequestCardRow
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
                text = AppStrings.TITLE_CONTACTS,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            IconButton(
                onClick = { onAction(ContactsUiAction.PullToRefresh) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(AppShapes.button)
                    .background(NiyeEshoDarkSurface),
            ) {
                if (uiState.isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = NiyeEshoGreenPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = AppStrings.DESC_REFRESH,
                        tint = NiyeEshoGreenPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
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
                // Search Bar
                item {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = { onAction(ContactsUiAction.QueryChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(AppStrings.HINT_SEARCH_CONTACTS, style = MaterialTheme.typography.bodyMedium, color = NiyeEshoGreenDarkMuted) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = AppStrings.ACTION_SEARCH,
                                tint = NiyeEshoGreenDarkMuted,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        singleLine = true,
                        shape = AppShapes.card,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = NiyeEshoDarkSurface,
                            focusedContainerColor = NiyeEshoDarkSurface,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = NiyeEshoGreenPrimary,
                        ),
                    )
                }

                // 3 Tabs: Contacts | Requests | Pending
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NiyeEshoDarkSurface, RoundedCornerShape(13.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        ContactsTab.entries.forEach { tab ->
                            val isSelected = uiState.selectedTab == tab
                            val label = when (tab) {
                                ContactsTab.CONTACTS -> "Contacts (${uiState.contacts.size})"
                                ContactsTab.REQUESTS -> "Requests (${uiState.pendingIncomingContacts.size})"
                                ContactsTab.PENDING -> "Pending (${uiState.pendingOutgoingContacts.size})"
                            }
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onAction(ContactsUiAction.TabSelected(tab)) },
                                color = if (isSelected) NiyeEshoGreenPrimary else Color.Transparent,
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 9.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else NiyeEshoGreenDarkMuted,
                                    )
                                }
                            }
                        }
                    }
                }

                uiState.error?.let { err ->
                    item { ErrorView(UiText.Plain(err), modifier = Modifier.fillMaxWidth()) }
                }

                when (uiState.selectedTab) {
                    ContactsTab.CONTACTS -> {
                        // Tab 0: Active Contacts
                        if (uiState.contacts.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = AppShapes.card,
                                    colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                                ) {
                                    Text(
                                        text = AppStrings.EMPTY_CONTACTS_MSG,
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
                    ContactsTab.REQUESTS -> {
                        // Tab 1: Incoming Requests
                        if (uiState.pendingIncomingContacts.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = AppShapes.card,
                                    colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                                ) {
                                    Text(
                                        text = AppStrings.EMPTY_INCOMING_REQUESTS_MSG,
                                        modifier = Modifier.padding(Spacing.lg),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NiyeEshoGreenMuted,
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(uiState.pendingIncomingContacts, key = { _, item -> "pending_inc_${item.uid}" }) { _, contact ->
                                PendingRequestCardRow(
                                    contact = contact,
                                    onAccept = { onAction(ContactsUiAction.AcceptContactRequest(contact)) },
                                    onDecline = { onAction(ContactsUiAction.DeclineContactRequest(contact)) },
                                    onBlock = { onAction(ContactsUiAction.BlockContact(contact)) },
                                    isProcessing = uiState.processingUids.contains(contact.uid),
                                )
                            }
                        }
                    }
                    ContactsTab.PENDING -> {
                        // Tab 2: Outgoing Pending Requests
                        if (uiState.pendingOutgoingContacts.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = AppShapes.card,
                                    colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                                ) {
                                    Text(
                                        text = AppStrings.EMPTY_OUTGOING_REQUESTS_MSG,
                                        modifier = Modifier.padding(Spacing.lg),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NiyeEshoGreenMuted,
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(uiState.pendingOutgoingContacts, key = { _, item -> "pending_out_${item.uid}" }) { _, contact ->
                                PendingOutgoingCardRow(contact = contact)
                            }
                        }
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
        shape = AppShapes.card,
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
                    contentDescription = AppStrings.ACTION_DELETE,
                    tint = ErrorDark,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
