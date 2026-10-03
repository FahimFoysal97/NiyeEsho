package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.presentation.component.QrCodeDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteMemberSheet(
    inviteLink: String,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier.fillMaxWidth().padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text("Invite someone", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Share this link or scan the QR code with a family member or friend so they can join this list.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            QrCodeDisplay(
                value = inviteLink,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                inviteLink,
                modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Button(onClick = onShare, modifier = Modifier.fillMaxWidth()) {
                Text("Share invite link")
            }
            OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Not now")
            }
        }
    }
}
