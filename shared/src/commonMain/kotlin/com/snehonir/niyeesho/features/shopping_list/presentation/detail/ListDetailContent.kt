package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGoldSecondary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.AddItemDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.ContactSettingsDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.GroupSettingsDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.ListItemRow
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog

@Composable
fun ListDetailContent(
    uiState: ListDetailUiState,
    onAction: (ListDetailUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isAddDialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NiyeEshoDarkBg),
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = uiState.list?.title ?: "Weekly groceries",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f).padding(horizontal = Spacing.sm),
            )
            IconButton(
                onClick = {
                    if (uiState.isContactList) {
                        onAction(ListDetailUiAction.OpenContactSettings)
                    } else {
                        onAction(ListDetailUiAction.OpenGroupSettings)
                    }
                },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = NiyeEshoGreenPrimary,
                    modifier = Modifier.size(20.dp),
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
                    bottom = 90.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Active Members & Synced Tag
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        ) {
                            Surface(
                                modifier = Modifier.size(24.dp),
                                shape = CircleShape,
                                color = NiyeEshoGreenPrimary,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("S", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            if ((uiState.list?.members?.size ?: 0) > 1) {
                                Surface(
                                    modifier = Modifier.size(24.dp),
                                    shape = CircleShape,
                                    color = NiyeEshoGoldSecondary,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("R", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                            val memberNames = uiState.list?.members?.joinToString(", ") { it.displayName } ?: "Sneho, Rahim"
                            Text(
                                text = memberNames,
                                style = MaterialTheme.typography.bodySmall,
                                color = NiyeEshoGreenMuted,
                            )
                        }

                        // Synced Tag
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = NiyeEshoDarkSurface,
                        ) {
                            Text(
                                text = "Synced",
                                modifier = Modifier.padding(horizontal = Spacing.md, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NiyeEshoGreenPrimary,
                            )
                        }
                    }
                }

                uiState.error?.let { message -> item { ErrorView(UiText.Plain(message)) } }

                // TO GET Section Header
                item {
                    Text(
                        text = "TO GET",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = NiyeEshoGreenDarkMuted,
                    )
                }

                if (uiState.activeItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                        ) {
                            Text(
                                text = "No items left to get. Tap 'Add item' below!",
                                modifier = Modifier.padding(Spacing.md),
                                style = MaterialTheme.typography.bodyMedium,
                                color = NiyeEshoGreenDarkMuted,
                            )
                        }
                    }
                } else {
                    items(uiState.activeItems, key = { it.id }) { item ->
                        ListItemRow(
                            item = item,
                            onToggle = { onAction(ListDetailUiAction.ToggleItem(item.id, !item.checked)) },
                            onDelete = { onAction(ListDetailUiAction.DeleteItem(item.id)) },
                        )
                    }
                }

                // DONE Section Header
                if (uiState.checkedItems.isNotEmpty()) {
                    item {
                        Spacer(Modifier.padding(top = Spacing.xs))
                        Text(
                            text = "DONE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = NiyeEshoGreenDarkMuted,
                        )
                    }
                    items(uiState.checkedItems, key = { it.id }) { item ->
                        ListItemRow(
                            item = item,
                            onToggle = { onAction(ListDetailUiAction.ToggleItem(item.id, false)) },
                            onDelete = { onAction(ListDetailUiAction.DeleteItem(item.id)) },
                        )
                    }
                }
            }

            // Bottom Add Item Button
            OutlinedButton(
                onClick = { isAddDialogVisible = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(Spacing.lg),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, NiyeEshoGreenPrimary),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = Spacing.xs),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add item",
                        tint = NiyeEshoGreenPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = "Add item",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NiyeEshoGreenPrimary,
                    )
                }
            }
        }
    }

    if (isAddDialogVisible) {
        AddItemDialog(
            onConfirm = { name, qty ->
                onAction(ListDetailUiAction.AddItem(name, qty, null))
                isAddDialogVisible = false
            },
            onDismiss = { isAddDialogVisible = false },
        )
    }

    if (uiState.isCreateGroupDialogVisible) {
        val contactUid = uiState.list?.id?.removePrefix("contact_list_").orEmpty()
        val initialSelectedUids = if (contactUid.isNotBlank()) listOf(contactUid) else emptyList()
        val contactName = uiState.list?.title ?: ""
        val initialTitle = if (contactName.isNotBlank()) "Group with $contactName" else ""

        CreateGroupDialog(
            availableContacts = uiState.availableContacts,
            initialTitle = initialTitle,
            initialSelectedContactUids = initialSelectedUids,
            onConfirm = { title, members ->
                onAction(ListDetailUiAction.CreateGroupConfirmed(title, members))
            },
            onDismiss = { onAction(ListDetailUiAction.DismissCreateGroup) },
        )
    }

    if (uiState.isGroupSettingsVisible) {
        GroupSettingsDialog(
            listTitle = uiState.list?.title ?: "",
            members = uiState.list?.members ?: emptyList(),
            availableContacts = uiState.availableContacts,
            onUpdateTitle = { title -> onAction(ListDetailUiAction.UpdateList(title)) },
            onDeleteList = { onAction(ListDetailUiAction.DeleteList) },
            onRemoveMember = { memberId -> onAction(ListDetailUiAction.RemoveMember(memberId)) },
            onAddMembers = { members ->
                onAction(ListDetailUiAction.AddMembersConfirmed(members))
            },
            onAddContact = { member ->
                onAction(ListDetailUiAction.AddContactFromMember(member))
            },
            onDismiss = { onAction(ListDetailUiAction.DismissGroupSettings) },
        )
    }

    if (uiState.isContactSettingsVisible) {
        val contactUid = uiState.list?.id?.removePrefix("contact_list_").orEmpty()
        val contact = uiState.availableContacts.firstOrNull { it.uid == contactUid }
        ContactSettingsDialog(
            contact = contact,
            onCreateGroupClicked = {
                onAction(ListDetailUiAction.DismissContactSettings)
                onAction(ListDetailUiAction.CreateGroupClicked)
            },
            onDismiss = { onAction(ListDetailUiAction.DismissContactSettings) },
        )
    }
}
