package com.snehonir.niyeesho.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
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

                val existingOwnerIds = lists.map { it.ownerId }.toSet()
                val listModels = lists.map(DashboardUiModel::fromDomain)
                val contactModels = contacts.filter { it.uid !in existingOwnerIds }.map(DashboardUiModel::fromContact)

                val combined = (listModels + contactModels)
                    .sortedByDescending { it.updatedAtEpoch }
                    .toImmutableList()

                _uiState.update {
                    it.copy(
                        lists = combined,
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
            DashboardUiAction.InviteShareClicked -> _uiState.update { it.copy(isFabMenuVisible = false, isInviteDialogVisible = true) }
            DashboardUiAction.InviteDismissed -> _uiState.update { it.copy(isInviteDialogVisible = false) }
            DashboardUiAction.CreateClicked -> _uiState.update { it.copy(isCreateDialogVisible = true) }
            DashboardUiAction.CreateDismissed -> _uiState.update { it.copy(isCreateDialogVisible = false) }
            is DashboardUiAction.CreateConfirmed -> create(action.title, action.ownerId)
            is DashboardUiAction.ListClicked -> handleListClick(action.id)
            DashboardUiAction.OpenProfile -> viewModelScope.launch { _uiEvent.send(DashboardUiEvent.NavigateToProfile) }
            DashboardUiAction.SyncNowClicked -> {}
            DashboardUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun handleListClick(id: String) = viewModelScope.launch {
        if (id.startsWith("contact_list_")) {
            val existing = getShoppingList(id)
            if (existing.isFailure) {
                val contactName = id.removePrefix("contact_list_").let { "Contact Shopping List" }
                createList(contactName, "user_demo")
            }
        }
        _uiEvent.send(DashboardUiEvent.NavigateToDetail(id))
    }

    private fun create(title: String, ownerId: String) = viewModelScope.launch {
        createList(title, ownerId).onFailure { fail(it) }
        _uiState.update { it.copy(isCreateDialogVisible = false) }
    }

    private fun fail(error: Throwable) {
        val message = error.message ?: "Unable to load dashboard data."
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(DashboardUiEvent.ShowError(message)) }
    }
}
