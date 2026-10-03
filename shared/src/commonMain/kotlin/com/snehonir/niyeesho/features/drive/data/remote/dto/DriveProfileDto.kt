package com.snehonir.niyeesho.features.drive.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriveProfileDto(
    val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
    val fcmToken: String,
    val tokenUpdatedAt: String,
    val appVersion: String,
    val profileDriveUrl: String,
)
