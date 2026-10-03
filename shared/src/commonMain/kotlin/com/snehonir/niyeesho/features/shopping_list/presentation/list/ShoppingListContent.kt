package com.snehonir.niyeesho.features.shopping_list.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.ShoppingListCard

@Composable
fun ShoppingListContent(
    uiState: ShoppingListUiState,
    onAction: (ShoppingListUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        AppButton(UiText.Resource("niyeesho_create_list"), { onAction(ShoppingListUiAction.CreateClicked) })
        uiState.error?.let { ErrorView(UiText.Plain(it)) }
        if (uiState.lists.isEmpty()) Text(UiText.Resource("niyeesho_no_lists").resolve())
        LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            items(uiState.lists, key = { it.id }) { list ->
                ShoppingListCard(list, { onAction(ShoppingListUiAction.ListClicked(list.id)) })
            }
        }
    }
}
