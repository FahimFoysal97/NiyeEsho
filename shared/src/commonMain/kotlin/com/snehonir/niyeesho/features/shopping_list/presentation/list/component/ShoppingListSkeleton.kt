package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ShoppingListSkeleton(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier)
}
