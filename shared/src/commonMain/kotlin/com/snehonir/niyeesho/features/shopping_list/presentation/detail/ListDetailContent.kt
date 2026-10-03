package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.AddItemDialog
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.component.ListItemRow

@Composable
fun ListDetailContent(
    uiState: ListDetailUiState,
    onAction: (ListDetailUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isAddDialogVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(
            title = UiText.Plain(uiState.list?.title ?: "Shopping List"),
            onBackClick = onBack,
        )
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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

            FloatingActionButton(
                onClick = { isAddDialogVisible = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.lg),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add new item")
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
}
