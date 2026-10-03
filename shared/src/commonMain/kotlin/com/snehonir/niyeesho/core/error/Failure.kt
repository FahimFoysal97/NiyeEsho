package com.snehonir.niyeesho.core.error

sealed interface Failure {
    data object Network : Failure
    data object Unauthorized : Failure
    data object NotFound : Failure
    data object Offline : Failure
    data object Storage : Failure
    data object Validation : Failure
    data class Unknown(val cause: Throwable) : Failure
}
