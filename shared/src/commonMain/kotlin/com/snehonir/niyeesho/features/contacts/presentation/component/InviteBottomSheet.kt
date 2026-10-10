package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.theme.AppShapes
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteBottomSheet(
    inviteLink: String,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NiyeEshoDarkSurface,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = "Invite a Friend or Family",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
            )
            Text(
                text = "Share this link or scan the QR code to connect and sync shopping lists.",
                style = MaterialTheme.typography.bodyMedium,
                color = NiyeEshoGreenMuted,
            )
            QrCodeDisplay(
                value = inviteLink,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = inviteLink,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.sm),
                style = MaterialTheme.typography.bodySmall,
                color = NiyeEshoGreenPrimary,
            )
            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NiyeEshoGreenPrimary,
                    contentColor = Color.White,
                ),
            ) {
                Text("Share Invite Link", style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.button,
            ) {
                Text("Close", style = MaterialTheme.typography.bodyMedium, color = NiyeEshoGreenDarkMuted)
            }
        }
    }
}
