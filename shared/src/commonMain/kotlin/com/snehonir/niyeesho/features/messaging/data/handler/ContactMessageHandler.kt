package com.snehonir.niyeesho.features.messaging.data.handler

import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository
import com.snehonir.niyeesho.features.messaging.domain.entity.ContactHandshakeAction
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import kotlinx.datetime.Clock

class ContactMessageHandler(
    private val contactsRepository: ContactsRepository,
) : MessageHandler {
    override suspend fun handle(message: FcmMessage): Result<Unit> = runCatching {
        logD("ContactMessageHandler", "handle() -> message type: ${message.type}, senderId: ${message.senderId}, senderName: ${message.senderName}")
        val now = Clock.System.now()
        val handshakeData = message.data as? FcmMessageData.ContactHandshake
        val action = handshakeData?.action ?: when (message.type) {
            FcmMessageType.CONTACT_REQUEST -> ContactHandshakeAction.REQUEST
            FcmMessageType.CONTACT_ACCEPTED -> ContactHandshakeAction.ACCEPT
            FcmMessageType.CONTACT_DECLINED -> ContactHandshakeAction.DECLINE
            FcmMessageType.CONTACT_REMOVED -> ContactHandshakeAction.REMOVE
            else -> ContactHandshakeAction.REQUEST
        }

        when (action) {
            ContactHandshakeAction.REQUEST -> {
                logD("ContactMessageHandler", "Handling REQUEST from ${message.senderName} (uid: ${message.senderId})")
                val pendingIncomingContact = Contact(
                    uid = message.senderId,
                    displayName = message.senderName.ifBlank { "Friend" },
                    email = handshakeData?.email ?: "${message.senderId}@gmail.com",
                    avatarUrl = handshakeData?.avatarUrl,
                    profileDriveUrl = "",
                    cachedFcmToken = handshakeData?.fcmToken ?: "",
                    tokenCachedAt = now,
                    connectedAt = now,
                    status = ContactStatus.PENDING_INCOMING,
                )
                contactsRepository.addContact(pendingIncomingContact).getOrThrow()
                logD("ContactMessageHandler", "Successfully saved PENDING_INCOMING contact for ${message.senderName}")
            }

            ContactHandshakeAction.ACCEPT -> {
                logD("ContactMessageHandler", "Handling ACCEPT from ${message.senderName} (uid: ${message.senderId})")
                contactsRepository.updateStatus(message.senderId, ContactStatus.ACTIVE).getOrThrow()
                logD("ContactMessageHandler", "Successfully updated contact ${message.senderName} to ACTIVE")
            }

            ContactHandshakeAction.DECLINE -> {
                logD("ContactMessageHandler", "Handling DECLINE from ${message.senderName} (uid: ${message.senderId})")
                contactsRepository.updateStatus(message.senderId, ContactStatus.DECLINED).getOrThrow()
            }

            ContactHandshakeAction.REMOVE -> {
                logD("ContactMessageHandler", "Handling REMOVE from ${message.senderName} (uid: ${message.senderId})")
                contactsRepository.removeContact(message.senderId).getOrThrow()
            }
        }
    }
}
