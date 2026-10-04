package com.snehonir.niyeesho.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AppBottomBar
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
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
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.InviteMemberSheet
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.SyncStatusIndicator

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onAction: (DashboardUiAction) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToContacts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onShareInvite: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val accentColors = listOf(NiyeEshoGreenPrimary, NiyeEshoGoldSecondary, NiyeEshoIndigoTertiary)
    val mostActiveList = uiState.lists.firstOrNull()
    val otherLists = if (uiState.lists.isNotEmpty()) uiState.lists.drop(1) else emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NiyeEshoDarkBg),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    top = Spacing.lg,
                    bottom = Spacing.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Header Row (Greeting & Plus Button)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Good morning,",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NiyeEshoGreenMuted,
                            )
                            Text(
                                text = uiState.userName.ifBlank { "Sneho" },
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                        IconButton(
                            onClick = { onAction(DashboardUiAction.AddGroupClicked) },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NiyeEshoGreenPrimary),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Group",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }

                // Most Active List Hero Card
                mostActiveList?.let { mostActive ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(mostActive.id) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Accent line
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(110.dp)
                                        .background(NiyeEshoGreenPrimary),
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "MOST ACTIVE LIST",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = NiyeEshoGreenPrimary,
                                        )
                                        SyncStatusIndicator(mostActive.syncStatus)
                                    }
                                    Text(
                                        text = mostActive.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    )
                                    Text(
                                        text = "Updated: ${mostActive.updatedAtFormatted} · ${mostActive.memberCount} members",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NiyeEshoGreenMuted,
                                    )
                                    Spacer(Modifier.height(Spacing.xs))
                                    LinearProgressIndicator(
                                        progress = { 0.4f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = NiyeEshoGreenPrimary,
                                        trackColor = NiyeEshoDarkBg,
                                    )
                                }
                            }
                        }
                    }
                }

                // 2 Action Buttons Row (Invite / Share & Create Group)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Button(
                            onClick = { onAction(DashboardUiAction.InviteShareClicked) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NiyeEshoGreenPrimary,
                                contentColor = Color.White,
                            ),
                            contentPadding = PaddingValues(vertical = Spacing.sm),
                        ) {
                            Text("🔗 Invite / Share", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { onAction(DashboardUiAction.AddGroupClicked) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NiyeEshoDarkSurface,
                                contentColor = Color.White,
                            ),
                            contentPadding = PaddingValues(vertical = Spacing.sm),
                        ) {
                            Text("👥 Create Group", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                uiState.error?.let { error ->
                    item { ErrorView(UiText.Plain(error), modifier = Modifier.fillMaxWidth()) }
                }

                if (otherLists.isNotEmpty()) {
                    item {
                        Text(
                            text = "ALL LISTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NiyeEshoGreenDarkMuted,
                        )
                    }

                    itemsIndexed(otherLists, key = { _, item -> item.id }) { index, list ->
                        val accentColor = accentColors[(index + 1) % accentColors.size]
                        DashboardListRowCard(
                            list = list,
                            accentColor = accentColor,
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
                }

                if (uiState.lists.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.xxl),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No shopping lists yet. Tap 'Create Group' to start!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NiyeEshoGreenMuted,
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        AppBottomBar(
            currentTab = AppNavTab.LISTS,
            onTabSelected = { tab ->
                when (tab) {
                    AppNavTab.LISTS -> Unit
                    AppNavTab.CONTACTS -> onNavigateToContacts()
                    AppNavTab.PROFILE -> onNavigateToProfile()
                }
            },
        )
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
private fun DashboardListRowCard(
    list: DashboardUiModel,
    accentColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Accent Border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(72.dp)
                    .background(accentColor),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = list.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        Surface(
                            modifier = Modifier.size(20.dp),
                            shape = CircleShape,
                            color = accentColor,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = list.title.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            }
                        }
                        Text(
                            text = "${list.memberCount} members · ${list.updatedAtFormatted}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NiyeEshoGreenMuted,
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
                            contentDescription = "Delete list",
                            tint = NiyeEshoGreenDarkMuted,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}
