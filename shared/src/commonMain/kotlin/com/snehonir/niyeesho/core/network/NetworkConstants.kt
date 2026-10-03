package com.snehonir.niyeesho.core.network

import com.snehonir.niyeesho.BuildKonfig

object NetworkConstants {
    const val DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file"
    val driveBaseUrl: String = BuildKonfig.BASE_URL
    val fcmBaseUrl: String = BuildKonfig.FCM_BASE_URL
    val fcmProjectId: String = BuildKonfig.FCM_PROJECT_ID
}
