package com.snehonir.niyeesho.features.invite.presentation

enum class ScannerStep { SCAN, CONNECTED }

data class InviteUiState(
    val inviteLink: String = "niyeesho.app/invite?uid=user_demo&token=demo_token",
    val displayInviteCode: String = "maria-4f7K",
    val selectedSubTab: Int = 0, // 0 = My code, 1 = Scan
    val scannerStep: ScannerStep = ScannerStep.SCAN,
    val pastedLink: String = "",
    val connectedFriendName: String = "Nadia",
)
