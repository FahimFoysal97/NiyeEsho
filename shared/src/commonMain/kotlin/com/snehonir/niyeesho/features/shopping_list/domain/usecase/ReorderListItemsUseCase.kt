package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository

class ReorderListItemsUseCase(private val repository: ListItemRepository) {
    suspend operator fun invoke(listId: String, orderedIds: List<String>): Result<Unit> =
        repository.reorderItems(listId, orderedIds)
}
