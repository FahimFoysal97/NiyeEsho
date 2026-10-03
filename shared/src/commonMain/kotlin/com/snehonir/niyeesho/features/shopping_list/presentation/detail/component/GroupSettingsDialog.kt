package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

@Composable
fun GroupSettingsDialog(
    members: List<ListMember>,
    availableContacts: List<Contact>,
    onAddMembers: (List<ListMember>) -> Unit,
    onAddContact: (ListMember) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedContactUids by remember { mutableStateOf(emptySet<String>()) }
    val uniqueContacts = remember(availableContacts) { availableContacts.distinctBy { it.uid } }
    val contactUids = remember(uniqueContacts) { uniqueContacts.map { it.uid }.toSet() }
    val currentMemberUids = remember(members) { members.map { it.uid }.toSet() }
    val contactsNotAlreadyInGroup = remember(uniqueContacts, currentMemberUids) {
        uniqueContacts.filter { it.uid !in currentMemberUids }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Group Settings & Members") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = Spacing.xxl * 5),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Current Members Section
                Text(
                    text = "Current Members (${members.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = Spacing.xxl * 2),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    items(members, key = { it.uid }) { member ->
                        val isInContacts = member.uid in contactUids || member.uid == "user_demo"
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(Spacing.lg),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(Spacing.md),
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(member.displayName, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = if (isInContacts) member.uid else "${member.uid} (Not in contacts)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isInContacts) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                                    )
                                }
                            }

                            if (!isInContacts) {
                                IconButton(onClick = { onAddContact(member) }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add to contacts",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Add from Contacts Section
                if (contactsNotAlreadyInGroup.isNotEmpty()) {
                    Text(
                        text = "Add from Contacts",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = Spacing.xxl * 2),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        items(contactsNotAlreadyInGroup, key = { it.uid }) { contact ->
                            val isSelected = selectedContactUids.contains(contact.uid)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedContactUids = if (isSelected) {
                                            selectedContactUids - contact.uid
                                        } else {
                                            selectedContactUids + contact.uid
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
                                        selectedContactUids = if (checked) {
                                            selectedContactUids + contact.uid
                                        } else {
                                            selectedContactUids - contact.uid
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
            if (contactsNotAlreadyInGroup.isNotEmpty()) {
                TextButton(
                    onClick = {
                        val selectedMembers = contactsNotAlreadyInGroup
                            .filter { contact -> selectedContactUids.contains(contact.uid) }
                            .map { ListMember(uid = it.uid, displayName = it.displayName, avatarUrl = it.avatarUrl) }
                        if (selectedMembers.isNotEmpty()) {
                            onAddMembers(selectedMembers)
                            selectedContactUids = emptySet()
                        }
                    },
                    enabled = selectedContactUids.isNotEmpty(),
                ) { Text("Add Selected Contacts") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}
