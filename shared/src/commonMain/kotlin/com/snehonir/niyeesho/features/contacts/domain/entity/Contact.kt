package com.snehonir.niyeesho.features.contacts.domain.entity

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}

@Serializable
data class Contact(
    val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val profileDriveUrl: String,
    val cachedFcmToken: String,
    @Serializable(with = InstantSerializer::class)
    val tokenCachedAt: Instant,
    @Serializable(with = InstantSerializer::class)
    val connectedAt: Instant,
    val isBlocked: Boolean = false,
    val status: ContactStatus = ContactStatus.ACTIVE,
    @Serializable(with = InstantSerializer::class)
    val requestedAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val expiresAt: Instant? = null,
) {
    companion object {
        fun preview() = Contact(
            uid = "user_rahim",
            displayName = "Rahim Ahmed",
            email = "rahim@example.com",
            avatarUrl = null,
            profileDriveUrl = "https://drive.google.com/uc?id=rahim_profile&export=download",
            cachedFcmToken = "rahim_fcm_token",
            tokenCachedAt = Instant.parse("2026-06-15T08:00:00Z"),
            connectedAt = Instant.parse("2026-01-10T00:00:00Z"),
            status = ContactStatus.ACTIVE,
        )
    }
}
