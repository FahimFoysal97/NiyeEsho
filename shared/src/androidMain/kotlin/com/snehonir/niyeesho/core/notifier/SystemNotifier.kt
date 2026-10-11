package com.snehonir.niyeesho.core.notifier

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.snehonir.niyeesho.core.utils.logD

actual object SystemNotifier {
    private var appContext: Context? = null

    fun init(context: Context) {
        logD("SystemNotifier", "init() called with context: ${context.packageName}")
        appContext = context.applicationContext
    }

    actual fun postNotification(title: String, messageText: String) {
        logD("SystemNotifier", "postNotification() called with title: '$title', message: '$messageText', appContext present: ${appContext != null}")
        val context = appContext ?: run {
            logD("SystemNotifier", "postNotification() -> ERROR: appContext is null! Cannot post notification.")
            return
        }
        runCatching {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "niye_esho_handshake_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Niye Esho Connection Requests",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Notifications for incoming contact requests and connection approvals"
                    enableVibration(true)
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: Intent()
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            notificationManager.notify(notificationId, notification)
            logD("SystemNotifier", "Posted system notification: '$title' - '$messageText' (id: $notificationId)")
        }.onFailure {
            logD("SystemNotifier", "Failed to post system notification: ${it.message}")
        }
    }
}
