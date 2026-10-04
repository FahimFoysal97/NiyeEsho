package com.snehonir.niyeesho.features.contacts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppButton
import com.snehonir.niyeesho.core.designsystem.component.AppTextField
import com.snehonir.niyeesho.core.designsystem.component.AppTopBar
import com.snehonir.niyeesho.core.designsystem.component.BrandPageHeader
import com.snehonir.niyeesho.core.designsystem.component.ErrorView
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.contacts.presentation.component.ContactListItem
import com.snehonir.niyeesho.features.shopping_list.presentation.list.component.CreateGroupDialog

@Composable
fun ContactsContent(
    uiState: ContactsUiState,
    onAction: (ContactsUiAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(
            title = UiText.Plain("Family & Contacts"),
            onBackClick = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            BrandPageHeader(
                title = "People you shop with",
                subtitle = "Share lists with family and friends instantly.",
            )
            AppTextField(
                value = uiState.query,
                onValueChange = { onAction(ContactsUiAction.QueryChanged(it)) },
                label = UiText.Plain("Search contacts"),
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = UiText.Plain("👥 Create Group List"),
                onClick = { onAction(ContactsUiAction.CreateGroupClicked) },
                modifier = Modifier.fillMaxWidth(),
            )
            uiState.error?.let { ErrorView(UiText.Plain(it), modifier = Modifier.fillMaxWidth()) }
            if (uiState.contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No contacts yet. Tap 'Create Group List' to start!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(uiState.contacts, key = { it.uid }) { contact ->
                        ContactListItem(
                            contact = contact,
                            onClick = {
                                onAction(ContactsUiAction.ContactSelected(contact))
                            },
                            onDelete = {
                                onAction(ContactsUiAction.RemoveContact(contact.uid))
                            },
                        )
                    }
                }
            }
        }
    }

    if (uiState.isCreateGroupDialogVisible) {
        CreateGroupDialog(
            availableContacts = uiState.contacts,
            onConfirm = { title, members ->
                onAction(ContactsUiAction.CreateGroupConfirmed(title, members))
            },
            onDismiss = { onAction(ContactsUiAction.DismissCreateGroup) },
        )
    }
}
