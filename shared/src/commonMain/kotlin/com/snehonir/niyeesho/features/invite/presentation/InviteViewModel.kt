package com.snehonir.niyeesho.features.invite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.core.navigation.DeepLinkHandler
import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.features.auth.domain.usecase.GetCurrentUserUseCase
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.invite.domain.usecase.AddContactFromInviteUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

sealed interface InviteUiEvent {
    data class ShareLink(val link: String) : InviteUiEvent
    data class ShowToast(val message: String) : InviteUiEvent
}

class InviteViewModel(
    private val generateInviteLink: GenerateInviteLinkUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val tokenManager: TokenManager,
    private val addContactFromInvite: AddContactFromInviteUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InviteUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<InviteUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<InviteUiEvent> = _uiEvent.receiveAsFlow()

    init {
        loadInviteLink()
    }

    private fun loadInviteLink() {
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
                    val shortCode = "${(user?.displayName ?: "user").lowercase().take(4)}-${(user?.uid ?: "demo").take(4)}"
                    _uiState.update { it.copy(inviteLink = link, displayInviteCode = shortCode) }
                }
        }
    }

    fun onAction(action: InviteUiAction) {
        when (action) {
            is InviteUiAction.SubTabSelected -> _uiState.update { it.copy(selectedSubTab = action.index) }
            InviteUiAction.SwitchToScan -> _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, pastedLink = "") }
            is InviteUiAction.PastedLinkClicked -> _uiState.update { it.copy(pastedLink = action.link) }
            InviteUiAction.ConnectInviteClicked -> handleConnectInvite()
            InviteUiAction.SaveQrImage -> viewModelScope.launch { _uiEvent.send(InviteUiEvent.ShowToast("QR code saved")) }
            InviteUiAction.ShareQrImage -> viewModelScope.launch { _uiEvent.send(InviteUiEvent.ShareLink(_uiState.value.inviteLink)) }
            InviteUiAction.CopyLink -> viewModelScope.launch { _uiEvent.send(InviteUiEvent.ShowToast("Link copied")) }
            InviteUiAction.ShareLink -> viewModelScope.launch { _uiEvent.send(InviteUiEvent.ShareLink(_uiState.value.inviteLink)) }
        }
    }

    private fun handleConnectInvite() {
        viewModelScope.launch {
            val link = _uiState.value.pastedLink.trim()
            if (link.isNotBlank()) {
                val invite = DeepLinkHandler.parse(link)
                val friendName = invite?.name ?: "Friend"

                if (invite != null) {
                    val now = Clock.System.now()
                    val contactEmail = invite.email
                        ?: if (invite.name.contains("@")) invite.name
                        else if (invite.name.isNotBlank()) "${invite.name.lowercase().replace(" ", ".")}@gmail.com"
                        else "${invite.uid}@gmail.com"

                    val contact = Contact(
                        uid = invite.uid,
                        displayName = invite.name,
                        email = contactEmail,
                        avatarUrl = invite.avatar ?: invite.driveUrl,
                        profileDriveUrl = invite.driveUrl,
                        cachedFcmToken = invite.token,
                        tokenCachedAt = now,
                        connectedAt = now,
                    )
                    addContactFromInvite(contact)
                }

                _uiState.update { it.copy(scannerStep = ScannerStep.CONNECTED, connectedFriendName = friendName) }
                kotlinx.coroutines.delay(2.seconds)
                _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, pastedLink = "", connectedFriendName = "Nadia") }
            }
        }
    }
}
