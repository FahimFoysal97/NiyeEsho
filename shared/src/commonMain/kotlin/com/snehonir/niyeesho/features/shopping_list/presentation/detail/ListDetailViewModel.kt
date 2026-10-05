package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.invite.domain.usecase.AddContactFromInviteUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.AddListItemUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.AddMembersToListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.CreateShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.DeleteListItemUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.DeleteShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.GetShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.RemoveMemberUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.ToggleListItemUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.UpdateShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.WatchListItemsUseCase
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ListDetailViewModel(
    private val getList: GetShoppingListUseCase,
    private val watchItems: WatchListItemsUseCase,
    private val addItem: AddListItemUseCase,
    private val toggleItem: ToggleListItemUseCase,
    private val deleteItem: DeleteListItemUseCase,
    private val generateInviteLink: GenerateInviteLinkUseCase,
    private val watchContacts: WatchContactsUseCase,
    private val addMembersToList: AddMembersToListUseCase,
    private val removeMemberUseCase: RemoveMemberUseCase,
    private val createList: CreateShoppingListUseCase,
    private val addContact: AddContactFromInviteUseCase,
    private val updateShoppingList: UpdateShoppingListUseCase,
    private val deleteShoppingList: DeleteShoppingListUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = Channel<ListDetailUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ListDetailUiEvent> = _uiEvent.receiveAsFlow()
    private var currentListId: String = ""
    private var allContacts: List<Contact> = emptyList()

    init {
        viewModelScope.launch {
            watchContacts().collect { result ->
                result.onSuccess { contacts ->
                    allContacts = contacts
                    _uiState.update { it.copy(availableContacts = contacts.toImmutableList()) }
                }
            }
        }
    }

    fun load(listId: String) {
        if (currentListId == listId && !_uiState.value.isLoading) return
        currentListId = listId
        viewModelScope.launch {
            loadListAndObserve(listId)
        }
    }

    private suspend fun loadListAndObserve(listId: String) {
        var result = getList(listId)
        if (result.isFailure && listId.startsWith("contact_list_")) {
            val contactUid = listId.removePrefix("contact_list_")
            val contact = allContacts.firstOrNull { it.uid == contactUid }
            val contactName = contact?.displayName ?: "Contact Shopping List"
            val contactMember = contact?.let { ListMember(it.uid, it.displayName, it.avatarUrl) }
            val members = listOfNotNull(contactMember)
            createList(title = contactName, ownerId = "user_demo", members = members, id = listId)
            result = getList(listId)
        }

        result.onSuccess { list ->
            _uiState.update { it.copy(list = list, isLoading = false) }
        }.onFailure { fail(it) }

        watchItems(listId).collect { itemsResult ->
            itemsResult.onSuccess { items ->
                _uiState.update {
                    it.copy(
                        activeItems = items.filterNot { item -> item.isChecked }
                            .map(ListItemUiModel::fromDomain)
                            .distinctBy { item -> item.id }
                            .toImmutableList(),
                        checkedItems = items.filter { item -> item.isChecked }
                            .map(ListItemUiModel::fromDomain)
                            .distinctBy { item -> item.id }
                            .toImmutableList(),
                    )
                }
            }.onFailure { fail(it) }
        }
    }

    fun onAction(action: ListDetailUiAction) {
        when (action) {
            is ListDetailUiAction.ToggleItem -> mutate { toggleItem(action.id, action.checked, null) }
            is ListDetailUiAction.DeleteItem -> mutate { deleteItem(action.id) }
            is ListDetailUiAction.AddItem -> add(action)
            is ListDetailUiAction.UpdateList -> updateList(action.title)
            ListDetailUiAction.DeleteList -> deleteList()
            is ListDetailUiAction.RemoveMember -> removeMember(action.memberId)
            ListDetailUiAction.InviteMember -> generateInvite()
            ListDetailUiAction.DismissInvite -> _uiState.update { it.copy(inviteLink = null) }
            is ListDetailUiAction.AddMembersConfirmed -> addMembers(action.members)
            ListDetailUiAction.OpenGroupSettings -> _uiState.update { it.copy(isGroupSettingsVisible = true) }
            ListDetailUiAction.DismissGroupSettings -> _uiState.update { it.copy(isGroupSettingsVisible = false) }
            ListDetailUiAction.OpenContactSettings -> _uiState.update { it.copy(isContactSettingsVisible = true) }
            ListDetailUiAction.DismissContactSettings -> _uiState.update { it.copy(isContactSettingsVisible = false) }
            is ListDetailUiAction.AddContactFromMember -> addContactFromMember(action.member)
            ListDetailUiAction.CreateGroupClicked -> _uiState.update { it.copy(isCreateGroupDialogVisible = true) }
            ListDetailUiAction.DismissCreateGroup -> _uiState.update { it.copy(isCreateGroupDialogVisible = false) }
            is ListDetailUiAction.CreateGroupConfirmed -> {
                _uiState.update { it.copy(isContactSettingsVisible = false) }
                createGroup(action.title, action.members)
            }
            ListDetailUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun updateList(title: String) {
        viewModelScope.launch {
            val list = _uiState.value.list ?: return@launch
            updateShoppingList(list.copy(title = title)).onSuccess {
                getList(currentListId).onSuccess { updated ->
                    _uiState.update { it.copy(list = updated, isGroupSettingsVisible = false) }
                }
            }.onFailure { fail(it) }
        }
    }

    private fun deleteList() {
        viewModelScope.launch {
            deleteShoppingList(currentListId).onSuccess {
                _uiState.update { it.copy(isGroupSettingsVisible = false) }
                _uiEvent.send(ListDetailUiEvent.ShowError("Shopping list deleted."))
            }.onFailure { fail(it) }
        }
    }

    private fun removeMember(memberId: String) {
        viewModelScope.launch {
            removeMemberUseCase(currentListId, memberId).onSuccess {
                getList(currentListId).onSuccess { updatedList ->
                    _uiState.update { it.copy(list = updatedList) }
                }
            }.onFailure { fail(it) }
        }
    }

    private fun addMembers(members: List<ListMember>) {
        if (members.isEmpty()) return
        viewModelScope.launch {
            addMembersToList(currentListId, members).onSuccess {
                _uiState.update { it.copy(isGroupSettingsVisible = false) }
                getList(currentListId).onSuccess { updatedList ->
                    _uiState.update { currentState -> currentState.copy(list = updatedList) }
                }
            }.onFailure { fail(it) }
        }
    }

    private fun addContactFromMember(member: ListMember) {
        viewModelScope.launch {
            val now = Clock.System.now()
            val contact = Contact(
                uid = member.uid,
                displayName = member.displayName,
                email = "${member.uid}@niyeesho.com",
                avatarUrl = member.avatarUrl,
                profileDriveUrl = "",
                cachedFcmToken = "",
                tokenCachedAt = now,
                connectedAt = now,
            )
            addContact(contact).onFailure { fail(it) }
        }
    }

    private fun createGroup(title: String, members: List<ListMember>) {
        viewModelScope.launch {
            createList(title = title, ownerId = "user_demo", members = members).onSuccess { createdList ->
                _uiState.update { it.copy(isCreateGroupDialogVisible = false) }
                _uiEvent.send(ListDetailUiEvent.NavigateToDetail(createdList.id))
            }.onFailure { fail(it) }
        }
    }

    private fun generateInvite() {
        viewModelScope.launch {
            val list = _uiState.value.list ?: run {
                fail(IllegalStateException("List is not loaded yet."))
                return@launch
            }
            generateInviteLink(
                InviteData.preview().copy(listId = list.id, listTitle = list.title),
            ).onSuccess { link ->
                _uiState.update { it.copy(inviteLink = link) }
                _uiEvent.send(ListDetailUiEvent.OpenInvite)
            }.onFailure { fail(it) }
        }
    }

    private fun add(action: ListDetailUiAction.AddItem) {
        val listId = currentListId
        viewModelScope.launch {
            addItem(
                ListItem(
                    id = "item_${Clock.System.now().toEpochMilliseconds()}",
                    listId = listId,
                    name = action.name,
                    quantity = action.quantity,
                    unit = action.unit,
                    isChecked = false,
                    addedBy = "user_demo",
                    addedByName = "Sneho Nir",
                    checkedBy = null,
                    position = _uiState.value.activeItems.size,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now(),
                ),
            ).onFailure { fail(it) }
        }
    }

    private fun mutate(operation: suspend () -> Result<Unit>) = viewModelScope.launch {
        operation().onFailure { fail(it) }
    }

    private fun fail(error: Throwable) {
        val message = error.message ?: "Unable to update this list."
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(ListDetailUiEvent.ShowError(message)) }
    }
}
