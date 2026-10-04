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
                    start = Spacing.md,
                    end = Spacing.md,
                    top = Spacing.md,
                    bottom = Spacing.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                // Header Row (Good morning / Sneho + Plus Button)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                .size(Spacing.xxl)
                                .clip(RoundedCornerShape(Spacing.sm))
                                .background(NiyeEshoGreenPrimary),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Group",
                                tint = Color.White,
                                modifier = Modifier.size(Spacing.md),
                            )
                        }
                    }
                }

                // Most Active Hero Card (Real Dynamic Data & Progress)
                mostActiveList?.let { mostActive ->
                    item {
                        val activeMembersStr = if (mostActive.members.isNotEmpty()) {
                            mostActive.members.joinToString(", ") { it.displayName }
                        } else {
                            uiState.userName.ifBlank { "Sneho" }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(mostActive.id) },
                            shape = RoundedCornerShape(Spacing.md),
                            colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                // Left accent border
                                Box(
                                    modifier = Modifier
                                        .width(Spacing.xs / 2)
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
                                    Text(
                                        text = "${mostActive.lastActivityText} · $activeMembersStr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NiyeEshoGreenMuted,
                                    )
                                    Spacer(Modifier.height(Spacing.xs))
                                    LinearProgressIndicator(
                                        progress = { mostActive.progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(Spacing.xs / 2)
                                            .clip(RoundedCornerShape(Spacing.xs)),
                                        color = if (mostActive.isDone) NiyeEshoGoldSecondary else NiyeEshoGreenPrimary,
                                        trackColor = NiyeEshoDarkBg,
                                    )
                                    Spacer(Modifier.height(Spacing.xs / 2))
                                    Text(
                                        text = mostActive.progressDetailLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NiyeEshoGreenMuted,
                                    )
                                }
                            }
                        }
                    }
                }

                uiState.error?.let { error ->
                    item { ErrorView(UiText.Plain(error), modifier = Modifier.fillMaxWidth()) }
                }

                // Other Lists (Real Dynamic Data & Progress)
                if (otherLists.isNotEmpty()) {
                    itemsIndexed(otherLists, key = { _, item -> item.id }) { index, list ->
                        val accentColor = accentColors[(index + 1) % accentColors.size]

                        DashboardListRowCard(
                            list = list,
                            accentColor = accentColor,
                            avatarColors = accentColors,
                            userName = uiState.userName,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayMembers = if (list.members.isNotEmpty()) {
        list.members
    } else {
        listOf(ListMember("user_demo", userName.ifBlank { "User" }, null))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Spacing.md),
        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Accent Border
            Box(
                modifier = Modifier
                    .width(Spacing.xs / 2)
                    .height(72.dp)
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
                                val initial = member.displayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                                Surface(
                                    modifier = Modifier.size(Spacing.md + Spacing.xs / 2),
                                    shape = CircleShape,
                                    color = bg,
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = initial,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                        )
                                    }
                                }
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
                        .height(Spacing.xs / 2)
                        .clip(RoundedCornerShape(Spacing.xs)),
                    color = if (list.isDone) NiyeEshoGoldSecondary else accentColor,
                    trackColor = NiyeEshoDarkBg,
                )
            }
        }
    }
}
