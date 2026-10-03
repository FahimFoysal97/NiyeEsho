package com.snehonir.niyeesho

import androidx.compose.ui.window.ComposeUIViewController
import com.snehonir.niyeesho.app.NiyeEshoApp
import com.snehonir.niyeesho.app.di.initializeNiyeEsho
import com.snehonir.niyeesho.core.database.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController { NiyeEshoApp() }

fun initializeIosNiyeEsho() {
    initializeNiyeEsho(DatabaseDriverFactory())
}