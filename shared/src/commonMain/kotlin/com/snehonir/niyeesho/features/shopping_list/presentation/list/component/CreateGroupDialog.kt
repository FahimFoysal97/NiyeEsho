package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AppTextField
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoCheckboxBorder
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenLight
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoShapes
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
        shape = RoundedCornerShape(20.dp),
        containerColor = NiyeEshoDarkSurface,
        title = {
            Text(
                text = "Create Group List",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        },
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
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = NiyeEshoGreenLight,
                )

                if (uniqueContacts.isNotEmpty()) {
                    Text(
                        text = "Add contacts to this group:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NiyeEshoGreenLight,
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
                                    Text(
                                        text = contact.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                    Text(
                                        text = contact.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NiyeEshoGreenLight,
                                    )
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val selectedMembers = uniqueContacts
                            .filter { selectedUids.contains(it.uid) }
                            .map { ListMember(uid = it.uid, displayName = it.displayName, avatarUrl = it.avatarUrl) }
                        onConfirm(title.trim(), selectedMembers)
                    }
                },
                enabled = title.isNotBlank(),
                shape = NiyeEshoShapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NiyeEshoGreenPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = NiyeEshoGreenDarkMuted,
                    disabledContentColor = Color.White.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = "Create Group",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenLight),
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
    )
}
