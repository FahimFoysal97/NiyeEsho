package com.snehonir.niyeesho.core.utils

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed interface SyncStatus {
    @Serializable
    data object Synced : SyncStatus
    @Serializable
    data object Syncing : SyncStatus
    @Serializable
    data object PendingSync : SyncStatus
    @Serializable
    data object SyncFailed : SyncStatus
    @Serializable
    data class LastSynced(val at: Instant) : SyncStatus
}
