package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun EmptyContactsView(modifier: Modifier = Modifier) {
    Text(UiText.Resource("niyeesho_no_contacts").resolve(), modifier)
}
