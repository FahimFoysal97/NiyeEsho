package com.snehonir.niyeesho.features.contacts.presentation

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember

sealed interface ContactsUiAction {
    data class QueryChanged(val query: String) : ContactsUiAction
    data class ContactSelected(val contact: Contact) : ContactsUiAction
    data object CreateGroupClicked : ContactsUiAction
    data class InviteContactClicked(val contact: Contact) : ContactsUiAction
    data class CreateGroupConfirmed(val title: String, val members: List<ListMember>) : ContactsUiAction
    data object DismissCreateGroup : ContactsUiAction
    data class RemoveContact(val uid: String) : ContactsUiAction
    data class RefreshToken(val uid: String) : ContactsUiAction
    data object InviteClicked : ContactsUiAction
    data object ScanQrClicked : ContactsUiAction
    data object DismissInvite : ContactsUiAction
    data object DismissScanner : ContactsUiAction
    data object ErrorDismissed : ContactsUiAction
}
