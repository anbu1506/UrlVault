package com.hunter.urlvault

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hunter.urlvault.components.ExitAlert
import com.hunter.urlvault.components.HomeScreen
import com.hunter.urlvault.fileSystem.Database
import com.hunter.urlvault.fileSystem.FileSystem
import com.hunter.urlvault.ui.theme.UrlVaultTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Database(this).writableDatabase
        val fs = FileSystem(db)
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW)
        window.statusBarColor = this.resources.getColor(R.color.black)

        var sharedUrl: String? = null
        if (Intent.ACTION_SEND == intent.action) {
            val uri =  intent.getStringExtra(Intent.EXTRA_TEXT)
            if (uri != null) {
                 sharedUrl = uri.toString()
            }
        }
        setContent {
            UrlVaultTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(50.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(fs,sharedUrl)
                    ExitAlert()
                }
            }
        }
    }
}

