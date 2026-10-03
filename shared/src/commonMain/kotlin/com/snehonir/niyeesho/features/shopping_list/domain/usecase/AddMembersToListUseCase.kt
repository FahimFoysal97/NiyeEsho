package com.snehonir.niyeesho.features.shopping_list.domain.usecase

import com.snehonir.niyeesho.features.shopping_list.domain.entity.ListMember
import com.snehonir.niyeesho.features.shopping_list.domain.repository.ShoppingListRepository

class AddMembersToListUseCase(private val repository: ShoppingListRepository) {
    suspend operator fun invoke(listId: String, members: List<ListMember>): Result<Unit> =
        if (listId.isBlank() || members.isEmpty()) Result.success(Unit)
        else repository.addMembers(listId, members)
}
