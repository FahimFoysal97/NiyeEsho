package com.snehonir.niyeesho.features.drive.domain.entity

import kotlinx.datetime.Instant

data class DriveProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val fcmToken: String,
    val tokenUpdatedAt: Instant,
    val appVersion: String,
    val profileDriveUrl: String,
) {
    companion object {
        fun preview() = DriveProfile(
            uid = "user_demo",
            displayName = "Sneho Nir",
            email = "sneho@example.com",
            avatarUrl = null,
            fcmToken = "demo_fcm_token",
            tokenUpdatedAt = Instant.parse("2026-06-15T10:00:00Z"),
            appVersion = "1.0.0",
            profileDriveUrl = "https://drive.google.com/uc?id=profile_demo&export=download",
        )
    }
}
