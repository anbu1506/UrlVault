package com.hunter.urlvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hunter.urlvault.fileSystem.FileSystem
import com.hunter.urlvault.ui.theme.UrlVaultTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val db = Database(this).writableDatabase
//        val fs = FileSystem(db)
//        val tests = Tests()
//        tests.execTests(fs)
//        val childList = fs.listDir("root")
        setContent {
            UrlVaultTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DirView(dir = FileSystem.Dir(
                        1,
                        "root",
                        1,
                        1,
                        mapOf(1 to "/Dir")
                    )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirView(dir:FileSystem.Dir) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.LightGray)) {
            Icon(Icons.Filled.Email, contentDescription = "Folder Icon", Modifier.size(50.dp).align(Alignment.CenterVertically))
            Text(text = dir.FsName,Modifier.align(Alignment.CenterVertically).padding(10.dp))
        }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UrlVaultTheme {
       DirView(dir = FileSystem.Dir(
           1,
           "root",
           1,
           1,
           mapOf(1 to "/Dir")
       ))
    }
}
