package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.*
import com.snehonir.niyeesho.features.shopping_list.presentation.detail.model.ListItemUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ListDetailViewModel(
    private val getList: GetShoppingListUseCase,
    private val watchItems: WatchListItemsUseCase,
    private val addItem: AddListItemUseCase,
    private val toggleItem: ToggleListItemUseCase,
    private val deleteItem: DeleteListItemUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = Channel<ListDetailUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ListDetailUiEvent> = _uiEvent.receiveAsFlow()
    private var currentListId: String = ""

    fun load(listId: String) {
        if (currentListId == listId && !_uiState.value.isLoading) return
        currentListId = listId
        viewModelScope.launch {
            getList(listId).onSuccess { list -> _uiState.update { it.copy(list = list, isLoading = false) } }
                .onFailure { fail(it) }
            watchItems(listId).collect { result ->
                result.onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            activeItems = items.filterNot { item -> item.isChecked }.map(ListItemUiModel::fromDomain).toImmutableList(),
                            checkedItems = items.filter { item -> item.isChecked }.map(ListItemUiModel::fromDomain).toImmutableList(),
                        )
                    }
                }.onFailure { fail(it) }
            }
        }
    }

    fun onAction(action: ListDetailUiAction) {
        when (action) {
            is ListDetailUiAction.ToggleItem -> mutate { toggleItem(action.id, action.checked, null) }
            is ListDetailUiAction.DeleteItem -> mutate { deleteItem(action.id) }
            is ListDetailUiAction.AddItem -> add(action)
            ListDetailUiAction.InviteMember -> viewModelScope.launch { _uiEvent.send(ListDetailUiEvent.OpenInvite) }
            ListDetailUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
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
