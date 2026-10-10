package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.snehonir.niyeesho.core.designsystem.theme.ErrorDark
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoMutedGreenText

@Composable
fun ConfirmDeleteContactDialog(
    contactName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NiyeEshoDarkSurface,
        shape = MaterialTheme.shapes.large,
        title = {
            Text(
                text = "Delete Contact",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to remove $contactName from your contacts?",
                style = MaterialTheme.typography.bodyMedium,
                color = NiyeEshoGreenDarkMuted,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorDark,
                    contentColor = Color.White,
                ),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = NiyeEshoMutedGreenText,
                ),
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
    )
}
