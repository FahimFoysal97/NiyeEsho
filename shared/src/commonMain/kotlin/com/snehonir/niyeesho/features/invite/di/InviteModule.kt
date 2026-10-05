package com.snehonir.niyeesho.features.invite.di

import com.snehonir.niyeesho.features.invite.domain.usecase.AddContactFromInviteUseCase
import com.snehonir.niyeesho.features.invite.presentation.InviteViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val inviteModule = module {
    singleOf(::AddContactFromInviteUseCase)
    viewModelOf(::InviteViewModel)
}
