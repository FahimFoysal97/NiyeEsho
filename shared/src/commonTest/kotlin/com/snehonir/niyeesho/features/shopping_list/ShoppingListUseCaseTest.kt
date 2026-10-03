package com.snehonir.niyeesho.features.shopping_list

import com.snehonir.niyeesho.features.shopping_list.domain.usecase.CreateShoppingListUseCase
import com.snehonir.niyeesho.testing.FakeShoppingListRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShoppingListUseCaseTest {
    @Test
    fun blankTitleIsRejected() = runTest {
        val result = CreateShoppingListUseCase(FakeShoppingListRepository())("", "owner")
        assertTrue(result.isFailure)
    }

    @Test
    fun createsTrimmedTitle() = runTest {
        val result = CreateShoppingListUseCase(FakeShoppingListRepository())("  Groceries  ", "owner")
        assertEquals("Groceries", result.getOrThrow().title)
    }
}
