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
            val myName = user?.displayName?.ifBlank { null } ?: "Me"
            val myAvatar = user?.avatarUrl

            val inviteData = InviteData(
                uid = user?.uid ?: "user_demo",
                name = myName,
                email = user?.email,
                token = tokenManager.getFcmToken() ?: "fcm_token_placeholder",
                driveUrl = myAvatar ?: "",
                avatar = myAvatar,
            )
            generateInviteLink(inviteData)
                .onSuccess { link ->
                    val shortCode = "${myName.lowercase().take(4)}-${(user?.uid ?: "demo").take(4)}"
                    _uiState.update {
                        it.copy(
                            inviteLink = link,
                            displayInviteCode = shortCode,
                            myName = myName,
                            myAvatarUrl = myAvatar,
                        )
                    }
                }
        }
    }

    fun onAction(action: InviteUiAction) {
        when (action) {
            is InviteUiAction.SubTabSelected -> _uiState.update { it.copy(selectedSubTab = action.index, scannerStep = ScannerStep.SCAN, errorMessage = null) }
            InviteUiAction.SwitchToScan -> _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, pastedLink = "", errorMessage = null) }
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
            if (link.isBlank()) return@launch

            val invite = DeepLinkHandler.parse(link)
            if (invite == null) {
                _uiState.update {
                    it.copy(
                        scannerStep = ScannerStep.FAILED,
                        errorMessage = "Invalid or unrecognized invite link.",
                    )
                }
                kotlinx.coroutines.delay(4.seconds)
                _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, errorMessage = null) }
                return@launch
            }

            val friendName = invite.name.ifBlank { "Friend" }
            val friendAvatar = invite.avatar?.ifBlank { null } ?: invite.driveUrl.ifBlank { null }
            val now = Clock.System.now()
            val contactEmail = invite.email
                ?: if (invite.name.contains("@")) invite.name
                else if (invite.name.isNotBlank()) "${invite.name.lowercase().replace(" ", ".")}@gmail.com"
                else "${invite.uid}@gmail.com"

            val contact = Contact(
                uid = invite.uid,
                displayName = friendName,
                email = contactEmail,
                avatarUrl = friendAvatar,
                profileDriveUrl = invite.driveUrl,
                cachedFcmToken = invite.token,
                tokenCachedAt = now,
                connectedAt = now,
            )

            val result = addContactFromInvite(contact)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            scannerStep = ScannerStep.CONNECTED,
                            connectedFriendName = friendName,
                            connectedFriendAvatarUrl = friendAvatar,
                            errorMessage = null,
                        )
                    }
                    kotlinx.coroutines.delay(4.seconds)
                    _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, pastedLink = "") }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            scannerStep = ScannerStep.FAILED,
                            errorMessage = error.message ?: "Failed to save connection.",
                        )
                    }
                    kotlinx.coroutines.delay(4.seconds)
                    _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, errorMessage = null) }
                },
            )
        }
    }
}
