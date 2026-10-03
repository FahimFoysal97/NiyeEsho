package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ShoppingList
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository

class CreateShoppingListUseCase(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(title: String, ownerId: String): Result<ShoppingList> =
        if (title.isBlank() || ownerId.isBlank()) Result.failure(IllegalArgumentException("Title and owner are required."))
        else repository.createShoppingList(title.trim(), ownerId)
}
