package com.snehonir.niyeesho.features.dashboard.di

import com.snehonir.niyeesho.features.dashboard.presentation.DashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule = module {
    viewModelOf(::DashboardViewModel)
}
