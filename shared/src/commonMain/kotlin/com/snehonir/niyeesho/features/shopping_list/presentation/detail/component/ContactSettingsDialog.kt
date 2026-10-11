package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
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
        containerColor = NiyeEshoDarkSurface,
        title = {
            Text(
                text = AppStrings.TITLE_CONTACT_DETAILS,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        },
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
                            shape = AppShapes.button,
                            color = NiyeEshoGreenPrimary,
                            modifier = Modifier.size(Spacing.xxl),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(Spacing.xl),
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                            )
                            Text(
                                text = contact.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NiyeEshoGreenDarkMuted,
                            )
                            Text(
                                text = AppStrings.LABEL_CONNECTED,
                                style = MaterialTheme.typography.bodySmall,
                                color = NiyeEshoGreenPrimary,
                            )
                        }
                    }

                    HorizontalDivider(color = NiyeEshoDarkBg)

                    Button(
                        onClick = onCreateGroupClicked,
                        modifier = Modifier.fillMaxWidth(),
                        shape = AppShapes.button,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NiyeEshoGreenPrimary,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("👥 Create Group with ${contact.displayName}", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Text(
                        text = AppStrings.LABEL_CONTACT_UNAVAILABLE,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NiyeEshoGreenDarkMuted,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenPrimary),
            ) { Text(AppStrings.ACTION_CLOSE) }
        },
    )
}
