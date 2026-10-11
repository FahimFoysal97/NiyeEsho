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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGoldSecondary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoIndigoTertiary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.AddItemBottomSheet
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.ContactSettingsDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.GroupSettingsDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.InviteMemberSheet
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.ListItemRow

@Composable
fun ListDetailContent(
    uiState: ListDetailUiState,
    onAction: (ListDetailUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isAddItemSheetOpen by remember { mutableStateOf(false) }
    val accentColors = listOf(NiyeEshoGreenPrimary, NiyeEshoGoldSecondary, NiyeEshoIndigoTertiary)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NiyeEshoDarkBg),
    ) {
        // Top Header Bar with Proper Spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(AppShapes.button)
                    .background(NiyeEshoDarkSurface),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = uiState.list?.title ?: "Shopping list",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = {
                    if (uiState.isContactList) {
                        onAction(ListDetailUiAction.OpenContactSettings)
                    } else {
                        onAction(ListDetailUiAction.OpenGroupSettings)
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(AppShapes.button)
                    .background(NiyeEshoDarkSurface),
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
                // Active Members Chips Section
                item {
                    val members = uiState.list?.members.takeIf { !it.isNullOrEmpty() }
                        ?: listOf(ListMember("user_demo", "Sneho Nir", null))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                        ) {
                            members.forEachIndexed { idx, member ->
                                val chipColor = NiyeEshoGreenPrimary
                                Surface(
                                    shape = AppShapes.surfaceMedium,
                                    color = chipColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, chipColor.copy(alpha = 0.5f)),
                                ) {
                                    Text(
                                        text = member.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = chipColor,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                                    )
                                }
                            }
                        }

                        // Synced Tag
                        Surface(
                            shape = AppShapes.surfaceLarge,
                            color = NiyeEshoDarkSurface,
                        ) {
                            Text(
                                text = "Synced",
                                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = NiyeEshoGreenPrimary,
                            )
                        }
                    }
                }

                // Active Items Section
                if (uiState.activeItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }

                    itemsIndexed(uiState.activeItems, key = { _, item -> item.id }) { _, item ->
                        ListItemRow(
                            item = item,
                            onToggle = { onAction(ListDetailUiAction.ToggleItem(item.id, !item.checked)) },
                            onDelete = { onAction(ListDetailUiAction.DeleteItem(item.id)) },
                        )
                    }
                }

                // Checked / Completed Items Section
                if (uiState.checkedItems.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            text = "Completed (${uiState.checkedItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NiyeEshoGreenMuted,
                        )
                    }

                    itemsIndexed(uiState.checkedItems, key = { _, item -> item.id }) { _, item ->
                        ListItemRow(
                            item = item,
                            onToggle = { onAction(ListDetailUiAction.ToggleItem(item.id, !item.checked)) },
                            onDelete = { onAction(ListDetailUiAction.DeleteItem(item.id)) },
                        )
                    }
                }

                if (uiState.activeItems.isEmpty() && uiState.checkedItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.xxl),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No items in this list yet. Tap '+' to add one!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NiyeEshoGreenMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Floating Action Button to Add Item
            FloatingActionButton(
                onClick = { isAddItemSheetOpen = true },
                containerColor = NiyeEshoGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Spacing.lg),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    }

    if (isAddItemSheetOpen) {
        AddItemBottomSheet(
            onAdd = { name, quantity, unit ->
                onAction(ListDetailUiAction.AddItem(name, quantity, unit))
                isAddItemSheetOpen = false
            },
            onDismiss = { isAddItemSheetOpen = false },
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
            onAddMembers = { members -> onAction(ListDetailUiAction.AddMembersConfirmed(members)) },
            onAddContact = { member -> onAction(ListDetailUiAction.AddContactFromMember(member)) },
            onDismiss = { onAction(ListDetailUiAction.DismissGroupSettings) },
        )
    }

    if (uiState.isContactSettingsVisible) {
        ContactSettingsDialog(
            contact = null,
            onCreateGroupClicked = { },
            onDismiss = { onAction(ListDetailUiAction.DismissContactSettings) },
        )
    }

    uiState.inviteLink?.let { link ->
        InviteMemberSheet(
            inviteLink = link,
            onShare = {},
            onDismiss = { onAction(ListDetailUiAction.DismissInvite) },
        )
    }
}
