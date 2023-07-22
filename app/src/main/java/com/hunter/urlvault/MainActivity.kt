package com.hunter.urlvault

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hunter.urlvault.components.ExitAlert
import com.hunter.urlvault.components.HomeScreen
import com.hunter.urlvault.fileSystem.Database
import com.hunter.urlvault.fileSystem.FileSystem
import com.hunter.urlvault.ui.theme.UrlVaultTheme
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Database(this).writableDatabase
        val fs = FileSystem(db)
        setContent {
            UrlVaultTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(50.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(fs)
                    ExitAlert()
                }
            }
        }
    }
}
