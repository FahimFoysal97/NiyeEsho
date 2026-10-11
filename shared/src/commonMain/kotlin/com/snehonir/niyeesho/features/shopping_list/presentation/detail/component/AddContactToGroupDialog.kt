package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

@Composable
fun AddContactToGroupDialog(
    availableContacts: List<Contact>,
    onConfirm: (members: List<ListMember>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedUids by remember { mutableStateOf(emptySet<String>()) }
    val uniqueContacts = remember(availableContacts) { availableContacts.distinctBy { it.uid } }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(AppStrings.TITLE_ADD_FROM_CONTACTS) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                if (uniqueContacts.isEmpty()) {
                    Text(
                        text = "All your contacts are already in this group, or you don't have any contacts yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = "Select contacts to add to this group shopping list:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = Spacing.xxl * 3),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        items(uniqueContacts, key = { it.uid }) { contact ->
                            val isSelected = selectedUids.contains(contact.uid)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedUids = if (isSelected) {
                                            selectedUids - contact.uid
                                        } else {
                                            selectedUids + contact.uid
                                        }
                                    }
                                    .padding(vertical = Spacing.xs),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(contact.displayName, style = MaterialTheme.typography.bodyMedium)
                                    Text(contact.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedUids = if (checked) {
                                            selectedUids + contact.uid
                                        } else {
                                            selectedUids - contact.uid
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (uniqueContacts.isNotEmpty()) {
                TextButton(
                    onClick = {
                        val selectedMembers = uniqueContacts
                            .filter { selectedUids.contains(it.uid) }
                            .map { ListMember(uid = it.uid, displayName = it.displayName, avatarUrl = it.avatarUrl) }
                        onConfirm(selectedMembers)
                    },
                    enabled = selectedUids.isNotEmpty(),
                ) { Text(AppStrings.ACTION_ADD_TO_GROUP) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.ACTION_CANCEL) }
        },
    )
}
