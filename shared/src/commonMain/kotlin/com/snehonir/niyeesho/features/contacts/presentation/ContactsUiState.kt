package com.snehonir.niyeesho.features.contacts.presentation

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ContactsUiState(
    val contacts: ImmutableList<Contact> = persistentListOf(),
    val query: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isCreateGroupDialogVisible: Boolean = false,
    val selectedContactForGroup: Contact? = null,
    val error: String? = null,
    val inviteLink: String? = null,
    val isInviteBottomSheetVisible: Boolean = false,
) {
    companion object {
        fun preview() = ContactsUiState(
            contacts = persistentListOf(Contact.preview()),
            isLoading = false,
        )
    }
}
