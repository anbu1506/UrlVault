package com.hunter.urlvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
        fs.createFile("root","file-system-repo")
        fs.writeFile("root/file-system-repo","https://www.github.com/anbu1506/FileSystem")
        setContent {
            UrlVaultTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(Color.Black)
                }
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
