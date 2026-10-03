package com.snehonir.niyeesho.features.auth.data.mapper

import com.snehonir.niyeesho.features.auth.domain.entity.AuthState
import com.snehonir.niyeesho.features.auth.domain.entity.User

fun User?.toAuthState(): AuthState =
    this?.let { AuthState.SignedIn(it) } ?: AuthState.SignedOut
