package com.snehonir.niyeesho.core.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toDisplayDate(): String {
    val local = toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.date.dayOfMonth.toString().padStart(2, '0')}/" +
        "${local.date.monthNumber.toString().padStart(2, '0')}/${local.date.year}"
}
