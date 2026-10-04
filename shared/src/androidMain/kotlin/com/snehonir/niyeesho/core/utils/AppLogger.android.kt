package com.snehonir.niyeesho.core.utils

import android.util.Log

actual fun logD(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun logE(tag: String, message: String, throwable: Throwable?) {
    Log.e(tag, message, throwable)
}
