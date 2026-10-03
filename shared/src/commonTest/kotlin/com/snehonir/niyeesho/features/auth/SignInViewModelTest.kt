package com.snehonir.niyeesho.features.auth

import app.cash.turbine.test
import com.snehonir.niyeesho.features.auth.domain.usecase.SignInWithGoogleUseCase
import com.snehonir.niyeesho.features.auth.presentation.signin.SignInUiAction
import com.snehonir.niyeesho.features.auth.presentation.signin.SignInUiEvent
import com.snehonir.niyeesho.features.auth.presentation.signin.SignInViewModel
import com.snehonir.niyeesho.testing.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SignInViewModelTest {
    @Test
    fun successfulSignInEmitsNavigationEvent() = runTest {
        val viewModel = SignInViewModel(SignInWithGoogleUseCase(FakeAuthRepository()))
        viewModel.uiEvent.test {
            viewModel.onAction(SignInUiAction.GoogleSignInClicked("token"))
            assertTrue(awaitItem() is SignInUiEvent.NavigateToShoppingLists)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
