package com.snehonir.niyeesho

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.snehonir.niyeesho.app.NiyeEshoApp
import com.snehonir.niyeesho.core.notifier.SystemNotifier
import com.snehonir.niyeesho.core.utils.logD

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        SystemNotifier.init(this)
        logD("MainActivity", "MainActivity.onCreate() started")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101,
                )
            }
        }

        setContent {
            logD("MainActivity", "Setting content in setContent")
            NiyeEshoApp(
                onShareInvite = { inviteLink ->
                    logD("MainActivity", "onShareInvite called with inviteLink: $inviteLink")
                    startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, inviteLink)
                            },
                            "Share invite link",
                        ),
                    )
                },
            )
        }
        logD("MainActivity", "MainActivity.onCreate() completed")
    }
}
