package com.snehonir.niyeesho.features.drive.domain.entity

import kotlinx.datetime.Instant

data class DriveMetadata(
    val lastSyncedAt: Instant?,
    val folderId: String?,
    val fileIds: Map<String, String>,
) {
    companion object {
        fun preview() = DriveMetadata(
            lastSyncedAt = Instant.parse("2026-06-15T10:30:00Z"),
            folderId = "folder_demo",
            fileIds = mapOf("profile.json" to "profile_demo"),
        )
    }
}
