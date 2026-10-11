package com.snehonir.niyeesho.core.navigation

import com.snehonir.niyeesho.core.utils.Constants
import com.snehonir.niyeesho.core.utils.logD
import io.ktor.http.decodeURLQueryComponent
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class InviteDeepLink(
    val uid: String,
    val name: String,
    val email: String? = null,
    val token: String,
    val driveUrl: String,
    val avatar: String? = null,
    val listId: String? = null,
    val listTitle: String? = null,
    val expiresAt: Long? = null,
)

object DeepLinkHandler {
    fun parse(uri: String): InviteDeepLink? {
        val query = when {
            uri.contains("?") -> uri.substringAfter("?")
            uri.contains("#") -> uri.substringAfter("#")
            else -> ""
        }

        if (query.isBlank()) return null

        val values = query
            .split('&')
            .mapNotNull { parameter ->
                val key = parameter.substringBefore('=', missingDelimiterValue = "")
                val value = parameter.substringAfter('=', missingDelimiterValue = "")
                if (key.isBlank()) null else key to value.decodeURLQueryComponent()
            }
            .toMap()

        val now = Clock.System.now().toEpochMilliseconds()
        val expiresAt = values[DeepLinkConfig.Params.EXPIRES_AT]?.toLongOrNull()
        val ts = values[DeepLinkConfig.Params.TS]?.toLongOrNull()

        // Enforce expiration window check
        if (expiresAt != null && now > expiresAt) {
            logD("DeepLinkHandler", "parse() -> Invite link expired! Current time $now > expiresAt $expiresAt")
            return null
        }
        if (ts != null && (now - ts) > Constants.INVITE_LINK_EXPIRY_MS) {
            logD("DeepLinkHandler", "parse() -> Invite link expired! Timestamp $ts is older than ${Constants.INVITE_LINK_EXPIRY_MS}ms")
            return null
        }

        val uid = values[DeepLinkConfig.Params.UID] ?: return null
        val name = values[DeepLinkConfig.Params.NAME] ?: return null
        val email = values[DeepLinkConfig.Params.EMAIL]
        val token = values[DeepLinkConfig.Params.TOKEN] ?: return null
        val driveUrl = values[DeepLinkConfig.Params.DRIVE_URL] ?: ""

        return InviteDeepLink(
            uid = uid,
            name = name,
            email = email,
            token = token,
            driveUrl = driveUrl,
            avatar = values[DeepLinkConfig.Params.AVATAR],
            listId = values[DeepLinkConfig.Params.LIST_ID],
            listTitle = values[DeepLinkConfig.Params.LIST_TITLE],
            expiresAt = expiresAt ?: (ts?.let { it + 60_000L }),
        )
    }

    fun toRoute(link: InviteDeepLink): String = buildString {
        append(NavRoute.InviteAccept.path)
        append("?${DeepLinkConfig.Params.UID}=").append(link.uid)
        append("&${DeepLinkConfig.Params.NAME}=").append(link.name)
        link.email?.let { append("&${DeepLinkConfig.Params.EMAIL}=").append(it) }
        append("&${DeepLinkConfig.Params.TOKEN}=").append(link.token)
        append("&${DeepLinkConfig.Params.DRIVE_URL}=").append(link.driveUrl)
        link.avatar?.let { append("&${DeepLinkConfig.Params.AVATAR}=").append(it) }
        link.listId?.let { append("&${DeepLinkConfig.Params.LIST_ID}=").append(it) }
        link.listTitle?.let { append("&${DeepLinkConfig.Params.LIST_TITLE}=").append(it) }
    }
}
