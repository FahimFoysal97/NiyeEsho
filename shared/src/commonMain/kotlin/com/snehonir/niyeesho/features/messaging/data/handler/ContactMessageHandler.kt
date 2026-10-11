package com.snehonir.niyeesho.features.messaging.data.handler

import com.russhwolf.settings.Settings
import com.snehonir.niyeesho.core.notifier.SystemNotifier
import com.snehonir.niyeesho.core.utils.logD
import com.snehonir.niyeesho.features.contacts.domain.entity.Contact
import com.snehonir.niyeesho.features.contacts.domain.entity.ContactStatus
import com.snehonir.niyeesho.features.contacts.domain.repository.ContactsRepository
import com.snehonir.niyeesho.features.messaging.domain.entity.ContactHandshakeAction
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessage
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageData
import com.snehonir.niyeesho.features.messaging.domain.entity.FcmMessageType
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val json = Json { ignoreUnknownKeys = true }

class ContactMessageHandler(
    private val contactsRepository: ContactsRepository,
    private val settings: Settings = Settings(),
) : MessageHandler {

    private fun isAlreadyNotified(uid: String): Boolean {
        val notified = settings.getString(KEY_NOTIFIED_UIDS, "")
            .split(',')
            .filter { it.isNotBlank() }
            .toSet()
        return notified.contains(uid)
    }

    private fun markNotified(uid: String) {
        val notified = settings.getString(KEY_NOTIFIED_UIDS, "")
            .split(',')
            .filter { it.isNotBlank() }
            .toMutableSet()
        if (notified.add(uid)) {
            settings.putString(KEY_NOTIFIED_UIDS, notified.joinToString(","))
        }
    }

    private fun clearNotified(uid: String) {
        val notified = settings.getString(KEY_NOTIFIED_UIDS, "")
            .split(',')
            .filter { it.isNotBlank() }
            .toMutableSet()
        if (notified.remove(uid)) {
            settings.putString(KEY_NOTIFIED_UIDS, notified.joinToString(","))
        }
    }

    override suspend fun handle(message: FcmMessage): Result<Unit> = runCatching {
        logD("SystemNotifier", "ContactMessageHandler.handle() -> type: ${message.type}, senderId: ${message.senderId}, senderName: ${message.senderName}")
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
                logD("SystemNotifier", "Handling REQUEST from ${message.senderName} (uid: ${message.senderId})")

                var email = handshakeData?.email
                var fcmToken = handshakeData?.fcmToken
                var avatarUrl = handshakeData?.avatarUrl

                val dataObj = message.data
                if ((email.isNullOrBlank() || fcmToken.isNullOrBlank()) && dataObj is FcmMessageData.Generic) {
                    val genericText = dataObj.text
                    if (genericText.startsWith("{")) {
                        runCatching {
                            val obj = json.parseToJsonElement(genericText).jsonObject
                            if (email.isNullOrBlank()) email = obj["email"]?.jsonPrimitive?.contentOrNull
                            if (fcmToken.isNullOrBlank()) fcmToken = obj["fcmToken"]?.jsonPrimitive?.contentOrNull
                            if (avatarUrl.isNullOrBlank()) avatarUrl = obj["avatarUrl"]?.jsonPrimitive?.contentOrNull
                        }
                    }
                }

                val pendingIncomingContact = Contact(
                    uid = message.senderId,
                    displayName = message.senderName.ifBlank { "Friend" },
                    email = email ?: "${message.senderId}@gmail.com",
                    avatarUrl = avatarUrl,
                    profileDriveUrl = "",
                    cachedFcmToken = fcmToken ?: "",
                    tokenCachedAt = now,
                    connectedAt = now,
                    status = ContactStatus.PENDING_INCOMING,
                )
                contactsRepository.addContact(pendingIncomingContact).getOrThrow()

                if (!isAlreadyNotified(message.senderId)) {
                    markNotified(message.senderId)
                    logD("SystemNotifier", "Triggering SystemNotifier for ${pendingIncomingContact.displayName}")
                    SystemNotifier.postNotification(
                        title = "New Connection Request 🤝",
                        messageText = "${pendingIncomingContact.displayName} wants to connect with you on Niye Esho.",
                    )
                } else {
                    logD("SystemNotifier", "Notification already sent and persisted on disk for ${message.senderId}; skipping duplicate notification.")
                }
                logD("SystemNotifier", "Successfully saved PENDING_INCOMING contact for ${message.senderName}")
            }

            ContactHandshakeAction.ACCEPT -> {
                logD("SystemNotifier", "Handling ACCEPT from ${message.senderName} (uid: ${message.senderId})")
                clearNotified(message.senderId)
                val existing = contactsRepository.getContact(message.senderId)
                contactsRepository.updateStatus(message.senderId, ContactStatus.ACTIVE).getOrThrow()

                if (existing?.status != ContactStatus.ACTIVE) {
                    SystemNotifier.postNotification(
                        title = "Connection Accepted 🎉",
                        messageText = "${message.senderName} accepted your connection request!",
                    )
                }
                logD("SystemNotifier", "Successfully updated contact ${message.senderName} to ACTIVE")
            }

            ContactHandshakeAction.DECLINE -> {
                logD("SystemNotifier", "Handling DECLINE from ${message.senderName} (uid: ${message.senderId})")
                clearNotified(message.senderId)
                contactsRepository.updateStatus(message.senderId, ContactStatus.DECLINED).getOrThrow()
            }

            ContactHandshakeAction.REMOVE -> {
                logD("SystemNotifier", "Handling REMOVE from ${message.senderName} (uid: ${message.senderId})")
                clearNotified(message.senderId)
                contactsRepository.removeContact(message.senderId).getOrThrow()
            }
        }
    }

    companion object {
        private const val KEY_NOTIFIED_UIDS = "notified_connection_request_uids"
    }
}
