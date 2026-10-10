package com.snehonir.niyeesho.core.utils

import kotlin.time.Duration.Companion.hours

object Constants {
    const val NIYE_ESHO_FOLDER = "NiyeEsho"
    const val PROFILE_FILE = "profile.json"
    const val METADATA_FILE = "metadata.json"
    const val CONTACTS_FILE = "contacts.json"
    const val PROFILE_CACHE_MAX_AGE_DAYS = 7
    const val MAX_FCM_PAYLOAD_BYTES = 3_500

    val INVITE_LINK_EXPIRY_MS: Long = 24.hours.inWholeMilliseconds
}
