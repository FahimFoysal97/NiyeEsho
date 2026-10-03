package com.snehonir.niyeesho.core.utils

expect fun formatUiText(key: String, args: List<String>): String

sealed interface UiText {
    data class Plain(val value: String) : UiText
    data class Resource(val key: String, val args: List<String> = emptyList()) : UiText

    fun resolve(): String = when (this) {
        is Plain -> value
        is Resource -> formatUiText(key, args)
    }
}
