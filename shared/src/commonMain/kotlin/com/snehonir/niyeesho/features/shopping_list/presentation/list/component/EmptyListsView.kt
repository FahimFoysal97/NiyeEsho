package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun EmptyListsView(modifier: Modifier = Modifier) {
    Text(UiText.Resource("niyeesho_no_lists").resolve(), modifier)
}
