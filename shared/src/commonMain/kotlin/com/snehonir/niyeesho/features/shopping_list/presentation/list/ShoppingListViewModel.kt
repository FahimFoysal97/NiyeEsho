package com.snehonir.niyeesho.features.shopping_list.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        viewModelScope.launch {
            watchLists().collect { result ->
                result.onSuccess { lists ->
                    _uiState.update { it.copy(lists = lists.map(ShoppingListUiModel::fromDomain).toImmutableList(), isLoading = false) }
                }.onFailure { fail(it) }
            }
        }
    }

    fun onAction(action: ShoppingListUiAction) {
        when (action) {
            ShoppingListUiAction.CreateClicked -> _uiState.update { it.copy(isCreateDialogVisible = true) }
            ShoppingListUiAction.CreateDismissed -> _uiState.update { it.copy(isCreateDialogVisible = false) }
            is ShoppingListUiAction.CreateConfirmed -> create(action.title, action.ownerId)
            is ShoppingListUiAction.ListClicked -> viewModelScope.launch { _uiEvent.send(ShoppingListUiEvent.NavigateToDetail(action.id)) }
            is ShoppingListUiAction.DeleteClicked -> mutate { deleteList(action.id) }
            is ShoppingListUiAction.ArchiveClicked -> mutate { archiveList(action.id) }
            ShoppingListUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun create(title: String, ownerId: String) = viewModelScope.launch {
        createList(title, ownerId).onFailure { fail(it) }
        _uiState.update { it.copy(isCreateDialogVisible = false) }
    }
    private fun mutate(operation: suspend () -> Result<Unit>) = viewModelScope.launch {
        operation().onFailure { fail(it) }
    }
    private fun fail(error: Throwable) {
        val message = error.message ?: "Unable to update shopping lists."
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(ShoppingListUiEvent.ShowError(message)) }
    }
}
