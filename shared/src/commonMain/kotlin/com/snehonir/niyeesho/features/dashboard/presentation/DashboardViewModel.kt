package com.snehonir.niyeesho.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.CreateShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.GetShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.WatchShoppingListsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val watchLists: WatchShoppingListsUseCase,
    private val watchContacts: WatchContactsUseCase,
    private val getShoppingList: GetShoppingListUseCase,
    private val createList: CreateShoppingListUseCase,
    private val generateInviteLink: GenerateInviteLinkUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<DashboardUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<DashboardUiEvent> = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                watchLists(),
                watchContacts(),
            ) { listsResult, contactsResult ->
                val lists = listsResult.getOrDefault(emptyList())
                val contacts = contactsResult.getOrDefault(emptyList())

                val existingListIds = lists.map { it.id }.toSet()
                val listModels = lists.map(DashboardUiModel::fromDomain)
                val contactModels = contacts
                    .filter { "contact_list_${it.uid}" !in existingListIds }
                    .map(DashboardUiModel::fromContact)

                val combined = (listModels + contactModels)
                    .distinctBy { it.id }
                    .sortedByDescending { it.updatedAtEpoch }
                    .toImmutableList()

                _uiState.update {
                    it.copy(
                        lists = combined,
                        availableContacts = contacts.distinctBy { contact -> contact.uid }.toImmutableList(),
                        isLoading = false,
                    )
                }
            }.collect {}
        }
    }

    fun onAction(action: DashboardUiAction) {
        when (action) {
            DashboardUiAction.FabClicked -> _uiState.update { it.copy(isFabMenuVisible = true) }
            DashboardUiAction.FabMenuDismissed -> _uiState.update { it.copy(isFabMenuVisible = false) }
            DashboardUiAction.AddGroupClicked -> _uiState.update { it.copy(isFabMenuVisible = false, isCreateDialogVisible = true) }
            DashboardUiAction.InviteShareClicked -> generateInvite()
            DashboardUiAction.InviteDismissed -> _uiState.update { it.copy(isInviteDialogVisible = false, inviteLink = null) }
            DashboardUiAction.CreateClicked -> _uiState.update { it.copy(isCreateDialogVisible = true) }
            DashboardUiAction.CreateDismissed -> _uiState.update { it.copy(isCreateDialogVisible = false) }
            is DashboardUiAction.CreateConfirmed -> create(action.title, action.ownerId, action.members)
            is DashboardUiAction.ListClicked -> handleListClick(action.id)
            DashboardUiAction.OpenProfile -> viewModelScope.launch { _uiEvent.send(DashboardUiEvent.NavigateToProfile) }
            DashboardUiAction.SyncNowClicked -> {}
            DashboardUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun generateInvite() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFabMenuVisible = false) }
            generateInviteLink(InviteData.preview())
                .onSuccess { link ->
                    _uiState.update { it.copy(inviteLink = link, isInviteDialogVisible = true) }
                }
                .onFailure { fail(it) }
        }
    }

    private fun handleListClick(id: String) = viewModelScope.launch {
        if (id.startsWith("contact_list_")) {
            val existing = getShoppingList(id)
            if (existing.isFailure) {
                val contactUid = id.removePrefix("contact_list_")
                val contact = _uiState.value.availableContacts.firstOrNull { it.uid == contactUid }
                val contactName = contact?.displayName ?: "Contact Shopping List"
                val contactMember = contact?.let { ListMember(it.uid, it.displayName, it.avatarUrl) }
                val members = listOfNotNull(contactMember)
                createList(title = contactName, ownerId = "user_demo", members = members, id = id)
            }
        }
        _uiEvent.send(DashboardUiEvent.NavigateToDetail(id))
    }

    private fun create(title: String, ownerId: String, members: List<ListMember> = emptyList()) = viewModelScope.launch {
        createList(title, ownerId, members)
            .onSuccess { createdList ->
                _uiState.update { it.copy(isCreateDialogVisible = false) }
                _uiEvent.send(DashboardUiEvent.NavigateToDetail(createdList.id))
            }
            .onFailure { fail(it) }
    }

    private fun fail(error: Throwable) {
        val message = error.message ?: "Unable to load dashboard data."
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(DashboardUiEvent.ShowError(message)) }
    }
}
