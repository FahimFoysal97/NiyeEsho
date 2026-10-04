package com.snehonir.niyeesho.features.shopping_list.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.*
import com.snehonir.niyeesho.features.shopping_list.presentation.list.model.ShoppingListUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val watchLists: WatchShoppingListsUseCase,
    private val createList: CreateShoppingListUseCase,
    private val deleteList: DeleteShoppingListUseCase,
    private val archiveList: ArchiveShoppingListUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = Channel<ShoppingListUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ShoppingListUiEvent> = _uiEvent.receiveAsFlow()

    init {
        logD("ShoppingListVM", "ShoppingListViewModel initialized, starting watchLists collection")
        viewModelScope.launch {
            watchLists().collect { result ->
                result.onSuccess { lists ->
                    logD("ShoppingListVM", "watchLists() -> success, lists count: ${lists.size}")
                    _uiState.update {
                        it.copy(
                            lists = lists.map(ShoppingListUiModel::fromDomain)
                                .distinctBy { list -> list.id }
                                .toImmutableList(),
                            isLoading = false,
                        )
                    }
                }.onFailure { fail(it) }
            }
        }
    }

    fun onAction(action: ShoppingListUiAction) {
        logD("ShoppingListVM", "onAction called with action: $action")
        when (action) {
            ShoppingListUiAction.CreateClicked -> _uiState.update { it.copy(isCreateDialogVisible = true) }
            ShoppingListUiAction.CreateDismissed -> _uiState.update { it.copy(isCreateDialogVisible = false) }
            is ShoppingListUiAction.CreateConfirmed -> create(action.title, action.ownerId)
            is ShoppingListUiAction.ListClicked -> viewModelScope.launch {
                logD("ShoppingListVM", "Navigating to detail for list id: ${action.id}")
                _uiEvent.send(ShoppingListUiEvent.NavigateToDetail(action.id))
            }
            is ShoppingListUiAction.DeleteClicked -> mutate { deleteList(action.id) }
            is ShoppingListUiAction.ArchiveClicked -> mutate { archiveList(action.id) }
            ShoppingListUiAction.OpenContacts -> viewModelScope.launch {
                logD("ShoppingListVM", "Navigating to Contacts")
                _uiEvent.send(ShoppingListUiEvent.NavigateToContacts)
            }
            ShoppingListUiAction.OpenProfile -> viewModelScope.launch {
                logD("ShoppingListVM", "Navigating to Profile")
                _uiEvent.send(ShoppingListUiEvent.NavigateToProfile)
            }
            ShoppingListUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun create(title: String, ownerId: String) = viewModelScope.launch {
        logD("ShoppingListVM", "create() list with title: $title")
        createList(title, ownerId)
            .onSuccess { logD("ShoppingListVM", "createList -> success") }
            .onFailure { fail(it) }
        _uiState.update { it.copy(isCreateDialogVisible = false) }
    }
    private fun mutate(operation: suspend () -> Result<Unit>) = viewModelScope.launch {
        logD("ShoppingListVM", "Executing mutation operation")
        operation()
            .onSuccess { logD("ShoppingListVM", "Mutation operation -> success") }
            .onFailure { fail(it) }
    }
    private fun fail(error: Throwable) {
        val message = error.message ?: "Unable to update shopping lists."
        logD("ShoppingListVM", "ShoppingListViewModel failure: $message")
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(ShoppingListUiEvent.ShowError(message)) }
    }
}
