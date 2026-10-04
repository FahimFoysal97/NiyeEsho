package com.snehonir.niyeesho

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.snehonir.niyeesho.app.NiyeEshoApp
import com.snehonir.niyeesho.core.utils.logD

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        logD("MainActivity", "MainActivity.onCreate() started")

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
