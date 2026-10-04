package com.snehonir.niyeesho.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.InviteMemberSheet
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.SyncStatusIndicator

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onAction: (DashboardUiAction) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onShareInvite: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(
            title = UiText.Plain("Niye Esho"),
            actions = {
                IconButton(onClick = onNavigateToProfile) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                }
            },
        )
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = Spacing.lg, end = Spacing.lg, top = Spacing.md, bottom = Spacing.xxl * 3),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // User Greeting Header Item in the Single List
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(
                                        text = "Hello, ${uiState.userName}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                    Text(
                                        text = "Bari ferar age, shob mone koriye dei.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                    )
                                }
                            }
                        }
                    }
                }

                // 2 Action Buttons in a Row (Invite / Share & Create Group)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Button(
                            onClick = { onAction(DashboardUiAction.InviteShareClicked) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        ) {
                            Text("🔗 Invite / Share")
                        }
                        Button(
                            onClick = { onAction(DashboardUiAction.AddGroupClicked) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                            ),
                        ) {
                            Text("👥 Create Group")
                        }
                    }
                }

                uiState.error?.let { error ->
                    item { ErrorView(UiText.Plain(error), modifier = Modifier.fillMaxWidth()) }
                }

                // Unified list items (groups and contacts combined in single list)
                items(uiState.lists, key = { it.id }) { list ->
                    DashboardListCard(
                        list = list,
                        onClick = { onNavigateToDetail(list.id) },
                        onDelete = {
                            if (list.id.startsWith("contact_list_")) {
                                onAction(DashboardUiAction.RemoveContact(list.id.removePrefix("contact_list_")))
                            } else {
                                onAction(DashboardUiAction.DeleteList(list.id))
                            }
                        },
                    )
                }

                if (uiState.lists.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.xxl),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No shopping lists or requests yet. Tap 'Create Group' to start!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isInviteDialogVisible && uiState.inviteLink != null) {
        InviteMemberSheet(
            inviteLink = uiState.inviteLink,
            onShare = { onShareInvite(uiState.inviteLink) },
            onDismiss = { onAction(DashboardUiAction.InviteDismissed) },
        )
    }

    ItemCreateDialog(uiState, onAction)
}

@Composable
private fun ItemCreateDialog(uiState: DashboardUiState, onAction: (DashboardUiAction) -> Unit) {
    if (uiState.isCreateDialogVisible) {
        CreateGroupDialog(
            availableContacts = uiState.availableContacts,
            onConfirm = { title, members ->
                onAction(DashboardUiAction.CreateConfirmed(title, "user_demo", members))
            },
            onDismiss = { onAction(DashboardUiAction.CreateDismissed) },
        )
    }
}

@Composable
private fun DashboardListCard(
    list: DashboardUiModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f).clickable(onClick = onClick),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = list.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.extraSmall,
                ) {
                    Text(
                        text = "Updated: ${list.updatedAtFormatted}",
                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SyncStatusIndicator(list.syncStatus)
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete list or contact",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
