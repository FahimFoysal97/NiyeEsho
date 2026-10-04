package com.snehonir.niyeesho.core.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toDisplayDate(): String {
    val local = toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.date.dayOfMonth.toString().padStart(2, '0')}/" +
        "${local.date.monthNumber.toString().padStart(2, '0')}/${local.date.year}"
}

fun Instant.toDisplayTime(): String {
    val local = toLocalDateTime(TimeZone.currentSystemDefault())
    val hour24 = local.hour
    val minute = local.minute.toString().padStart(2, '0')
    val amPm = if (hour24 >= 12) "PM" else "AM"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    return "$hour12:$minute $amPm"
}

fun Instant.toRelativeTimeString(): String {
    val now = Clock.System.now()
    val diffSec = (now - this).inWholeSeconds
    return when {
        diffSec < 60 -> "Just now"
        diffSec < 3600 -> "${diffSec / 60}m ago"
        diffSec < 86400 -> "${diffSec / 3600}h ago"
        diffSec < 604800 -> "${diffSec / 86400}d ago"
        else -> toDisplayDate()
    }
}
