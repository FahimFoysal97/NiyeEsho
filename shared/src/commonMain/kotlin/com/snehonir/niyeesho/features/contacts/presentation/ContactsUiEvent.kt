package com.snehonir.niyeesho.features.contacts.presentation

sealed interface ContactsUiEvent {
    data class ShowError(val message: String) : ContactsUiEvent
    data class ShareInvite(val link: String) : ContactsUiEvent
    data class OpenContact(val uid: String) : ContactsUiEvent
    data class GroupCreated(val listId: String) : ContactsUiEvent
}
