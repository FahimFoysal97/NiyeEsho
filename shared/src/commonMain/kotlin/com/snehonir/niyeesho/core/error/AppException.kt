package com.snehonir.niyeesho.core.error

sealed class AppException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    class Network(cause: Throwable? = null) : AppException("A network request failed.", cause)
    class Unauthorized(cause: Throwable? = null) : AppException("Authorization is required.", cause)
    class NotFound(cause: Throwable? = null) : AppException("The requested resource was not found.", cause)
    class Storage(cause: Throwable? = null) : AppException("Local storage is unavailable.", cause)
    class Validation(message: String) : AppException(message)
    class Unknown(cause: Throwable) : AppException("An unexpected error occurred.", cause)
}
