package com.hunter.urlvault.fileSystem

import android.util.Log

class Tests {
    fun execTests(fs:FileSystem){
        val t1 = fs.createDir("root","A")
        val t2 = fs.createDir("root/A","E")
        val t3 = fs.createDir("root/A","F")

        Log.d("test-result","$t1 $t2 $t3")

        val t4 = fs.createDir("root","B")
        val t5 = fs.createDir("root/B","G")
        val t6 = fs.createDir("root/B","H")

        Log.d("test-result","$t4 $t5 $t6")

        val t7 = fs.createDir("root","C")
        val t8 = fs.createDir("root/C","I")
        val t9 = fs.createDir("root/C","J")

        Log.d("test-result","$t7 $t8 $t9")

        val t10 = fs.createFile("root/A/E","M.txt")
        val t11 = fs.createFile("root/A/F","N.txt")
        val t12 = fs.createFile("root/B/G","O.txt")
        val t13 = fs.createFile("root/B/H","P.txt")
        val t14 = fs.createFile("root/C/I","Q.txt")
        val t15 = fs.createFile("root/C/J","R.txt")

        Log.d("test-result","$t10 $t11 $t12 $t13 $t14 $t15")

        val t16 = fs.listDir("root")
        val t17 = fs.listDir("root/A")
        val t18 = fs.listDir("root/B")
        val t19 = fs.listDir("root/C")

        Log.d("test-result","root contains $t16")
        Log.d("test-result","root/A contains $t17")
        Log.d("test-result","root/B contains $t18")
        Log.d("test-result","root/c contains $t19")

        val t20 = fs.deleteNode("root/C/J/R.txt")
        val t21 = fs.listDir("root/C/J")
        Log.d("test-result","$t20 | root/c/j contains $t21")

        val t22 = fs.deleteNode("root/C")
        val t23 = fs.listDir("root")
        Log.d("test-result","$t22 | root contains $t23")

    }
}