package com.hunter.urlvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hunter.urlvault.fileSystem.Database
import com.hunter.urlvault.fileSystem.FileSystem
import com.hunter.urlvault.ui.theme.UrlVaultTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Database(this).writableDatabase
        val fs = FileSystem(db)
//        val tests = Tests()
//        tests.execTests(fs)
        val childList = fs.listDir("root")
        setContent {
            UrlVaultTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DirView(childList)
                }
            }
        }
    }
}

@Composable
fun DirView(map:Map<Int,String>?){
        LazyColumn{
            map?.forEach { (key, value) ->
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),

                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)) {
                            Icon(painter = painterResource(id = R.drawable.dir_icon), contentDescription = null)
                            Text(
                                text = value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .padding(15.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        }
                    }
                }
            }
        }
