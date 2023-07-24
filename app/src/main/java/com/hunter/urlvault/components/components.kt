package com.hunter.urlvault.components

import android.content.Intent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.fresh.materiallinkpreview.models.OpenGraphMetaData
import com.fresh.materiallinkpreview.parsing.OpenGraphMetaDataProvider
import com.fresh.materiallinkpreview.ui.CardLinkPreview
import com.fresh.materiallinkpreview.ui.CardLinkPreviewProperties
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hunter.urlvault.R
import com.hunter.urlvault.fileSystem.FileSystem
import java.net.URL
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
    var setCreateFileVisible by remember {
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
            Column{
                FloatingActionButton(onClick = {
                    setCreateDirVisible=true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_folder),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp,25.dp)
                    )
                    CreateDir(setCreateDirVisible,cancel = {setCreateDirVisible=false},
                        save ={name: String ->
                            val res=fs.createDir(selectedDir,name)
                            Log.d("GUI-create", res)
                            Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                            list = fs.listNode(selectedDir)
                        } )
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = {
                        setCreateFileVisible=true
                    },
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.url),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp,25.dp)
                        )
                        CreateFile(isVisible = setCreateFileVisible,
                            cancel = { setCreateFileVisible=false},
                            save ={name: String,url:String->
                                val res=fs.createFile(selectedDir,name)
                                fs.writeFile("$selectedDir/$name",url)
                                Log.d("GUI-create", res)
                                Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                                list = fs.listNode(selectedDir)
                                setCreateFileVisible=false
                            } )
                    }
                )
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
                        if (item.fsType == 0){
                            Dir(
                                item = item as FileSystem.Dir,
                                nodeClick = {path:String->
                                    selectedDir = path
                                    list = fs.listNode(selectedDir)
                                },
                                delete = { path:String ->
                                    val res = fs.deleteNode(path)
                                    Log.d("GUI-delete", res)
                                    Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                                    list = fs.listNode(selectedDir)
                                }, rename ={ path:String,name:String ->
                                    val res=fs.rename(path,name)
                                    Log.d("GUI-rename", res)
                                    Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                                    list = fs.listNode(selectedDir)
                                }
                            )
                        }
                        else{
                            File(
                                item = item as FileSystem.File,
                                delete ={path: String ->
                                    val res = fs.deleteNode(path)
                                    Log.d("GUI-delete", res)
                                    Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                                    list = fs.listNode(selectedDir)
                                } ,
                                rename ={path: String, name: String ->
                                    val res=fs.rename(path,name)
                                    Log.d("GUI-rename", res)
                                    Toast.makeText(context,res,Toast.LENGTH_SHORT).show()
                                    list = fs.listNode(selectedDir)
                                }
                            )
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
fun Dir(item:FileSystem.Dir,nodeClick:(path:String)->Unit,delete:(path:String)->Unit,rename:(path:String,name:String)->Unit){
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                nodeClick(item.path)
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
                painter = painterResource(id = R.drawable.folder),
                contentDescription = null
            )
            Text(
                text = item.fsName,
                modifier = Modifier
                    .height(48.dp)
                    .padding(14.dp),
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
                        delete(item.path)
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
                                rename(item.path,name)
                                isVisible=false
                            }
                        )
                    })
            }
        }

    }
}
@Composable
fun File(item:FileSystem.File,delete:(path:String)->Unit,rename:(path:String,name:String)->Unit){
        Column(
            modifier = Modifier.padding(25.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = item.fsName,
                    modifier = Modifier
                        .height(48.dp)
                        .padding(14.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ))
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, item.data)
                }

                Row {
                    val context =LocalContext.current
                    IconButton(
                        onClick = {
                            // Start the activity for the share intent
                            val intentChooser = Intent.createChooser(shareIntent, "Share URL")
                            context.startActivity(intentChooser)
                        }
                    ) {
                        Icon(Icons.Default.Share,"share")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                var mDisplayMenu by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { mDisplayMenu = !mDisplayMenu },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.MoreVert, "more")
                }
                DropdownMenu(
                    expanded = mDisplayMenu,
                    onDismissRequest = { mDisplayMenu = false },
                    offset = DpOffset((-40).dp, 0.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("delete") },
                        onClick = {
                            delete(item.path)
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
                        onClick = { isVisible = true },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = null
                            )
                            RenameDialogue(
                                isVisible = isVisible,
                                cancel = { isVisible=false},
                                save ={name: String ->
                                    rename(item.path,name)
                                    isVisible=false
                                }
                            )
                        })
                }
            }
            var metaData:OpenGraphMetaData? by remember {
                mutableStateOf(OpenGraphMetaData(title = item.fsName, url = item.data!!))
            }
            LaunchedEffect(Unit){
                metaData = getMetaData(item.data!!,item.fsName)
            }
            if (metaData?.imageUrl?.isNotEmpty()==true) {
                metaData?.let {
                    CardLinkPreview(
                        it, CardLinkPreviewProperties.Builder(
                            imagePainter = rememberImagePainter(metaData!!.imageUrl)
                        ).build()
                    )
                }
            } else {
                metaData?.let { CardLinkPreview(it) }
            }
            Log.d("previewCard","$metaData")
        }
}
@Composable
fun CreateDir(isVisible:Boolean,cancel:()->Unit,save:(name:String)->Unit){
    var editedName by remember { mutableStateOf("New Dir") }
    if (isVisible){
        AlertDialog(
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
fun CreateFile(isVisible: Boolean,cancel:()->Unit,save:(name:String,url:String)->Unit){
    var editedName by remember { mutableStateOf("New File") }
    var editedUrl by remember { mutableStateOf("https://") }
    if (isVisible){
    AlertDialog(
        onDismissRequest = {
            cancel()
        },
        title = { Text(text = "New Url",modifier = Modifier.padding(0.dp),style = TextStyle(
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
            Text(text = "Url:",modifier = Modifier.padding(5.dp),style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Thin
            ))
            OutlinedTextField(
                value = editedUrl,
                onValueChange = { editedUrl = it },
                modifier = Modifier.fillMaxWidth()
            )
        } },
        confirmButton = {
            TextButton(
                onClick = {
                    if(editedName!=""&&editedUrl!=""){
                        save(editedName,editedUrl)
                        cancel()
                    }
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

suspend fun getMetaData(url:String,name:String):OpenGraphMetaData{
    return OpenGraphMetaDataProvider().startFetchingMetadataAsync(URL(url)).getOrDefault(OpenGraphMetaData(url =url, title = name))
}