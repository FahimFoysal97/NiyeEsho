package com.snehonir.niyeesho.features.drive.domain.entity

import kotlinx.datetime.Instant

data class DriveContact(
    val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val profileDriveUrl: String,
    val cachedFcmToken: String,
    val tokenCachedAt: Instant,
    val connectedAt: Instant,
) {
    companion object {
        fun preview() = DriveContact(
            uid = "user_rahim",
            displayName = "Rahim Ahmed",
            email = "rahim@example.com",
            avatarUrl = null,
            profileDriveUrl = "https://drive.google.com/uc?id=rahim_profile&export=download",
            cachedFcmToken = "rahim_fcm_token",
            tokenCachedAt = Instant.parse("2026-06-15T08:00:00Z"),
            connectedAt = Instant.parse("2026-01-10T00:00:00Z"),
        )
    }
}
