package com.snehonir.niyeesho.features.shopping_list.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListItem
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.AddListItemUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.DeleteListItemUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.GetShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.ToggleListItemUseCase
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
            getList(listId).onSuccess { list ->
                _uiState.update { it.copy(list = list, isLoading = false) }
            }.onFailure { fail(it) }
            watchItems(listId).collect { result ->
                result.onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            activeItems = items.filterNot { item -> item.isChecked }
                                .map(ListItemUiModel::fromDomain).toImmutableList(),
                            checkedItems = items.filter { item -> item.isChecked }
                                .map(ListItemUiModel::fromDomain).toImmutableList(),
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
            ListDetailUiAction.InviteMember -> generateInvite()
            ListDetailUiAction.DismissInvite -> _uiState.update { it.copy(inviteLink = null) }
            ListDetailUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
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