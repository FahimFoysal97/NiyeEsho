package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact

@Composable
fun ContactSettingsDialog(
    contact: Contact?,
    onCreateGroupClicked: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text("Contact Settings & Details") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                if (contact != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(Spacing.xxl),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(Spacing.xl),
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.displayName, style = MaterialTheme.typography.titleMedium)
                            Text(contact.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Connected", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    HorizontalDivider()

                    Button(
                        onClick = onCreateGroupClicked,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("👥 Create Group with ${contact.displayName}")
                    }
                } else {
                    Text(
                        text = "Contact details unavailable.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}
