package com.snehonir.niyeesho.features.contacts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.auth.domain.usecase.GetCurrentUserUseCase
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository
import com.snehonir.niyeesho.features.contacts.domain.usecase.RefreshContactTokenUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.RemoveContactUseCase
import com.snehonir.niyeesho.features.contacts.domain.usecase.SearchContactsUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.ContactHandshakeAction
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.repository.MessagingRepository
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.SendFcmMessageUseCase
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
import kotlinx.datetime.Clock

class ContactsViewModel(
    private val contactsRepository: ContactsRepository,
    private val removeContact: RemoveContactUseCase,
    private val searchContacts: SearchContactsUseCase,
    private val refreshToken: RefreshContactTokenUseCase,
    private val generateInviteLink: GenerateInviteLinkUseCase,
    private val createList: CreateShoppingListUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val tokenManager: TokenManager,
    private val sendFcmMessage: SendFcmMessageUseCase,
    private val messagingRepository: MessagingRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = Channel<ContactsUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<ContactsUiEvent> = _uiEvent.receiveAsFlow()

    init {
        // Watch active contacts
        viewModelScope.launch {
            contactsRepository.watchActiveContacts().collect { result ->
                result.onSuccess { contacts ->
                    _uiState.update { it.copy(contacts = contacts.distinctBy { c -> c.uid }.toImmutableList(), isLoading = false) }
                }.onFailure { fail(it) }
            }
        }
        // Watch incoming pending requests
        viewModelScope.launch {
            contactsRepository.watchPendingIncomingContacts().collect { result ->
                result.onSuccess { pending ->
                    _uiState.update { it.copy(pendingIncomingContacts = pending.distinctBy { c -> c.uid }.toImmutableList()) }
                }
            }
        }
        // Watch outgoing pending requests
        viewModelScope.launch {
            contactsRepository.watchPendingOutgoingContacts().collect { result ->
                result.onSuccess { pending ->
                    _uiState.update { it.copy(pendingOutgoingContacts = pending.distinctBy { c -> c.uid }.toImmutableList()) }
                }
            }
        }
        // Initial handshake check on screen load
        viewModelScope.launch {
            val user = getCurrentUser().getOrNull()
            val myUid = user?.uid ?: "user_me"
            messagingRepository.fetchPendingHandshakes(myUid)
        }
        generateInviteLinkOnLoad()
    }

    fun onAction(action: ContactsUiAction) {
        when (action) {
            is ContactsUiAction.QueryChanged -> queryChanged(action.query)
            is ContactsUiAction.TabSelected -> {
                _uiState.update { it.copy(selectedTab = action.tab) }
                viewModelScope.launch {
                    val user = getCurrentUser().getOrNull()
                    val myUid = user?.uid ?: "user_me"
                    messagingRepository.fetchPendingHandshakes(myUid)
                }
            }
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
            is ContactsUiAction.AcceptContactRequest -> acceptRequest(action.contact)
            is ContactsUiAction.DeclineContactRequest -> declineRequest(action.contact)
            is ContactsUiAction.BlockContact -> block(action.contact)
            is ContactsUiAction.RefreshToken -> refresh(action.uid)
            ContactsUiAction.InviteClicked -> generateInvite()
            ContactsUiAction.ScanQrClicked -> Unit
            ContactsUiAction.DismissInvite -> _uiState.update { it.copy(isInviteBottomSheetVisible = false) }
            ContactsUiAction.DismissScanner -> Unit
            ContactsUiAction.ErrorDismissed -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun acceptRequest(contact: Contact) {
        viewModelScope.launch {
            if (_uiState.value.processingUids.contains(contact.uid)) return@launch
            logD("ContactsVM", "acceptRequest() -> accepting request from ${contact.displayName} (uid: ${contact.uid})")
            _uiState.update { it.copy(processingUids = it.processingUids + contact.uid) }
            val user = getCurrentUser().getOrNull()
            val myUid = user?.uid ?: "user_me"

            try {
                // 1. Delete incoming request node from Realtime Database
                messagingRepository.deleteHandshakeRequest(myUid, contact.uid)

                // 2. Update local contact status to ACTIVE
                contactsRepository.updateStatus(contact.uid, ContactStatus.ACTIVE).onFailure { fail(it) }

                // 3. Dispatch CONTACT_ACCEPTED handshake response to User A's RTDB node so User A updates to ACTIVE
                val message = FcmMessage(
                    type = FcmMessageType.CONTACT_ACCEPTED,
                    senderId = myUid,
                    senderName = user?.displayName ?: "User",
                    listId = null,
                    timestamp = Clock.System.now(),
                    data = FcmMessageData.ContactHandshake(action = ContactHandshakeAction.ACCEPT),
                )
                messagingRepository.send(contact.uid, contact.cachedFcmToken, message)
            } finally {
                _uiState.update { it.copy(processingUids = it.processingUids - contact.uid) }
            }
        }
    }

    private fun declineRequest(contact: Contact) {
        viewModelScope.launch {
            if (_uiState.value.processingUids.contains(contact.uid)) return@launch
            logD("ContactsVM", "declineRequest() -> declining request from ${contact.displayName} (uid: ${contact.uid})")
            _uiState.update { it.copy(processingUids = it.processingUids + contact.uid) }
            val user = getCurrentUser().getOrNull()
            val myUid = user?.uid ?: "user_me"

            try {
                // 1. Delete incoming request node from Realtime Database
                messagingRepository.deleteHandshakeRequest(myUid, contact.uid)

                // 2. Remove contact from local storage on decline
                contactsRepository.removeContact(contact.uid).onFailure { fail(it) }

                // 3. Dispatch CONTACT_DECLINED handshake response to User A's RTDB node so User A removes User B
                val message = FcmMessage(
                    type = FcmMessageType.CONTACT_DECLINED,
                    senderId = myUid,
                    senderName = user?.displayName ?: "User",
                    listId = null,
                    timestamp = Clock.System.now(),
                    data = FcmMessageData.ContactHandshake(action = ContactHandshakeAction.DECLINE),
                )
                messagingRepository.send(contact.uid, contact.cachedFcmToken, message)
            } finally {
                _uiState.update { it.copy(processingUids = it.processingUids - contact.uid) }
            }
        }
    }

    private fun block(contact: Contact) {
        viewModelScope.launch {
            if (_uiState.value.processingUids.contains(contact.uid)) return@launch
            logD("ContactsVM", "block() -> blocking contact ${contact.displayName} (uid: ${contact.uid})")
            _uiState.update { it.copy(processingUids = it.processingUids + contact.uid) }
            val user = getCurrentUser().getOrNull()
            val myUid = user?.uid ?: "user_me"

            try {
                messagingRepository.deleteHandshakeRequest(myUid, contact.uid)
                contactsRepository.removeContact(contact.uid).onFailure { fail(it) }
            } finally {
                _uiState.update { it.copy(processingUids = it.processingUids - contact.uid) }
            }
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
                _uiState.update { it.copy(contacts = contacts.distinctBy { c -> c.uid }.toImmutableList()) }
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
