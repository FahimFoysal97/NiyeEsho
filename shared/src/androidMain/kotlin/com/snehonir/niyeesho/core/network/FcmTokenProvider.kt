package com.snehonir.niyeesho.core.network

import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.snehonir.niyeesho.core.utils.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual object FcmTokenProvider {
    private var appContext: Context? = null

    fun init(context: Context) {
        logD("FcmTokenProvider", "init() called with context: ${context.packageName}")
        appContext = context.applicationContext
    }

    actual suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        val context = appContext ?: return@withContext null
        runCatching {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null && account.account != null) {
                val scope = "oauth2:https://www.googleapis.com/auth/firebase.messaging"
                val token = GoogleAuthUtil.getToken(context, account.account!!, scope)
                logD("FcmTokenProvider", "Successfully retrieved FCM OAuth2 Access Token (${token.take(15)}...)")
                token
            } else {
                logD("FcmTokenProvider", "No signed in Google Account found for FCM access token.")
                null
            }
        }.onFailure {
            logD("FcmTokenProvider", "Failed to retrieve FCM OAuth2 Access Token: ${it.message}")
        }.getOrNull()
    }
}
