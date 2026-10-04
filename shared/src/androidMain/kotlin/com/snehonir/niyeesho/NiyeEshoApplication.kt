package com.snehonir.niyeesho

import android.app.Application
import com.snehonir.niyeesho.app.di.initializeNiyeEsho
import com.snehonir.niyeesho.core.database.DatabaseDriverFactory
import com.snehonir.niyeesho.core.utils.logD

class NiyeEshoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        logD("NiyeEshoApp", "NiyeEshoApplication.onCreate() started")
        initializeNiyeEsho(DatabaseDriverFactory(this))
        logD("NiyeEshoApp", "NiyeEshoApplication.onCreate() completed successfully")
    }
}
