package com.snehonir.niyeesho.features.contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.features.contacts.domain.usecase.RefreshContactTokenUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.RemoveContactUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.SearchContactsUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val watchContacts: WatchContactsUseCase,
    private val removeContact: RemoveContactUseCase,
    private val searchContacts: SearchContactsUseCase,
    private val refreshToken: RefreshContactTokenUseCase,
    private val generateInviteLink: GenerateInviteLinkUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = Channel<ContactsUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ContactsUiEvent> = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            watchContacts().collect { result ->
                result.onSuccess { contacts ->
                    _uiState.update { it.copy(contacts = contacts.toImmutableList(), isLoading = false) }
                }.onFailure { fail(it) }
            }
        }
    }

    fun onAction(action: ContactsUiAction) {
        when (action) {
            is ContactsUiAction.QueryChanged -> queryChanged(action.query)
            is ContactsUiAction.ContactSelected -> openContact(action.contact.uid)
            is ContactsUiAction.RemoveContact -> remove(action.uid)
            is ContactsUiAction.RefreshToken -> refresh(action.uid)
            ContactsUiAction.InviteClicked -> generateInvite()
            ContactsUiAction.ScanQrClicked -> Unit
            ContactsUiAction.DismissInvite -> _uiState.update { it.copy(inviteLink = null) }
            ContactsUiAction.DismissScanner -> Unit
            ContactsUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun queryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        viewModelScope.launch {
            searchContacts(query).onSuccess { contacts ->
                _uiState.update { it.copy(contacts = contacts.toImmutableList()) }
            }.onFailure { fail(it) }
        }
    }

    private fun remove(uid: String) {
        viewModelScope.launch {
            removeContact(uid).onFailure { fail(it) }
        }
    }

    private fun refresh(uid: String) {
    viewModelScope.launch {
        refreshToken(uid).onFailure { fail(it) }
    }
    }

    private fun generateInvite() {
        viewModelScope.launch {
            generateInviteLink(InviteData.preview())
                .onSuccess { link ->
                    _uiState.update { it.copy(inviteLink = link) }
                    _uiEvent.send(ContactsUiEvent.ShareInvite(link))
                }
                .onFailure { fail(it) }
        }
    }

    private fun openContact(uid: String) {
        viewModelScope.launch { _uiEvent.send(ContactsUiEvent.OpenContact(uid)) }
    }

    private fun fail(throwable: Throwable) {
        val message = throwable.message ?: "Unable to update contacts."
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(ContactsUiEvent.ShowError(message)) }
    }
}
