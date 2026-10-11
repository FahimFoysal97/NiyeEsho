package com.snehonir.niyeesho.features.invite.presentation

enum class ScannerStep { SCAN, CONNECTED, FAILED }

data class InviteUiState(
    val inviteLink: String = "niyeesho.app/invite?uid=user_demo&token=demo_token",
    val displayInviteCode: String = "maria-4f7K",
    val selectedSubTab: Int = 0, // 0 = My code, 1 = Scan
    val scannerStep: ScannerStep = ScannerStep.SCAN,
    val isCameraActive: Boolean = false,
    val pastedLink: String = "",
    val myName: String = "User",
    val myAvatarUrl: String? = null,
    val connectedFriendName: String = "Nadia",
    val connectedFriendAvatarUrl: String? = null,
    val errorMessage: String? = null,
)
