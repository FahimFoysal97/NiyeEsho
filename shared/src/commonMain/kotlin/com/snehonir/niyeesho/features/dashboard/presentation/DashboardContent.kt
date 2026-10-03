package com.snehonir.niyeesho.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateListDialog

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onAction: (DashboardUiAction) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
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
                modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.lg),
                contentPadding = PaddingValues(vertical = Spacing.md),
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
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                    )
                                }
                            }
                        }
                    }
                }

                uiState.error?.let { error ->
                    item { ErrorView(UiText.Plain(error), modifier = Modifier.fillMaxWidth()) }
                }

                // Unified list items (groups and contacts combined in single list)
                items(uiState.lists, key = { it.id }) { list ->
                    DashboardListCard(list = list, onClick = { onNavigateToDetail(list.id) })
                }

                if (uiState.lists.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(Spacing.xxl),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No shopping lists or requests yet. Tap '+ New List' to start!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { onAction(DashboardUiAction.CreateClicked) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.lg),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create new list")
            }
        }
    }

    ItemCreateDialog(uiState, onAction)
}

@Composable
private fun ItemCreateDialog(uiState: DashboardUiState, onAction: (DashboardUiAction) -> Unit) {
    if (uiState.isCreateDialogVisible) {
        CreateListDialog(
            onConfirm = { title -> onAction(DashboardUiAction.CreateConfirmed(title, "user_demo")) },
            onDismiss = { onAction(DashboardUiAction.CreateDismissed) },
        )
    }
}

@Composable
private fun DashboardListCard(list: DashboardUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = list.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Updated: ${list.updatedAtFormatted}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                )
                Text(
                    text = "Created: ${list.createdAtFormatted}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                )
            }
        }
    }
}
