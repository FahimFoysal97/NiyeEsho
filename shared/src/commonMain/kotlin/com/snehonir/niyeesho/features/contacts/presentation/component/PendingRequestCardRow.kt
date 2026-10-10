package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoBrightRed
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoMutedGreenText
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact

@Composable
fun PendingRequestCardRow(
    contact: Contact,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onBlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = NiyeEshoDarkSurface),
        border = BorderStroke(1.dp, NiyeEshoGreenPrimary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.weight(1f),
            ) {
                AvatarImage(
                    avatarUrl = contact.avatarUrl,
                    contentDescription = contact.displayName,
                    size = 44.dp,
                    backgroundColor = NiyeEshoGreenPrimary,
                )
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = contact.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Wants to connect",
                        style = MaterialTheme.typography.bodySmall,
                        color = NiyeEshoGreenMuted,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Button(
                    onClick = onAccept,
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = NiyeEshoGreenPrimary),
                    contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xs),
                ) {
                    Text("Accept", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onDecline,
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = Spacing.xs, vertical = Spacing.xs),
                ) {
                    Text("Decline", style = MaterialTheme.typography.labelSmall, color = NiyeEshoMutedGreenText)
                }

                IconButton(onClick = onBlock, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Block",
                        tint = NiyeEshoBrightRed,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
