package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository

class GetShoppingListUseCase(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(id: String): Result<ShoppingList> = repository.getShoppingList(id)
}
