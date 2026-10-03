package com.snehonir.niyeesho.features.contacts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.AppTextField
import com.snehonir.niyeesho.core.designsystem.component.BrandPageHeader
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
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        BrandPageHeader(
            title = "People you shop with",
            subtitle = "Share lists with family and friends.",
        )
        AppTextField(
            value = uiState.query,
            onValueChange = { onAction(ContactsUiAction.QueryChanged(it)) },
            label = UiText.Plain("Search contacts"),
            modifier = Modifier.fillMaxWidth(),
        )
        AppButton(
            text = UiText.Plain("Invite a friend"),
            onClick = { onAction(ContactsUiAction.InviteClicked) },
            modifier = Modifier.fillMaxWidth(),
        )
        uiState.error?.let { ErrorView(UiText.Plain(it), modifier = Modifier.fillMaxWidth()) }
        if (uiState.contacts.isEmpty()) {
            Text("No contacts yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(uiState.contacts, key = { it.uid }) { contact ->
                    ContactListItem(contact = contact, onClick = {
                        onAction(ContactsUiAction.ContactSelected(contact))
                    })
                }
            }
        }
    }
}