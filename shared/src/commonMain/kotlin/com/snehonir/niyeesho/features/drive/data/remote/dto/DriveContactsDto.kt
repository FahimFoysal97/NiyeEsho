package com.snehonir.niyeesho.features.drive.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriveContactsDto(
    val lastUpdatedAt: String,
    val contacts: List<DriveContactDto>,
)

@Serializable
data class DriveContactDto(
    val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
    val profileDriveUrl: String,
    val cachedFcmToken: String,
    val tokenCachedAt: String,
    val connectedAt: String,
)
