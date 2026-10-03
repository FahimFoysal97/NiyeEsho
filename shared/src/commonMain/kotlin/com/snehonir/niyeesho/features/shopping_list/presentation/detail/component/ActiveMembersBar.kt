package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

@Composable
fun ActiveMembersBar(members: List<ListMember>, modifier: Modifier = Modifier) {
    Text(members.joinToString { it.displayName }, modifier)
}
