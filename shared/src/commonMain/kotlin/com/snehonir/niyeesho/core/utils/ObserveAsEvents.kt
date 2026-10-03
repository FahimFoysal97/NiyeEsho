package com.snehonir.niyeesho.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key: Any? = Unit,
    onEvent: suspend (T) -> Unit,
) {
    val currentOnEvent = rememberUpdatedEventHandler(onEvent)
    LaunchedEffect(flow, key) {
        flow.collect { currentOnEvent(it) }
    }
}

@Composable
private fun <T> rememberUpdatedEventHandler(handler: suspend (T) -> Unit): suspend (T) -> Unit {
    val currentHandler = remember { HandlerReference(handler) }
    currentHandler.value = handler
    return { value -> currentHandler.value(value) }
}

private class HandlerReference<T>(var value: suspend (T) -> Unit)
