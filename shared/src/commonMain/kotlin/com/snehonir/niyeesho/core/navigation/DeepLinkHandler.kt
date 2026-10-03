package com.snehonir.niyeesho.core.navigation

import io.ktor.http.decodeURLQueryComponent

data class InviteDeepLink(
    val uid: String,
    val name: String,
    val token: String,
    val driveUrl: String,
    val avatar: String?,
    val listId: String?,
    val listTitle: String?,
)

object DeepLinkHandler {
    private const val Scheme = "niyeesho://"
    private const val InvitePath = "invite"

    fun parse(uri: String): InviteDeepLink? {
        if (!uri.startsWith("$Scheme$InvitePath")) return null
        val query = uri.substringAfter('?', missingDelimiterValue = "")
        if (query.isBlank()) return null

        val values = query
            .split('&')
            .mapNotNull { parameter ->
                val key = parameter.substringBefore('=', missingDelimiterValue = "")
                val value = parameter.substringAfter('=', missingDelimiterValue = "")
                if (key.isBlank()) null else key to value.decodeURLQueryComponent()
            }
            .toMap()

        val uid = values["uid"] ?: return null
        val name = values["name"] ?: return null
        val token = values["token"] ?: return null
        val driveUrl = values["driveUrl"] ?: return null

        return InviteDeepLink(
            uid = uid,
            name = name,
            token = token,
            driveUrl = driveUrl,
            avatar = values["avatar"],
            listId = values["listId"],
            listTitle = values["listTitle"],
        )
    }

    fun toRoute(link: InviteDeepLink): String = buildString {
        append(NavRoute.InviteAccept.path)
        append("?uid=").append(link.uid)
        append("&name=").append(link.name)
        append("&token=").append(link.token)
        append("&driveUrl=").append(link.driveUrl)
        link.avatar?.let { append("&avatar=").append(it) }
        link.listId?.let { append("&listId=").append(it) }
        link.listTitle?.let { append("&listTitle=").append(it) }
    }
}
