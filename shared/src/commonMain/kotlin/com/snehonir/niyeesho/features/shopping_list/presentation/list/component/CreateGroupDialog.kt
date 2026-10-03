package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.component.AppTextField
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

@Composable
fun CreateGroupDialog(
    availableContacts: List<Contact> = emptyList(),
    initialTitle: String = "",
    initialSelectedContactUids: List<String> = emptyList(),
    onConfirm: (title: String, members: List<ListMember>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var selectedUids by remember { mutableStateOf(initialSelectedContactUids.toSet()) }
    val uniqueContacts = remember(availableContacts) { availableContacts.distinctBy { it.uid } }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Create Group List") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                AppTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = UiText.Plain("Group Name"),
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Owner: 👤 Sneho Nir (You)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (uniqueContacts.isNotEmpty()) {
                    Text(
                        text = "Add contacts to this group:",
                        style = MaterialTheme.typography.labelLarge,
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
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val selectedMembers = uniqueContacts
                            .filter { selectedUids.contains(it.uid) }
                            .map { ListMember(uid = it.uid, displayName = it.displayName, avatarUrl = it.avatarUrl) }
                        onConfirm(title.trim(), selectedMembers)
                    }
                },
                enabled = title.isNotBlank(),
            ) { Text("Create Group") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
