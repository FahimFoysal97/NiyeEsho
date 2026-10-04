package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.snehonir.niyeesho.core.utils.SyncStatus

@Composable
fun SyncStatusIndicator(status: SyncStatus, modifier: Modifier = Modifier) {
    val tint = when (status) {
        SyncStatus.Synced -> MaterialTheme.colorScheme.primary
        SyncStatus.Syncing -> MaterialTheme.colorScheme.secondary
        SyncStatus.PendingSync -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        SyncStatus.SyncFailed -> MaterialTheme.colorScheme.error
        is SyncStatus.LastSynced -> MaterialTheme.colorScheme.primary
    }

    val isDoubleCheck = status is SyncStatus.Synced || status is SyncStatus.LastSynced
    val isFailed = status is SyncStatus.SyncFailed

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((-4).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isFailed) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Sync failed",
                modifier = Modifier.size(16.dp),
                tint = tint,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Status check",
                modifier = Modifier.size(16.dp),
                tint = tint,
            )
            if (isDoubleCheck) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Status double check",
                    modifier = Modifier.size(16.dp),
                    tint = tint,
                )
            }
        }
    }
}
