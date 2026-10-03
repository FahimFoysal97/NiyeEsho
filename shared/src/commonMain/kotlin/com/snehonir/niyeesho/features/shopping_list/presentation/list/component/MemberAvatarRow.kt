package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

@Composable
fun MemberAvatarRow(members: List<ListMember>, modifier: Modifier = Modifier) {
    Row(modifier) { Text(members.joinToString { it.displayName }) }
}
