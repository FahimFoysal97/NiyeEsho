package com.snehonir.niyeesho

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.snehonir.niyeesho.app.NiyeEshoApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            NiyeEshoApp(
                onShareInvite = { inviteLink ->
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
    }
}