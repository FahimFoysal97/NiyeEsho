package com.snehonir.niyeesho.features.drive.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriveMetadataDto(
    val lastSyncedAt: String? = null,
    val folderId: String? = null,
    val fileIds: Map<String, String> = emptyMap(),
)
