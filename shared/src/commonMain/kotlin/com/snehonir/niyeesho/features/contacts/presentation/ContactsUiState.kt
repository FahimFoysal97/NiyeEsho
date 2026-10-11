package com.snehonir.niyeesho.features.contacts.presentation

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class ContactsTab(val label: String) {
    CONTACTS("Contacts"),
    REQUESTS("Requests"),
    PENDING("Pending"),
}

data class ContactsUiState(
    val contacts: ImmutableList<Contact> = persistentListOf(),
    val pendingIncomingContacts: ImmutableList<Contact> = persistentListOf(),
    val pendingOutgoingContacts: ImmutableList<Contact> = persistentListOf(),
    val query: String = "",
    val selectedTab: ContactsTab = ContactsTab.CONTACTS,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isCreateGroupDialogVisible: Boolean = false,
    val selectedContactForGroup: Contact? = null,
    val error: String? = null,
    val inviteLink: String? = null,
    val isInviteBottomSheetVisible: Boolean = false,
    val processingUids: Set<String> = emptySet(),
) {
    companion object {
        fun preview() = ContactsUiState(
            contacts = persistentListOf(Contact.preview()),
            isLoading = false,
        )
    }
}
