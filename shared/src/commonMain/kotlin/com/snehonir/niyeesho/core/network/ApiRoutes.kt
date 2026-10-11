package com.snehonir.niyeesho.core.network

object ApiRoutes {
    fun files(baseUrl: String): String = "${baseUrl.removeSuffix("/")}/files"
    fun file(baseUrl: String, fileId: String): String = "${files(baseUrl)}/$fileId"

    object Params {
        const val ALT = "alt"
        const val ALT_MEDIA = "media"
    }

    object Headers {
        const val AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val METADATA = "X-NiyeEsho-Metadata"
    }
}
