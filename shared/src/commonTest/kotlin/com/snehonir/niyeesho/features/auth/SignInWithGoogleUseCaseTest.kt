package com.snehonir.niyeesho.features.auth

import app.cash.turbine.test
import com.snehonir.niyeesho.features.auth.domain.usecase.SignInWithGoogleUseCase
import com.snehonir.niyeesho.testing.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignInWithGoogleUseCaseTest {
    @Test
    fun blankTokenIsRejected() = runTest {
        val result = SignInWithGoogleUseCase(FakeAuthRepository())("")
        assertTrue(result.isFailure)
    }

    @Test
    fun validTokenSignsUserIn() = runTest {
        val result = SignInWithGoogleUseCase(FakeAuthRepository())("google-token")
        assertEquals("user_demo", result.getOrThrow().uid)
    }

    @Test
    fun repositoryPublishesSignedInState() = runTest {
        val repository = FakeAuthRepository()
        repository.observeAuthState().test {
            assertTrue(awaitItem().isSuccess)
            repository.signInWithGoogle("token")
            assertTrue(awaitItem().getOrThrow() is com.snehonir.niyeesho.features.auth.domain.entity.AuthState.SignedIn)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
