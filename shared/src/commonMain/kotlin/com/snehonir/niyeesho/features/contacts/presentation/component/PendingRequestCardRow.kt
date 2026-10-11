package com.snehonir.niyeesho.features.contacts.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.designsystem.component.AvatarImage
import com.snehonir.niyeesho.core.designsystem.theme.ActionAcceptBorder
import com.snehonir.niyeesho.core.designsystem.theme.ActionAcceptIcon
import com.snehonir.niyeesho.core.designsystem.theme.ActionAcceptSurface
import com.snehonir.niyeesho.core.designsystem.theme.ActionBlockBorder
import com.snehonir.niyeesho.core.designsystem.theme.ActionBlockIcon
import com.snehonir.niyeesho.core.designsystem.theme.ActionBlockSurface
import com.snehonir.niyeesho.core.designsystem.theme.ActionDeclineBorder
import com.snehonir.niyeesho.core.designsystem.theme.ActionDeclineIcon
import com.snehonir.niyeesho.core.designsystem.theme.ActionDeclineSurface
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact

@Composable
fun PendingRequestCardRow(
    contact: Contact,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onBlock: () -> Unit,
    modifier: Modifier = Modifier,
    isProcessing: Boolean = false,
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
                        text = AppStrings.LABEL_WANTS_TO_CONNECT,
                        style = MaterialTheme.typography.bodySmall,
                        color = NiyeEshoGreenMuted,
                    )
                }
            }

            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .padding(horizontal = Spacing.xs),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = NiyeEshoGreenPrimary,
                        strokeWidth = 2.5.dp,
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 1. Accept Icon Button (Dark Green Surface + Green Border + Emerald Check)
                    IconButton(
                        onClick = onAccept,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ActionAcceptSurface)
                            .border(BorderStroke(1.dp, ActionAcceptBorder), CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = AppStrings.ACTION_ACCEPT,
                            tint = ActionAcceptIcon,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    // 2. Reject / Decline Icon Button (Dark Amber Surface + Gold Border + Amber Close 'X')
                    IconButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ActionDeclineSurface)
                            .border(BorderStroke(1.dp, ActionDeclineBorder), CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.ACTION_DECLINE,
                            tint = ActionDeclineIcon,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    // 3. Block Icon Button (Dark Crimson Surface + Red Border + Bright Red Lock)
                    IconButton(
                        onClick = onBlock,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ActionBlockSurface)
                            .border(BorderStroke(1.dp, ActionBlockBorder), CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = AppStrings.ACTION_BLOCK,
                            tint = ActionBlockIcon,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}
