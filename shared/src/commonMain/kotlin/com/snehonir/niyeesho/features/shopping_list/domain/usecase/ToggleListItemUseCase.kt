package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.repository.ListItemRepository

class ToggleListItemUseCase(private val repository: ListItemRepository) {
    suspend operator fun invoke(itemId: String, checked: Boolean, checkedBy: String?): Result<Unit> =
        repository.toggleItem(itemId, checked, checkedBy)
}
