package com.hunter.urlvault.components

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hunter.urlvault.R
import com.hunter.urlvault.fileSystem.FileSystem
import kotlin.system.exitProcess


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(fs: FileSystem){
    val context = LocalContext.current
    var selectedDir by remember {
        mutableStateOf("root")
    }
    var list by remember {
        mutableStateOf(fs.listNode(selectedDir))
    }
    var setCreateDirVisible by remember {
        mutableStateOf(false)
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color.Black)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text=selectedDir, color = Color.White, modifier = Modifier.padding(10.dp),style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Thin
                    )
                    )
                },
                modifier = Modifier.height(55.dp),
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if(selectedDir != "root"){
                                selectedDir = fs.parentPath(selectedDir)
                                list = fs.listNode(selectedDir)
                            }
                        }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                setCreateDirVisible=true
            }) {
                Icon(Icons.Default.Add,"add")
                CreateDir(setCreateDirVisible,cancel = {setCreateDirVisible=false},
                    save ={name: String ->
                        val res=fs.createDir(selectedDir,name)
                        Log.d("GUI-create", res)
                        Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                        list = fs.listNode(selectedDir)
                    } )
            }
        }
    ) {paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column {

                LazyColumn {
                    items(list) { item ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .clickable {
                                    selectedDir = item.path
                                    list = fs.listNode(selectedDir)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),

                            ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.dir_icon),
                                    contentDescription = null
                                )
                                Text(
                                    text = item.fsName,
                                    modifier = Modifier
                                        .height(48.dp)
                                        .padding(10.dp),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Thin
                                ))
                                Spacer(modifier = Modifier.weight(1f))
                                var mDisplayMenu by remember { mutableStateOf(false) }
                                IconButton(
                                    onClick = { mDisplayMenu = !mDisplayMenu },
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(Icons.Default.MoreVert, "")
                                }
                                DropdownMenu(
                                    expanded = mDisplayMenu,
                                    onDismissRequest = { mDisplayMenu = false },
                                    offset = DpOffset((-40).dp, 0.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("delete") },
                                        onClick = {
                                            val res = fs.deleteNode(item.path)
                                            Log.d("GUI-delete", res)
                                            Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                                            list = fs.listNode(selectedDir)
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = null
                                            )
                                        })
                                    var isVisible by remember { mutableStateOf(false) }
                                    DropdownMenuItem(
                                        text = { Text("rename") },
                                        onClick = {
                                            isVisible = true
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Edit,
                                                contentDescription = null
                                            )
                                            RenameDialogue(
                                                isVisible = isVisible,
                                                cancel = { isVisible=false},
                                                save ={name: String ->
                                                    val res=fs.rename(item.path,name)
                                                    Log.d("GUI-rename", res)
                                                    Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                                                    list = fs.listNode(selectedDir)
                                                    isVisible=false
                                                }
                                            )
                                        })
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}




@Composable
fun ExitAlert() {
    val showAlert = remember { mutableStateOf(false) }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Register a back callback to show the alert
    val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showAlert.value = true
        }
    }

    DisposableEffect(Unit) {
        backDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
    if (showAlert.value) {
        AlertDialog(
            onDismissRequest = {
                showAlert.value = false
            },
            title = { Text(text = "Alert!") },
            text = { Text(text = "are you sure to exit?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAlert.value = false
                        // Exit the app
                        exitApp()
                    }
                ) {
                    Text(text = "yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAlert.value = false
                    }
                ) {
                    Text(text = "no")
                }
            }
        )
    }
}

fun exitApp() {
    // Exit the app after a small delay to give time for the dialog to dismiss
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({ exitProcess(0) }, 200)
}
@Composable
fun CreateDir(isVisible:Boolean,cancel:()->Unit,save:(name:String)->Unit){
    var editedName by remember { mutableStateOf("New Dir") }
    if (isVisible){
        AlertDialog(
            shape = RectangleShape,
            onDismissRequest = {
                cancel()
            },
            title = { Text(text = "New Folder",modifier = Modifier.padding(0.dp),style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )) },
            text = { Column {
                Text(text = "Name:",modifier = Modifier.padding(5.dp),style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Thin
                ))
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth()
                )
            } },
            confirmButton = {
                TextButton(
                    onClick = {
                        if(editedName!=""){
                        save(editedName)
                        cancel()}
                    }
                ) {
                    Text(text = "Ok")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        cancel()
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun RenameDialogue(isVisible:Boolean,cancel:()->Unit,save:(name:String)->Unit) {
    var editedName by remember { mutableStateOf("New Dir") }
    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                cancel()
            },
            title = { Text(text = "Rename") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if(editedName!=""){
                            save(editedName)
                            cancel()}
                    }
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        cancel()
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}