package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository

class DeleteListItemUseCase(private val repository: ListItemRepository) {
    suspend operator fun invoke(itemId: String): Result<Unit> = repository.deleteItem(itemId)
}
