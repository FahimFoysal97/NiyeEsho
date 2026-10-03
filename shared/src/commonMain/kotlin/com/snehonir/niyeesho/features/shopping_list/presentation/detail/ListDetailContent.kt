package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        item { Text(uiState.list?.title ?: UiText.Resource("niyeesho_list").resolve()) }
        item { AppButton(UiText.Resource("niyeesho_invite_member"), { onAction(ListDetailUiAction.InviteMember) }) }
        uiState.error?.let { item { ErrorView(UiText.Plain(it)) } }
        items(uiState.activeItems, key = { it.id }) { item ->
            ListItemRow(item, { onAction(ListDetailUiAction.ToggleItem(item.id, !item.checked)) })
        }
        if (uiState.checkedItems.isNotEmpty()) {
            item { Text(UiText.Resource("niyeesho_checked_items").resolve()) }
            items(uiState.checkedItems, key = { it.id }) { item ->
                ListItemRow(item, { onAction(ListDetailUiAction.ToggleItem(item.id, false)) })
            }
        }
    }
}
