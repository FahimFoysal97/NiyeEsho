package com.snehonir.niyeesho.features.drive.data.mapper

import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveContactDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveListItemDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveListMemberDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveMetadataDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveProfileDto
import com.snehonir.niyeesho.features.drive.data.remote.dto.DriveShoppingListDto
import com.snehonir.niyeesho.features.drive.domain.entity.DriveContact
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListItem
import com.snehonir.niyeesho.features.drive.domain.entity.DriveListMember
import com.snehonir.niyeesho.features.drive.domain.entity.DriveMetadata
import com.snehonir.niyeesho.features.drive.domain.entity.DriveProfile
import com.snehonir.niyeesho.features.drive.domain.entity.DriveShoppingList
import kotlinx.datetime.Instant

fun DriveProfileDto.toDomain() = DriveProfile(
    uid, displayName, email, avatarUrl, fcmToken, Instant.parse(tokenUpdatedAt), appVersion, profileDriveUrl,
)

fun DriveProfile.toDto() = DriveProfileDto(
    uid, displayName, email, avatarUrl, fcmToken, tokenUpdatedAt.toString(), appVersion, profileDriveUrl,
)

fun DriveMetadataDto.toDomain() = DriveMetadata(lastSyncedAt?.let(Instant::parse), folderId, fileIds)

fun DriveShoppingListDto.toDomain() = DriveShoppingList(
    id, title, ownerId, memberIds, members.map(DriveListMemberDto::toDomain),
    Instant.parse(createdAt), Instant.parse(updatedAt), lastSyncedAt?.let(Instant::parse),
    items.map(DriveListItemDto::toDomain), isArchived,
)

fun DriveShoppingList.toDto() = DriveShoppingListDto(
    id, title, ownerId, memberIds, members.map(DriveListMember::toDto),
    createdAt.toString(), updatedAt.toString(), lastSyncedAt?.toString(),
    items.map(DriveListItem::toDto), isArchived,
)

fun DriveListMemberDto.toDomain() = DriveListMember(uid, displayName, avatarUrl)
fun DriveListMember.toDto() = DriveListMemberDto(uid, displayName, avatarUrl)

fun DriveListItemDto.toDomain() = DriveListItem(
    id, name, quantity, unit, isChecked, addedBy, addedByName, checkedBy, position,
    Instant.parse(createdAt), Instant.parse(updatedAt),
)

fun DriveListItem.toDto() = DriveListItemDto(
    id, name, quantity, unit, isChecked, addedBy, addedByName, checkedBy, position,
    createdAt.toString(), updatedAt.toString(),
)

fun DriveContactDto.toDomain() = DriveContact(
    uid, displayName, email, avatarUrl, profileDriveUrl, cachedFcmToken,
    Instant.parse(tokenCachedAt), Instant.parse(connectedAt),
)

fun DriveContact.toDto() = DriveContactDto(
    uid, displayName, email, avatarUrl, profileDriveUrl, cachedFcmToken,
    tokenCachedAt.toString(), connectedAt.toString(),
)
