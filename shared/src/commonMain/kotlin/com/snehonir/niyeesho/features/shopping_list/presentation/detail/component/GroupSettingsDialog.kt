package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoCheckboxBorder
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

@Composable
fun GroupSettingsDialog(
    listTitle: String,
    members: List<ListMember>,
    availableContacts: List<Contact>,
    onUpdateTitle: (String) -> Unit,
    onDeleteList: () -> Unit,
    onRemoveMember: (String) -> Unit,
    onAddMembers: (List<ListMember>) -> Unit,
    onAddContact: (ListMember) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var titleInput by remember { mutableStateOf(listTitle) }
    var selectedContactUids by remember { mutableStateOf(emptySet<String>()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val uniqueContacts = remember(availableContacts) { availableContacts.distinctBy { it.uid } }
    val contactUids = remember(uniqueContacts) { uniqueContacts.map { it.uid }.toSet() }
    val currentMemberUids = remember(members) { members.map { it.uid }.toSet() }
    val contactsNotAlreadyInGroup = remember(uniqueContacts, currentMemberUids) {
        uniqueContacts.filter { it.uid !in currentMemberUids }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = NiyeEshoDarkSurface,
            title = { Text("Delete Shopping List?", color = Color.White) },
            text = { Text("Are you sure you want to delete this list? This action cannot be undone.", color = NiyeEshoGreenMuted) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteList()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE05252)),
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenDarkMuted),
                ) { Text("Cancel") }
            },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = NiyeEshoDarkSurface,
        title = { Text("Group Settings & Details", color = Color.White, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = Spacing.xxl * 6),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Edit Title Section
                Text(
                    text = "List Title",
                    style = MaterialTheme.typography.labelMedium,
                    color = NiyeEshoGreenPrimary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = NiyeEshoDarkBg,
                            focusedContainerColor = NiyeEshoDarkBg,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = NiyeEshoGreenPrimary,
                        ),
                    )
                    IconButton(
                        onClick = { if (titleInput.isNotBlank()) onUpdateTitle(titleInput) },
                        enabled = titleInput.isNotBlank() && titleInput != listTitle,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Update title",
                            tint = NiyeEshoGreenPrimary,
                        )
                    }
                }

                HorizontalDivider(color = NiyeEshoDarkBg)

                // Current Members Section
                Text(
                    text = "Current Members (${members.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = NiyeEshoGreenPrimary,
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
                                    shape = RoundedCornerShape(8.dp),
                                    color = NiyeEshoGreenPrimary,
                                    modifier = Modifier.size(Spacing.lg),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(Spacing.md),
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(member.displayName, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                    Text(
                                        text = if (isInContacts) member.uid else "${member.uid} (Not in contacts)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isInContacts) NiyeEshoGreenDarkMuted else Color(0xFFE05252),
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (!isInContacts) {
                                    IconButton(onClick = { onAddContact(member) }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add to contacts",
                                            tint = NiyeEshoGreenPrimary,
                                        )
                                    }
                                }
                                if (member.uid != "user_demo") {
                                    IconButton(onClick = { onRemoveMember(member.uid) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove member",
                                            tint = Color(0xFFE05252),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = NiyeEshoDarkBg)

                // Add from Contacts Section
                if (contactsNotAlreadyInGroup.isNotEmpty()) {
                    Text(
                        text = "Add from Contacts",
                        style = MaterialTheme.typography.labelMedium,
                        color = NiyeEshoGreenPrimary,
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
                                    Text(contact.displayName, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                    Text(contact.email, style = MaterialTheme.typography.bodySmall, color = NiyeEshoGreenDarkMuted)
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
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = NiyeEshoGreenPrimary,
                                        uncheckedColor = NiyeEshoCheckboxBorder,
                                        checkmarkColor = Color.White,
                                    ),
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = NiyeEshoDarkBg)

                // Delete List Danger Zone
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE05252)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(Spacing.md),
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    Text("Delete Shopping List")
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
                    colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenPrimary),
                ) { Text("Add Selected Contacts") }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenDarkMuted),
            ) { Text("Close") }
        },
    )
}
