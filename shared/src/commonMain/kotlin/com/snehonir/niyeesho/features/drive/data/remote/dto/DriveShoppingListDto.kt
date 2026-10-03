package com.snehonir.niyeesho.features.drive.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DriveShoppingListDto(
    val id: String,
    val title: String,
    val ownerId: String,
    val memberIds: List<String>,
    val members: List<DriveListMemberDto>,
    val createdAt: String,
    val updatedAt: String,
    val lastSyncedAt: String? = null,
    val items: List<DriveListItemDto>,
    val isArchived: Boolean = false,
)

@Serializable
data class DriveListMemberDto(
    val uid: String,
    val displayName: String,
    val avatarUrl: String? = null,
)

@Serializable
data class DriveListItemDto(
    val id: String,
    val name: String,
    val quantity: String? = null,
    val unit: String? = null,
    val isChecked: Boolean,
    val addedBy: String,
    val addedByName: String,
    val checkedBy: String? = null,
    val position: Int,
    val createdAt: String,
    val updatedAt: String,
)
