package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository

class ArchiveShoppingListUseCase(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.archiveShoppingList(id)
}
