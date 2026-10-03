package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow

class WatchShoppingListsUseCase(private val repository: ShoppingListRepository) {
    operator fun invoke(): Flow<Result<List<ShoppingList>>> = repository.watchShoppingLists()
}
