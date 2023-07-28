package com.hunter.urlvault

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.hunter.urlvault.components.Search
import com.hunter.urlvault.fileSystem.Database
import com.hunter.urlvault.fileSystem.FileSystem
import com.hunter.urlvault.ui.theme.UrlVaultTheme

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Database(this).writableDatabase
        val fs = FileSystem(db)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW)
        window.statusBarColor = this.resources.getColor(R.color.black)

        val selectedDir =  intent.getStringExtra("selectedDir")!!
        setContent {
            UrlVaultTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val subNodes by remember {
                        mutableStateOf(fs.listSubNodes(selectedDir))
                    }
                    val delete = { path:String ->
                        val res = fs.deleteNode(path)
                        Log.d("GUI-delete", res)
                        Toast.makeText(this,res, Toast.LENGTH_SHORT).show()
                    }
                    val rename = { path:String,name:String ->
                        val res=fs.rename(path,name)
                        Log.d("GUI-rename", res)
                        Toast.makeText(this,res, Toast.LENGTH_SHORT).show()
                    }
                    Search(subNodes = subNodes, rename = rename, delete = delete)
                }
            }
        }
    }
}