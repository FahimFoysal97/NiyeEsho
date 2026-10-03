package com.snehonir.niyeesho.features.contacts.data.mapper

import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.drive.domain.entity.DriveContact

fun DriveContact.toContact() = Contact(
    uid, displayName, email, avatarUrl, profileDriveUrl, cachedFcmToken,
    tokenCachedAt, connectedAt,
)

fun Contact.toDriveContact() = DriveContact(
    uid, displayName, email, avatarUrl, profileDriveUrl, cachedFcmToken,
    tokenCachedAt, connectedAt,
)
