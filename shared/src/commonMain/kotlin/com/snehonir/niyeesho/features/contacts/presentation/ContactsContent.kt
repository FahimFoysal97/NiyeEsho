package com.snehonir.niyeesho.features.contacts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.AppTextField
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.contacts.presentation.component.ContactListItem

@Composable
fun ContactsContent(
    uiState: ContactsUiState,
    onAction: (ContactsUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        AppTextField(
            value = uiState.query,
            onValueChange = { onAction(ContactsUiAction.QueryChanged(it)) },
            label = UiText.Resource("niyeesho_search_contacts"),
            modifier = Modifier.fillMaxWidth(),
        )
        AppButton(
            text = UiText.Resource("niyeesho_invite_friend"),
            onClick = { onAction(ContactsUiAction.InviteClicked) },
        )
        uiState.error?.let { ErrorView(UiText.Plain(it)) }
        if (uiState.contacts.isEmpty()) {
            Text(UiText.Resource("niyeesho_no_contacts").resolve())
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(uiState.contacts, key = { it.uid }) { contact ->
                    ContactListItem(contact = contact, onClick = {
                        onAction(ContactsUiAction.ContactSelected(contact))
                    })
                }
            }
        }
    }
}
