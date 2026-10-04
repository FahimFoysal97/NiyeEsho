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
import androidx.compose.material.icons.filled.CheckCircle
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
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.component.AppNavTab
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGoldSecondary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoIndigoTertiary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.InviteMemberSheet
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog

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
                    top = Spacing.md,
                    bottom = Spacing.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Header Row (Good morning / Sneho + Plus Button)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs / 2)) {
                            Text(
                                text = "Good morning",
                                style = MaterialTheme.typography.bodySmall,
                                color = NiyeEshoGreenMuted,
                            )
                            Text(
                                text = uiState.userName.ifBlank { "Sneho" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                        IconButton(
                            onClick = { onAction(DashboardUiAction.AddGroupClicked) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NiyeEshoGreenPrimary),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Group",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }

                // Most Active Hero Card (Last Active List)
                mostActiveList?.let { mostActive ->
                    item {
                        val displayMembers = if (mostActive.members.isNotEmpty()) {
                            mostActive.members
                        } else {
                            listOf(ListMember(uiState.currentUserUid ?: "user_demo", uiState.userName.ifBlank { "User" }, uiState.userAvatarUrl))
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(mostActive.id) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Left accent border
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(116.dp)
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
                                            text = "LAST ACTIVE LIST",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = NiyeEshoGreenPrimary,
                                        )
                                        if (mostActive.isDone) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Completed",
                                                tint = NiyeEshoGreenPrimary,
                                                modifier = Modifier.size(Spacing.md),
                                            )
                                        } else {
                                            Text(
                                                text = mostActive.progressLabel,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = NiyeEshoGreenMuted,
                                            )
                                        }
                                    }
                                    Text(
                                        text = mostActive.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    )
                                    // Member circular letters row + member count & last activity
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy((-4).dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            displayMembers.take(3).forEachIndexed { idx, member ->
                                                val bg = accentColors[idx % accentColors.size]
                                                AvatarImage(
                                                    avatarUrl = member.avatarUrl,
                                                    contentDescription = member.displayName,
                                                    size = 20.dp,
                                                    backgroundColor = bg,
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${mostActive.memberCount} members · ${mostActive.lastActivityText}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NiyeEshoGreenMuted,
                                        )
                                    }
                                    Spacer(Modifier.height(Spacing.xs / 2))
                                    LinearProgressIndicator(
                                        progress = { mostActive.progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(Spacing.xs)),
                                        color = if (mostActive.isDone) NiyeEshoGoldSecondary else NiyeEshoGreenPrimary,
                                        trackColor = NiyeEshoDarkBg,
                                    )
                                }
                            }
                        }
                    }
                }

                uiState.error?.let { error ->
                    item { ErrorView(UiText.Plain(error), modifier = Modifier.fillMaxWidth()) }
                }

                // Other Lists
                if (otherLists.isNotEmpty()) {
                    itemsIndexed(otherLists, key = { _, item -> item.id }) { index, list ->
                        val accentColor = accentColors[(index + 1) % accentColors.size]

                        DashboardListRowCard(
                            list = list,
                            accentColor = accentColor,
                            avatarColors = accentColors,
                            userName = uiState.userName,
                            userAvatarUrl = uiState.userAvatarUrl,
                            onClick = { onNavigateToDetail(list.id) },
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
                                text = "No shopping lists yet. Tap '+' to create one!",
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
    avatarColors: List<Color>,
    userName: String,
    userAvatarUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayMembers = list.members.ifEmpty {
        listOf(ListMember("user_demo", userName.ifBlank { "User" }, userAvatarUrl))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Accent Border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(76.dp)
                    .background(accentColor),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs / 2)) {
                        Text(
                            text = list.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-4).dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            displayMembers.take(3).forEachIndexed { idx, member ->
                                val bg = avatarColors[idx % avatarColors.size]
                                AvatarImage(
                                    avatarUrl = member.avatarUrl,
                                    contentDescription = member.displayName,
                                    size = 20.dp,
                                    backgroundColor = bg,
                                )
                            }
                        }
                    }

                    if (list.isDone) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = NiyeEshoGreenPrimary,
                            modifier = Modifier.size(Spacing.md),
                        )
                    } else {
                        Text(
                            text = list.progressLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = NiyeEshoGreenMuted,
                        )
                    }
                }

                // Full-width Progress Bar
                LinearProgressIndicator(
                    progress = { list.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(Spacing.xs)),
                    color = if (list.isDone) NiyeEshoGoldSecondary else accentColor,
                    trackColor = NiyeEshoDarkBg,
                )
            }
        }
    }
}
