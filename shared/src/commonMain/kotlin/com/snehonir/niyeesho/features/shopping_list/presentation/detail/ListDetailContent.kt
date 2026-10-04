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
import androidx.compose.ui.unit.sp
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
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
        // App Top Bar
        AppTopBar(
            title = UiText.Plain(uiState.list?.title ?: "Shopping List"),
            onBackClick = onBack,
            containerColor = NiyeEshoDarkBg,
            contentColor = Color.White,
            actions = {
                IconButton(
                    onClick = {
                        if (uiState.isContactList) {
                            onAction(ListDetailUiAction.OpenContactSettings)
                        } else {
                            onAction(ListDetailUiAction.OpenGroupSettings)
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = NiyeEshoGreenPrimary,
                    )
                }
            },
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    top = Spacing.sm,
                    bottom = 80.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Active Members & Sync Status Row
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
                            // Member Avatars
                            Surface(
                                modifier = Modifier.size(20.dp),
                                shape = CircleShape,
                                color = NiyeEshoGreenPrimary,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("S", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            if ((uiState.list?.members?.size ?: 0) > 1) {
                                Surface(
                                    modifier = Modifier.size(20.dp),
                                    shape = CircleShape,
                                    color = NiyeEshoGoldSecondary,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("R", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                            val memberNames = uiState.list?.members?.joinToString(", ") { it.displayName } ?: "Sneho, Rahim"
                            Text(
                                text = memberNames,
                                fontSize = 10.sp,
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
                                modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NiyeEshoGreenPrimary,
                            )
                        }
                    }
                }

                uiState.error?.let { message -> item { ErrorView(UiText.Plain(message)) } }

                // TO GET Section
                item {
                    Text(
                        text = "TO GET",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NiyeEshoGreenDarkMuted,
                    )
                }

                if (uiState.activeItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(11.dp),
                            colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                        ) {
                            Text(
                                text = "No items left to get. Tap 'Add item' below!",
                                modifier = Modifier.padding(Spacing.md),
                                fontSize = 11.sp,
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

                // DONE Section
                if (uiState.checkedItems.isNotEmpty()) {
                    item {
                        Spacer(Modifier.padding(top = Spacing.xs))
                        Text(
                            text = "DONE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
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

            // Bottom Floating "Add item" Button
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
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add item",
                        tint = NiyeEshoGreenPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Add item",
                        fontSize = 12.sp,
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
