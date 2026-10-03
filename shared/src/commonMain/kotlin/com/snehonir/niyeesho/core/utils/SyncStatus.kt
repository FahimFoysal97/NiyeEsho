package com.snehonir.niyeesho.core.utils

import kotlinx.datetime.Instant

sealed interface SyncStatus {
    data object Synced : SyncStatus
    data object Syncing : SyncStatus
    data object PendingSync : SyncStatus
    data object SyncFailed : SyncStatus
    data class LastSynced(val at: Instant) : SyncStatus
}
