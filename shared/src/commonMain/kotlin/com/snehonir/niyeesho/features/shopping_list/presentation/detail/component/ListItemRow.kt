package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel

@Composable
fun ListItemRow(item: ListItemUiModel, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.clickable(onClick = onToggle)) {
        Checkbox(checked = item.checked, onCheckedChange = { onToggle() })
        Text("${item.name} ${item.quantityLabel}")
    }
}
