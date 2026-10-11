package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings

@Composable
fun AddItemBottomSheet(onAdd: (String, String?, String?) -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Text(AppStrings.ACTION_ADD_ITEM, modifier)
}
