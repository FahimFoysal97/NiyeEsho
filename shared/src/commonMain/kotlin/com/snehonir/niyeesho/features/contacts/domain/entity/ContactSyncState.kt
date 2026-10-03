package com.snehonir.niyeesho.features.contacts.domain.entity

sealed interface ContactSyncState {
    data object Synced : ContactSyncState
    data object Syncing : ContactSyncState
    data class Failed(val message: String) : ContactSyncState
}
