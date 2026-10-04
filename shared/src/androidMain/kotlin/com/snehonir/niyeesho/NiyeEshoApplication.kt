package com.snehonir.niyeesho

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.snehonir.niyeesho.app.di.initializeNiyeEsho
import com.snehonir.niyeesho.core.database.DatabaseDriverFactory
import com.snehonir.niyeesho.core.utils.TokenManagerImpl
import com.snehonir.niyeesho.core.utils.logD

class NiyeEshoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        logD("NiyeEshoApplication", "onCreate() started")
        initializeNiyeEsho(DatabaseDriverFactory(this))

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                if (!token.isNullOrBlank()) {
                    logD("NiyeEshoApplication", "Fetched initial FCM token: $token")
                    TokenManagerImpl().saveFcmToken(token)
                }
            } else {
                logD("NiyeEshoApplication", "Fetching FCM token failed: ${task.exception?.message}")
            }
        }

        logD("NiyeEshoApplication", "onCreate() completed successfully")
    }
}
