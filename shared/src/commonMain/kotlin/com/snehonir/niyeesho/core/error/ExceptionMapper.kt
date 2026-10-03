package com.snehonir.niyeesho.core.error

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException

fun Throwable.toFailure(): Failure {
    if (this is CancellationException) throw this
    return when (this) {
        is ClientRequestException -> when (response.status.value) {
            401, 403 -> Failure.Unauthorized
            404 -> Failure.NotFound
            else -> Failure.Network
        }
        is ResponseException -> Failure.Network
        is AppException.Network -> Failure.Network
        is AppException.Unauthorized -> Failure.Unauthorized
        is AppException.NotFound -> Failure.NotFound
        is AppException.Storage -> Failure.Storage
        is AppException.Validation -> Failure.Validation
        else -> Failure.Unknown(this)
    }
}

fun Failure.toAppException(): AppException = when (this) {
    Failure.Network, Failure.Offline -> AppException.Network()
    Failure.Unauthorized -> AppException.Unauthorized()
    Failure.NotFound -> AppException.NotFound()
    Failure.Storage -> AppException.Storage()
    Failure.Validation -> AppException.Validation("The provided data is invalid.")
    is Failure.Unknown -> AppException.Unknown(cause)
}
