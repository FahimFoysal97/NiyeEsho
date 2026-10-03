package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.ListItemRow

@Composable
fun ListDetailContent(
    uiState: ListDetailUiState,
    onAction: (ListDetailUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = uiState.list?.title ?: UiText.Plain("Shopping list").resolve(),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = UiText.Plain("Add what you need before heading home.").resolve(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            AppButton(
                text = UiText.Plain("Invite someone"),
                onClick = { onAction(ListDetailUiAction.InviteMember) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        uiState.error?.let { message -> item { ErrorView(UiText.Plain(message)) } }
        if (uiState.activeItems.isNotEmpty()) {
            item { Text(UiText.Plain("Still to buy").resolve(), style = MaterialTheme.typography.titleLarge) }
            items(uiState.activeItems, key = { it.id }) { item ->
                ListItemRow(item, { onAction(ListDetailUiAction.ToggleItem(item.id, !item.checked)) })
            }
        }
        if (uiState.checkedItems.isNotEmpty()) {
            item { Text(UiText.Plain("Already in the bag").resolve(), style = MaterialTheme.typography.titleLarge) }
            items(uiState.checkedItems, key = { it.id }) { item ->
                ListItemRow(item, { onAction(ListDetailUiAction.ToggleItem(item.id, false)) })
            }
        }
    }
}