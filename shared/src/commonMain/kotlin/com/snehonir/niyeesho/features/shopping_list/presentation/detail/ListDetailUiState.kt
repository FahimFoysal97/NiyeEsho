package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ListDetailUiState(
    val list: ShoppingList? = null,
    val activeItems: ImmutableList<ListItemUiModel> = persistentListOf(),
    val checkedItems: ImmutableList<ListItemUiModel> = persistentListOf(),
    val availableContacts: ImmutableList<Contact> = persistentListOf(),
    val isLoading: Boolean = true,
    val isCreateGroupDialogVisible: Boolean = false,
    val isGroupSettingsVisible: Boolean = false,
    val isContactSettingsVisible: Boolean = false,
    val error: String? = null,
    val inviteLink: String? = null,
) {
    val isContactList: Boolean
        get() = list?.id?.startsWith("contact_list_") == true

    companion object {
        fun preview() = ListDetailUiState(
            list = ShoppingList.preview(),
            activeItems = persistentListOf(ListItemUiModel.fromDomain(com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem.preview())),
            isLoading = false,
        )
    }
}
