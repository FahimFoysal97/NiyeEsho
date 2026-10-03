package com.snehonir.niyeesho

import android.app.Application
import com.snehonir.niyeesho.app.di.initializeNiyeEsho
import com.snehonir.niyeesho.core.database.DatabaseDriverFactory

class NiyeEshoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeNiyeEsho(DatabaseDriverFactory(this))
    }
}
