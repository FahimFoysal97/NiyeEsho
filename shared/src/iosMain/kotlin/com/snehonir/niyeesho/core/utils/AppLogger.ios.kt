package com.snehonir.niyeesho.core.utils

actual fun logD(tag: String, message: String) {
    println("[$tag] $message")
}

actual fun logE(tag: String, message: String, throwable: Throwable?) {
    println("[$tag] ERROR: $message - ${throwable?.message}")
    throwable?.printStackTrace()
}
