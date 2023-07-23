package com.hunter.urlvault.fileSystem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson

/*
  Table Schema for FsBlock
 */
object FsBlock{
    const val fsID = "fsID"
    const val fsType = "fsType"
    const val fsName = "fsName"
    const val fsParent = "fsParent"
    const val fsChild = "fsChild"
}

private const val FsBlockEntries = "create table FsBlock(" +
        "${FsBlock.fsID}   integer primary key autoincrement," +
        "${FsBlock.fsType}  integer," +
        "${FsBlock.fsName}     varchar(512)," +
        "${FsBlock.fsParent}   integer," +
        "${FsBlock.fsChild}    text)"

private  const val FsBlock_DELETE_ENTRIES = "DROP TABLE IF EXISTS FsBlock"

/*
        FileSystem class for initializing database and File Management
 */

class Database(context: Context) : SQLiteOpenHelper(context, "UrlVault.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(FsBlockEntries)
        Log.d("Database", "FsBlock Table created!")

        /*
            "root" Directory is created when Database is initialized
         */
        val map = mutableMapOf<Int,String>()
        val childList  = Gson().toJson(map)
        val values = ContentValues().apply {
            put(FsBlock.fsType,0)
            put(FsBlock.fsName,"root")
            put(FsBlock.fsParent,0)
            put(FsBlock.fsID,1)
            put(FsBlock.fsChild,childList)
        }

        val result=db.insert("FsBlock",null,values)
        if(result== (-1).toLong()){
            Log.d("Database","root Directory not created!")

        }
        else{
            Log.d("Database","root Directory Successfully created")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(FsBlock_DELETE_ENTRIES)
        onCreate(db)
        Log.d("Database", "FsBlock Table deleted!")
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}



