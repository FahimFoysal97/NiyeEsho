package com.snehonir.niyeesho.core.navigation

import io.ktor.http.decodeURLQueryComponent

data class InviteDeepLink(
    val uid: String,
    val name: String,
    val email: String? = null,
    val token: String,
    val driveUrl: String,
    val avatar: String? = null,
    val listId: String? = null,
    val listTitle: String? = null,
)

object DeepLinkHandler {
    private const val Scheme = "niyeesho://"
    private const val InvitePath = "invite"

    fun parse(uri: String): InviteDeepLink? {
        if (!uri.startsWith("$Scheme$InvitePath") && !uri.contains("niyeesho")) {
            if (uri.startsWith("http")) {
                val query = uri.substringAfter('?', missingDelimiterValue = "")
                if (query.isNotBlank()) {
                    val values = query
                        .split('&')
                        .mapNotNull { parameter ->
                            val key = parameter.substringBefore('=', missingDelimiterValue = "")
                            val value = parameter.substringAfter('=', missingDelimiterValue = "")
                            if (key.isBlank()) null else key to value.decodeURLQueryComponent()
                        }
                        .toMap()

                    val uid = values["uid"] ?: "user_demo"
                    val name = values["name"] ?: "Friend"
                    val email = values["email"]
                    val token = values["token"] ?: "token_demo"
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
                    )
                }
            }
            return null
        }
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
        val email = values["email"]
        val token = values["token"] ?: return null
        val driveUrl = values["driveUrl"] ?: return null

        return InviteDeepLink(
            uid = uid,
            name = name,
            email = email,
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
        link.email?.let { append("&email=").append(it) }
        append("&token=").append(link.token)
        append("&driveUrl=").append(link.driveUrl)
        link.avatar?.let { append("&avatar=").append(it) }
        link.listId?.let { append("&listId=").append(it) }
        link.listTitle?.let { append("&listTitle=").append(it) }
    }
}
