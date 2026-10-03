package com.snehonir.niyeesho.features.shopping_list

import app.cash.turbine.test
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.ArchiveShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.CreateShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.DeleteShoppingListUseCase
import com.snehonir.niyeesho.features.shopping_list.domain.usecase.WatchShoppingListsUseCase
import com.snehonir.niyeesho.features.shopping_list.presentation.list.ShoppingListUiAction
import com.snehonir.niyeesho.features.shopping_list.presentation.list.ShoppingListViewModel
import com.snehonir.niyeesho.testing.FakeShoppingListRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse

class ShoppingListViewModelTest {
    @Test
    fun initialStateLoadsCachedLists() = runTest {
        val repository = FakeShoppingListRepository()
        val viewModel = ShoppingListViewModel(
            WatchShoppingListsUseCase(repository),
            CreateShoppingListUseCase(repository),
            DeleteShoppingListUseCase(repository),
            ArchiveShoppingListUseCase(repository),
        )
        viewModel.uiState.test {
            awaitItem()
            val loaded = awaitItem()
            assertFalse(loaded.lists.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun createActionShowsThenClosesDialog() = runTest {
        val repository = FakeShoppingListRepository()
        val viewModel = ShoppingListViewModel(
            WatchShoppingListsUseCase(repository),
            CreateShoppingListUseCase(repository),
            DeleteShoppingListUseCase(repository),
            ArchiveShoppingListUseCase(repository),
        )
        viewModel.onAction(ShoppingListUiAction.CreateClicked)
        assertTrue(viewModel.uiState.value.isCreateDialogVisible)
        viewModel.onAction(ShoppingListUiAction.CreateConfirmed("Pantry", "owner"))
    }
}
