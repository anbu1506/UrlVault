package com.hunter.urlvault.fileSystem

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class FileSystem(private val db: SQLiteDatabase?) {
    /*
            function to get fields of FsBock { don't use this method to get ChildList }
     */
    private fun getField(FsId:Int,field:String):Any?{
        val c=db?.query("FsBlock", arrayOf(field),"FsId = ?", arrayOf(FsId.toString()),null,null,null,null)
        var value: Any? =null
        if(c?.moveToNext()==true){
            when(field){
                FsBlock.fsParent->{
                    value = c.getInt(0)
                }
                FsBlock.fsType->{
                    value = c.getInt(0)
                }
                FsBlock.fsName->{
                    value = c.getString(0)
                }
            }
        }
        else{
            when(field){
                FsBlock.fsParent->{
                    value = -1
                }
                FsBlock.fsType->{
                    value = -1
                }
                FsBlock.fsName->{
                    value = "-1"
                }
            }
        }
        Log.d("getField()","$field of $FsId is $value")
        return  value
    }
    /*
          can use this function to retrieve fsChild from FsBlock . Which contains MapOf FsId and Name of its Child directory or files
     */
    private fun getChildList(fsId:Int):MutableMap<Int,String>?{
        if(getField(fsId,FsBlock.fsType) as Int ==1) return null
        if (fsId==0){
            return mutableMapOf(1 to "root")
        }
        val c=db?.query("FsBlock", arrayOf(FsBlock.fsChild),"FsId = ?", arrayOf(fsId.toString()),null,null,null,null)
        return if(c?.moveToNext()==true) {
            val jsonArrayList = c.getString(c.getColumnIndexOrThrow(FsBlock.fsChild))
            val type = object : TypeToken<MutableMap<Int, String>?>() {}.type
            val res=Gson().fromJson<MutableMap<Int, String>?>(jsonArrayList, type)
            Log.d("getChildList()","the childList of $fsId is $res")
            res
        } else{
            Log.d("getChildList()","the childList of $fsId is null")
            null
        }

    }
    private fun getData(fsId: Int):String?{
        if(getField(fsId,FsBlock.fsType) as Int == 0) return "Invalid operation - Dir can't contain data"
        val c=db?.query("FsBlock", arrayOf(FsBlock.fsChild),"FsId = ?", arrayOf(fsId.toString()),null,null,null,null)
        return if(c?.moveToNext()==true){
            c.getString(0)
        }
        else null
    }
    /*
            function to update Child list in FsBlock
     */
    private fun setChildList(fsId: Int,map:MutableMap<Int, String>?):Int{
        if(getField(fsId,FsBlock.fsType)==1)return  -1
        val gson = Gson().toJson(map)
        val values = ContentValues().apply {
            put(FsBlock.fsChild,gson)
        }

        val selection="${FsBlock.fsID} = ?"
        return  db?.update(
            "FsBlock",
            values,
            selection,
            arrayOf(fsId.toString())
        )!!
    }
    private fun setField(FsId: Int,field: String,data:Any):Int{
        val values=ContentValues()
        when(field){
            FsBlock.fsType->{
                values.apply {
                    put(FsBlock.fsType,data as Int)
                }
            }
            FsBlock.fsParent->{
                values.apply {
                    put(FsBlock.fsParent,data as Int)
                }
            }
            FsBlock.fsName->{
                values.apply {
                    put(FsBlock.fsName,data as String)
                }
            }
        }
        return db?.update(
            "FsBlock",
            values,
            "FsId = ?",
            arrayOf(FsId.toString())
        )!!
    }
    /*
            function to convert path to FsId
     */
    private fun pathToFsId(path:String):Int{
        if(path.startsWith("/")||path.endsWith("/")||path.contains('\\')) return -1
        val split = path.split("/")
        var fsId= 0
        for(paths in split){
            val map= getChildList(fsId)
            val node = map?.entries?.find { it.value == paths }?.key
            Log.d("pathToFsId","$paths exists ${node!=null}")
            if (node==null)return -1
            else fsId = node
        }
        Log.d("pathToFsId()","the fsId of the path $path is $fsId")
        return fsId
    }
    private fun fsIdToPath(fsId: Int):String{
        var id = fsId
        var path = ""
        while (id!=1){
            path ="/"+getField(id,FsBlock.fsName) as String +path
            id = getField(id,FsBlock.fsParent)as Int
        }
        path = "root$path"
        return path
    }
    private fun createNode(path:String, name:String, type:Int):String{
        Log.d("createNode","path = $path")
        val parentFsId = pathToFsId(path)
        /*
               if parent Path is invalid we can't get it's FsId
         */
        if(parentFsId==-1) return "Invalid Path"
        /*
               if path specifies a FIle we can't create subdir to it
         */
        if(getField(parentFsId,FsBlock.fsType) as Int == 1) return "Warning: can't create Node under a File !"
        /*
               check whether a child with tha "name" already exists or not
         */
        val map= getChildList(parentFsId)
        Log.d("createNode","is map empty = ${map?.isEmpty()}")
        //if (map==null) return "invalid path"
        if(map?.containsValue(name) == true) return "Name Already Exists !"
        /*
               here create an empty childList for Dir or "empty data" for File
         */
        val emptyChildOrData=if(type==0)Gson().toJson(mutableMapOf<Int,String>())else "-1"
        val values = ContentValues().apply {
            put(FsBlock.fsName,name)
            put(FsBlock.fsType,type)
            put(FsBlock.fsParent,parentFsId)
            put(FsBlock.fsChild,emptyChildOrData)
        }
        Log.d("createNode","$name with $type is created with $values")
        val result= db?.insert("FsBlock",null,values)?.toInt()!!
        return if(result==-1) "Node not created!"
        else {
            map?.apply {
                put(result,name)
            }
            if(setChildList(parentFsId, map) ==-1){
                db.delete(
                    "FsBlock","FsId = ?", arrayOf(result.toString())
                )
                return "node not Created !"
            }
            "Node Created SuccessFully !"
        }
    }
    fun createFile(path:String,name:String):String{
        return createNode(path,name,1)
    }
    fun createDir(path:String,name:String):String{
        return createNode(path,name,0)
    }
    fun listDir(path:String): Map<Int, String>? {
        val fsId = pathToFsId(path)
        return if(getField(fsId,FsBlock.fsType) as Int ==0)
            getChildList(fsId)
        else null
    }
    fun listNode(path: String):List<Node>{
        val fsId = pathToFsId(path)
        val child = getChildList(fsId)
        val list = mutableListOf<Node>()
        if (child != null) {
            for (i in child){
                list.add(node(i.key))
            }

        }
        return list
    }
    private fun getSubDir(fsId: Int):ArrayList<String>{
        val result = ArrayList<String>()
        val map = getChildList(fsId)
        if(map!=null){
            for (node in map){
                result.add(node.key.toString())
                result.addAll(getSubDir(node.key))
            }
        }
        return result
    }
    fun deleteNode(path: String):String{
        val fsId = pathToFsId(path)
        if (fsId==-1)return "invalid path!"
        val type = getField(fsId,FsBlock.fsType) as Int
        if(type==1){
            val parent = getField(fsId,FsBlock.fsParent) as Int
            val childList = getChildList(parent)
            Log.d("deleteFile","parent's  childList of $path is $childList")
            childList?.remove(fsId)
            setChildList(parent,childList)
            Log.d("deleteFile","parent's childList of $path ${getChildList(parent)}")
            val result = db?.delete("FsBlock","FsId = ?", arrayOf(fsId.toString()))
            return if(result==-1)
                "file not deleted!"
            else {

                "file deleted successfully :)"
            }
        } else{
            val subDirs=getSubDir(fsId)
            subDirs.add(fsId.toString())
            var count=0
            val parent = getField(fsId,FsBlock.fsParent) as Int
            val childList = getChildList(parent)
            Log.d("deleteFile","parent's  childList of $path is $childList")
            childList?.remove(fsId)
            setChildList(parent,childList)
            Log.d("deleteFile","parent's childList of $path ${getChildList(parent)}")
            for(id in subDirs){
                val result=db?.delete("FsBlock","FsId = ?", arrayOf(id))
                if(result==1)count++
            }
            return "$count nodes deleted successfully :)"
        }
    }
    fun readFile(path: String):String?{
        val fsId = pathToFsId(path)
        if(getField(fsId,FsBlock.fsType) as Int == 0) return "Invalid operation - Dir can't contain data"
        val c=db?.query("FsBlock", arrayOf(FsBlock.fsChild),"FsId = ?", arrayOf(fsId.toString()),null,null,null,null)
        return if(c?.moveToNext()==true){
            c.getString(0)
        }
        else null
    }
    fun writeFile(path:String,data:String):Int{
        val fsId = pathToFsId(path)
        if(getField(fsId,FsBlock.fsType) as Int == 0) return -1
        val values=ContentValues().apply {
            put(FsBlock.fsChild,data)
        }
        return db?.update(
            "FsBlock",
            values,
            "FsId = ?",
            arrayOf(fsId.toString())
        )!!
    }
    fun move(pathFrom:String,pathTo:String):String{
        val fsIdSource = pathToFsId(pathFrom)
        val fsIdDestination = pathToFsId(pathTo)
        if(fsIdSource==-1 || fsIdDestination==-1) return "invalid path"
        if(getField(fsIdDestination,FsBlock.fsType) as Int ==1)return "can't move"
        val nodeName = getField(fsIdSource,FsBlock.fsName) as String
        val parent = getField(fsIdSource,FsBlock.fsParent) as Int
        val parentChildList = getChildList(parent)
        parentChildList?.remove(fsIdSource)
        val destination = getChildList(fsIdDestination)
        return if(destination?.containsValue(nodeName)==true) "$nodeName already exists in destination Dir"
        else {
            destination?.put(fsIdDestination,nodeName)
            if(setChildList(fsIdDestination,destination )==-1) "failed!"
            else {
                setChildList(parent,parentChildList)
                setField(fsIdSource,FsBlock.fsParent,fsIdDestination)
                "$nodeName moved from $pathFrom to $pathTo Successfully"}
        }
    }
    fun rename(path:String,newName:String): String {
        val fsId = pathToFsId(path)
        if(fsId==-1) return "invalid path !"
        val parent = getField(fsId,FsBlock.fsParent) as Int
        val parentChildList = getChildList(parent)
        return if(parentChildList?.containsValue(newName)==true)
            "name already exists !"
        else{
            parentChildList?.remove(fsId)
            parentChildList?.put(fsId,newName)
            setChildList(parent,parentChildList)
            setField(fsId,FsBlock.fsName,newName)
            "name changed successfully!"
        }
    }
    private fun node(fsId: Int):Node{
        return if (getField(fsId,FsBlock.fsType) as Int ==0) Dir(
            fsId,
            getField(fsId,FsBlock.fsName) as String,
            getField(fsId,FsBlock.fsType) as Int,
            getField(fsId,FsBlock.fsParent) as Int,
            fsIdToPath(fsId),
            getChildList(fsId)
        )
        else File(
            fsId,
            getField(fsId,FsBlock.fsName) as String,
            getField(fsId,FsBlock.fsType) as Int,
            getField(fsId,FsBlock.fsParent) as Int,
            fsIdToPath(fsId),
            getData(fsId)
        )
    }
    interface Node{
        val fsId: Int
        val fsName: String
        val fsType:Int
        val fsParent: Int
        val path:String
    }
    data class Dir (
        override val fsId: Int,
        override val fsName: String,
        override val fsType: Int,
        override val fsParent: Int,
        override val path: String,
        val FsChild:Map<Int,String>?,
    ):Node

    data class File(
        override val fsId: Int,
        override val fsName: String,
        override val fsType: Int,
        override val fsParent: Int,
        override val path: String,
        val data:String?
    ):Node
}