package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel

@Composable
fun CheckedItemsSection(items: List<ListItemUiModel>, modifier: Modifier = Modifier) {
    Text("${items.size} checked items", modifier)
}
