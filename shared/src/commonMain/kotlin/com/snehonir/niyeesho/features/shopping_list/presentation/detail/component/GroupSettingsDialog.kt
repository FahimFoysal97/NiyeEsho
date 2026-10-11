package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.snehonir.niyeesho.core.designsystem.theme.ErrorDark
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoCheckboxBorder
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenLight
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
            shape = AppShapes.dialog,
            containerColor = NiyeEshoDarkSurface,
            title = { Text("Delete Shopping List?", color = Color.White, style = MaterialTheme.typography.titleMedium) },
            text = { Text("Are you sure you want to delete this list? This action cannot be undone.", color = NiyeEshoGreenMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteList()
                    },
                    shape = AppShapes.button,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorDark, contentColor = Color.White),
                ) { Text("Delete", fontWeight = FontWeight.Bold) }
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
        shape = AppShapes.dialog,
        containerColor = NiyeEshoDarkSurface,
        title = {
            Text(
                "Group Settings & Details",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Edit Title Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = "List Title",
                            style = MaterialTheme.typography.labelMedium,
                            color = NiyeEshoGreenPrimary,
                            fontWeight = FontWeight.Bold,
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
                                shape = AppShapes.button,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = NiyeEshoDarkBg,
                                    focusedContainerColor = NiyeEshoDarkBg,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = NiyeEshoGreenPrimary,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                ),
                            )
                            IconButton(
                                onClick = { if (titleInput.isNotBlank()) onUpdateTitle(titleInput) },
                                enabled = titleInput.isNotBlank() && titleInput != listTitle,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(AppShapes.button)
                                    .background(if (titleInput.isNotBlank() && titleInput != listTitle) NiyeEshoGreenPrimary else NiyeEshoDarkBg),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Update title",
                                    tint = if (titleInput.isNotBlank() && titleInput != listTitle) Color.White else NiyeEshoGreenDarkMuted,
                                )
                            }
                        }
                    }
                }

                item { HorizontalDivider(color = NiyeEshoDarkBg) }

                // Current Members Header
                item {
                    Text(
                        text = "Current Members (${members.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = NiyeEshoGreenPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Current Members Items
                items(members, key = { item -> item.uid }) { member ->
                    val isInContacts = member.uid in contactUids || member.uid == "user_demo"
                    val initial = member.displayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NiyeEshoDarkBg.copy(alpha = 0.5f), AppShapes.button)
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NiyeEshoGreenPrimary,
                                modifier = Modifier.size(36.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = initial,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(member.displayName, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = if (isInContacts) member.uid else "${member.uid} (Not in contacts)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isInContacts) NiyeEshoGreenDarkMuted else ErrorDark,
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
                                        tint = ErrorDark,
                                    )
                                }
                            }
                        }
                    }
                }

                if (contactsNotAlreadyInGroup.isNotEmpty()) {
                    item { HorizontalDivider(color = NiyeEshoDarkBg) }

                    // Add from Contacts Header
                    item {
                        Text(
                            text = "Add from Contacts",
                            style = MaterialTheme.typography.labelMedium,
                            color = NiyeEshoGreenPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    // Add from Contacts Items
                    items(contactsNotAlreadyInGroup, key = { contact -> contact.uid }) { contact ->
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
                                .background(NiyeEshoDarkBg.copy(alpha = 0.5f), AppShapes.button)
                                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.displayName, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
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

                item { HorizontalDivider(color = NiyeEshoDarkBg) }

                // Delete List Danger Zone
                item {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = ErrorDark),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(Spacing.md),
                        )
                        Spacer(Modifier.width(Spacing.xs))
                        Text("Delete Shopping List", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            if (contactsNotAlreadyInGroup.isNotEmpty()) {
                Button(
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
                    shape = AppShapes.button,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NiyeEshoGreenPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = NiyeEshoGreenDarkMuted,
                        disabledContentColor = Color.White.copy(alpha = 0.5f),
                    ),
                ) { Text("Add Selected Contacts", fontWeight = FontWeight.Bold) }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenLight),
            ) {
                Text(
                    text = "Close",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
    )
}
