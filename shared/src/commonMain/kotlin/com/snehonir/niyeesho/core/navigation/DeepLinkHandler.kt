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
    private const val Scheme = "niyeesho://"
    private const val InvitePath = "invite"

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
        val expiresAt = values["expiresAt"]?.toLongOrNull()
        val ts = values["ts"]?.toLongOrNull()

        // Enforce expiration window check
        if (expiresAt != null && now > expiresAt) {
            logD("DeepLinkHandler", "parse() -> Invite link expired! Current time $now > expiresAt $expiresAt")
            return null
        }
        if (ts != null && (now - ts) > Constants.INVITE_LINK_EXPIRY_MS) {
            logD("DeepLinkHandler", "parse() -> Invite link expired! Timestamp $ts is older than ${Constants.INVITE_LINK_EXPIRY_MS}ms")
            return null
        }

        val uid = values["uid"] ?: return null
        val name = values["name"] ?: return null
        val email = values["email"]
        val token = values["token"] ?: return null
        val driveUrl = values["driveUrl"] ?: ""

        return InviteDeepLink(
            uid = uid,
            name = name,
            email = email,
            token = token,
            driveUrl = driveUrl,
            avatar = values["avatar"],
            listId = values["listId"],
            listTitle = values["listTitle"],
            expiresAt = expiresAt ?: (ts?.let { it + 60_000L }),
        )
    }

    fun toRoute(link: InviteDeepLink): String = buildString {
        append(NavRoute.InviteAccept.path)
        append("?uid=").append(link.uid)
        append("&name=").append(link.name)
        link.email?.let { append("&email=").append(it) }
        append("&token=").append(link.token)
        append("&driveUrl=").append(link.driveUrl)
        link.avatar?.let { append("&avatar=").append(it) }
        link.listId?.let { append("&listId=").append(it) }
        link.listTitle?.let { append("&listTitle=").append(it) }
    }
}
