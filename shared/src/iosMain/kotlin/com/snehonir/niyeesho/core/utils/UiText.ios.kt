package com.snehonir.niyeesho.core.utils

actual fun formatUiText(key: String, args: List<String>): String =
    if (args.isEmpty()) key else "$key: ${args.joinToString()}"
