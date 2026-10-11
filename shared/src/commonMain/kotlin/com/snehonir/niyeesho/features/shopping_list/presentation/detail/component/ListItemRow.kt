package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.ErrorDark
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoCheckboxBorder
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.SyncStatusIndicator

@Composable
fun ListItemRow(
    item: ListItemUiModel,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardColor = if (item.checked) NiyeEshoDarkSurface.copy(alpha = 0.5f) else NiyeEshoDarkSurface

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.card,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.xs),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = item.checked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = NiyeEshoGreenPrimary,
                    uncheckedColor = NiyeEshoCheckboxBorder,
                    checkmarkColor = Color.White,
                ),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onToggle),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs / 2),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.checked) NiyeEshoGreenMuted else Color.White,
                    textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None,
                )
                val subtitle = buildString {
                    if (item.quantityLabel.isNotBlank()) {
                        append("${item.quantityLabel} · ")
                    }
                    append("added by ${item.addedByName}")
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NiyeEshoGreenDarkMuted,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SyncStatusIndicator(item.syncStatus)
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
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
}
