package com.snehonir.niyeesho.features.shopping_list

import com.snehonir.niyeesho.features.shopping_list.data.local.ShoppingListLocalDataSourceImpl
import com.snehonir.niyeesho.features.shopping_list.data.repository.ShoppingListRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ShoppingListRepositoryTest {
    @Test
    fun localCacheIsVisibleImmediately() = runTest {
        val repository = ShoppingListRepositoryImpl(ShoppingListLocalDataSourceImpl())
        val created = repository.createShoppingList("Pantry", "owner").getOrThrow()
        assertEquals("Pantry", repository.getShoppingList(created.id).getOrThrow().title)
    }

    @Test
    fun archiveRemovesListFromActiveFlow() = runTest {
        val repository = ShoppingListRepositoryImpl(ShoppingListLocalDataSourceImpl())
        repository.archiveShoppingList("list_001")
        val active = repository.watchShoppingLists()
        assertEquals(0, active.first().getOrThrow().size)
    }
}
