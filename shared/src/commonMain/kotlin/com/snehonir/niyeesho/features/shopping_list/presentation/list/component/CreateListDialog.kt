package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CreateListDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Text("Create shopping list", modifier)
}
