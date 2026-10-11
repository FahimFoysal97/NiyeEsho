package com.snehonir.niyeesho.core.notifier

expect object SystemNotifier {
    fun postNotification(title: String, messageText: String)
}
