package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun InviteBottomSheet(
    inviteLink: String,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(UiText.Resource("niyeesho_invite_friend").resolve())
        Text(inviteLink)
        Button(onClick = onShare) { Text(UiText.Resource("niyeesho_share").resolve()) }
        Button(onClick = onDismiss) { Text(UiText.Resource("niyeesho_close").resolve()) }
    }
}
