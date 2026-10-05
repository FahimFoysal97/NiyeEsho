package com.snehonir.niyeesho.features.contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.features.auth.domain.usecase.GetCurrentUserUseCase
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.usecase.RefreshContactTokenUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.RemoveContactUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.SearchContactsUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.WatchContactsUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.CreateShoppingListUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val watchLists: WatchContactsUseCase,
    private val removeContact: RemoveContactUseCase,
    private val searchContacts: SearchContactsUseCase,
    private val refreshToken: RefreshContactTokenUseCase,
    private val generateInviteLink: GenerateInviteLinkUseCase,
    private val createList: CreateShoppingListUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val tokenManager: TokenManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = Channel<ContactsUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ContactsUiEvent> = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            watchLists().collect { result ->
                result.onSuccess { contacts ->
                    _uiState.update { it.copy(contacts = contacts.distinctBy { contact -> contact.uid }.toImmutableList(), isLoading = false) }
                }.onFailure { fail(it) }
            }
        }
        generateInviteLinkOnLoad()
    }

    fun onAction(action: ContactsUiAction) {
        when (action) {
            is ContactsUiAction.QueryChanged -> queryChanged(action.query)
            is ContactsUiAction.ContactSelected -> {
                _uiState.update {
                    it.copy(
                        isCreateGroupDialogVisible = true,
                        selectedContactForGroup = action.contact,
                    )
                }
            }
            ContactsUiAction.CreateGroupClicked -> _uiState.update { it.copy(isCreateGroupDialogVisible = true, selectedContactForGroup = null) }
            is ContactsUiAction.InviteContactClicked -> startGroupCreationForContact(action.contact)
            is ContactsUiAction.CreateGroupConfirmed -> createGroupList(action.title, action.members)
            ContactsUiAction.DismissCreateGroup -> _uiState.update { it.copy(isCreateGroupDialogVisible = false, selectedContactForGroup = null) }
            is ContactsUiAction.RemoveContact -> remove(action.uid)
            is ContactsUiAction.RefreshToken -> refresh(action.uid)
            ContactsUiAction.InviteClicked -> generateInvite()
            ContactsUiAction.ScanQrClicked -> Unit
            ContactsUiAction.DismissInvite -> _uiState.update { it.copy(isInviteBottomSheetVisible = false) }
            ContactsUiAction.DismissScanner -> Unit
            ContactsUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun generateInviteLinkOnLoad() {
        viewModelScope.launch {
            val user = getCurrentUser().getOrNull()
            val inviteData = InviteData(
                uid = user?.uid ?: "user_demo",
                name = user?.displayName ?: "User",
                email = user?.email,
                token = tokenManager.getFcmToken() ?: "fcm_token_placeholder",
                driveUrl = user?.avatarUrl ?: "",
                avatar = user?.avatarUrl,
            )
            generateInviteLink(inviteData)
                .onSuccess { link ->
                    _uiState.update { it.copy(inviteLink = link) }
                }
        }
    }

    private fun startGroupCreationForContact(contact: Contact) {
        _uiState.update {
            it.copy(
                isCreateGroupDialogVisible = true,
                selectedContactForGroup = contact,
            )
        }
    }

    private fun createGroupList(title: String, members: List<ListMember>) {
        viewModelScope.launch {
            createList(
                title = title,
                ownerId = "user_demo",
                members = members,
            ).onSuccess { list ->
                _uiState.update { it.copy(isCreateGroupDialogVisible = false, selectedContactForGroup = null) }
                _uiEvent.send(ContactsUiEvent.GroupCreated(list.id))
            }.onFailure { fail(it) }
        }
    }

    private fun queryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        viewModelScope.launch {
            searchContacts(query).onSuccess { contacts ->
                _uiState.update { it.copy(contacts = contacts.distinctBy { contact -> contact.uid }.toImmutableList()) }
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
            val existing = _uiState.value.inviteLink
            if (!existing.isNullOrBlank()) {
                _uiState.update { it.copy(isInviteBottomSheetVisible = true) }
                _uiEvent.send(ContactsUiEvent.ShareInvite(existing))
            } else {
                val user = getCurrentUser().getOrNull()
                val inviteData = InviteData(
                    uid = user?.uid ?: "user_demo",
                    name = user?.displayName ?: "User",
                    email = user?.email,
                    token = tokenManager.getFcmToken() ?: "fcm_token_placeholder",
                    driveUrl = user?.avatarUrl ?: "",
                    avatar = user?.avatarUrl,
                )
                generateInviteLink(inviteData)
                    .onSuccess { link ->
                        _uiState.update { it.copy(inviteLink = link, isInviteBottomSheetVisible = true) }
                        _uiEvent.send(ContactsUiEvent.ShareInvite(link))
                    }
                    .onFailure { fail(it) }
            }
        }
    }

    private fun fail(throwable: Throwable) {
        val message = throwable.message ?: "Unable to update contacts."
        _uiState.update { it.copy(isLoading = false, error = message) }
        viewModelScope.launch { _uiEvent.send(ContactsUiEvent.ShowError(message)) }
    }
}
