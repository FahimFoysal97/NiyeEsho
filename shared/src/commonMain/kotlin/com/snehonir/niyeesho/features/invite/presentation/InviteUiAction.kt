package com.snehonir.niyeesho.features.invite.presentation

sealed interface InviteUiAction {
    data class SubTabSelected(val index: Int) : InviteUiAction
    data object SwitchToPasteLink : InviteUiAction
    data object SwitchToScan : InviteUiAction
    data class PastedLinkClicked(val link: String) : InviteUiAction
    data object ConnectInviteClicked : InviteUiAction
    data object SaveQrImage : InviteUiAction
    data object ShareQrImage : InviteUiAction
    data object CopyLink : InviteUiAction
    data object ShareLink : InviteUiAction
}
