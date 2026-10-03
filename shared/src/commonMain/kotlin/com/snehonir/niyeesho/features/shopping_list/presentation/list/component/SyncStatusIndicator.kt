package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.utils.SyncStatus

@Composable
fun SyncStatusIndicator(status: SyncStatus, modifier: Modifier = Modifier) {
    Text(
        text = when (status) {
            SyncStatus.Synced -> "Synced"
            SyncStatus.Syncing -> "Syncing"
            SyncStatus.PendingSync -> "Pending"
            SyncStatus.SyncFailed -> "Sync failed"
            is SyncStatus.LastSynced -> "Synced"
        },
        modifier = modifier,
    )
}
