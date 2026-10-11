package com.snehonir.niyeesho.features.invite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snehonir.niyeesho.core.navigation.DeepLinkHandler
import com.snehonir.niyeesho.core.utils.TokenManager
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.auth.domain.usecase.GetCurrentUserUseCase
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import com.snehonir.niyeesho.features.invite.domain.usecase.AddContactFromInviteUseCase
import com.snehonir.niyeesho.features.messaging.domain.entity.InviteData
import com.snehonir.niyeesho.features.messaging.domain.usecase.GenerateInviteLinkUseCase
import com.snehonir.niyeesho.features.messaging.domain.usecase.SendContactRequestUseCase
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
    private val sendContactRequest: SendContactRequestUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InviteUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<InviteUiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<InviteUiEvent> = _uiEvent.receiveAsFlow()

    init {
        loadInviteLink()
    }

    private fun getValidFcmToken(): String {
        val cached = tokenManager.getFcmToken()
        if (!cached.isNullOrBlank() && cached != "fcm_token_placeholder") {
            logD("InviteVM", "getValidFcmToken() -> using cached token: ${cached.take(15)}...")
            return cached
        }
        return cached ?: "fcm_token_placeholder"
    }

    private fun loadInviteLink() {
        viewModelScope.launch {
            val user = getCurrentUser().getOrNull()
            val myName = user?.displayName?.ifBlank { null } ?: "Me"
            val myAvatar = user?.avatarUrl
            val fcmToken = getValidFcmToken()

            val inviteData = InviteData(
                uid = user?.uid ?: "user_demo",
                name = myName,
                email = user?.email,
                token = fcmToken,
                driveUrl = myAvatar ?: "",
                avatar = myAvatar,
            )
            generateInviteLink(inviteData)
                .onSuccess { link ->
                    val shortCode = "${myName.lowercase().take(4)}-${(user?.uid ?: "demo").take(4)}"
                    logD("InviteVM", "Generated invite link for $myName (token: ${fcmToken.take(15)}...): $link")
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
            is InviteUiAction.SubTabSelected -> _uiState.update { it.copy(selectedSubTab = action.index, isCameraActive = false, scannerStep = ScannerStep.SCAN, errorMessage = null) }
            InviteUiAction.SwitchToScan -> _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, isCameraActive = false, pastedLink = "", errorMessage = null) }
            InviteUiAction.StartCameraClicked -> _uiState.update { it.copy(isCameraActive = true, scannerStep = ScannerStep.SCAN, errorMessage = null) }
            InviteUiAction.StopCameraClicked -> _uiState.update { it.copy(isCameraActive = false) }
            InviteUiAction.TryAgainClicked -> _uiState.update { it.copy(isCameraActive = false, scannerStep = ScannerStep.SCAN, pastedLink = "", errorMessage = null) }
            is InviteUiAction.PastedLinkClicked -> {
                _uiState.update { it.copy(pastedLink = action.link, isCameraActive = false) }
            }
            InviteUiAction.ConnectInviteClicked -> {
                _uiState.update { it.copy(isCameraActive = false) }
                handleConnectInvite()
            }
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
                logD("InviteVM", "handleConnectInvite() -> failed to parse invite link: $link")
                _uiState.update {
                    it.copy(
                        isCameraActive = false,
                        scannerStep = ScannerStep.FAILED,
                        errorMessage = "Invalid or unrecognized invite link.",
                    )
                }
                return@launch
            }

            val friendName = invite.name.ifBlank { "Friend" }
            val friendAvatar = invite.avatar?.ifBlank { null } ?: invite.driveUrl.ifBlank { null }
            val now = Clock.System.now()
            val contactEmail = invite.email
                ?: if (invite.name.contains("@")) invite.name
                else if (invite.name.isNotBlank()) "${invite.name.lowercase().replace(" ", ".")}@gmail.com"
                else "${invite.uid}@gmail.com"

            val myUser = getCurrentUser().getOrNull()
            val myToken = getValidFcmToken()
            val myInviteData = InviteData(
                uid = myUser?.uid ?: "user_me",
                name = myUser?.displayName ?: "User B",
                email = myUser?.email,
                token = myToken,
                driveUrl = myUser?.avatarUrl ?: "",
                avatar = myUser?.avatarUrl,
            )

            logD("InviteVM", "handleConnectInvite() -> User B (${myInviteData.name}, token: ${myToken.take(15)}...) connecting to User A (${invite.uid}, name: $friendName, token: ${invite.token.take(15)}...)")

            // Save User A as PENDING_OUTGOING locally for User B
            val pendingContact = Contact(
                uid = invite.uid,
                displayName = friendName,
                email = contactEmail,
                avatarUrl = friendAvatar,
                profileDriveUrl = invite.driveUrl,
                cachedFcmToken = invite.token,
                tokenCachedAt = now,
                connectedAt = now,
                status = ContactStatus.PENDING_OUTGOING,
            )

            // Save outgoing request locally for User B
            addContactFromInvite(pendingContact).onSuccess {
                logD("InviteVM", "Successfully saved ${invite.uid} ($friendName) as PENDING_OUTGOING for User B")
            }.onFailure {
                logD("InviteVM", "Failed to save PENDING_OUTGOING contact: ${it.message}")
            }

            // Send FCM CONTACT_REQUEST message to User A
            logD("InviteVM", "Dispatching CONTACT_REQUEST via FCM to recipientUid: ${invite.uid}, recipientToken: ${invite.token.take(15)}... from senderUid: ${myInviteData.uid}")
            sendContactRequest(recipientUid = invite.uid, recipientToken = invite.token, senderData = myInviteData).onSuccess {
                logD("InviteVM", "sendContactRequest() -> FCM dispatch returned SUCCESS for recipientUid: ${invite.uid}")
            }.onFailure {
                logD("InviteVM", "sendContactRequest() -> FCM dispatch returned FAILURE: ${it.message}")
            }

            _uiState.update {
                it.copy(
                    isCameraActive = false,
                    scannerStep = ScannerStep.CONNECTED,
                    connectedFriendName = friendName,
                    connectedFriendAvatarUrl = friendAvatar,
                    errorMessage = null,
                )
            }
            kotlinx.coroutines.delay(4.seconds)
            _uiState.update { it.copy(scannerStep = ScannerStep.SCAN, pastedLink = "") }
        }
    }
}
