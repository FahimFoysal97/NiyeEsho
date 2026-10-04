package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.SyncStatusIndicator

@Composable
fun ListItemRow(item: ListItemUiModel, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onToggle),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (!item.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = item.checked, onCheckedChange = { onToggle() })
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (!item.isRead) {
                            Surface(
                                modifier = Modifier.size(8.dp),
                                shape = MaterialTheme.shapes.extraSmall,
                                color = MaterialTheme.colorScheme.primary,
                            ) {}
                        }
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Normal,
                            color = if (item.checked) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (item.quantityLabel.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.extraSmall,
                        ) {
                            Text(
                                text = item.quantityLabel,
                                modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Added by ${item.addedByName} • ${item.createdAtFormatted}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    )
                    SyncStatusIndicator(item.syncStatus)
                }
            }
        }
    }
}
