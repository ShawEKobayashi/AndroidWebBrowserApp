package edu.temple.browsr

import android.content.Context
import android.util.Log
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class Bookmarks(val context: Context) {

    private val bookmarkFile = File(context.filesDir,"bookmarks")
    
    fun getBM(): ArrayList<Page>{
        val bookmarkList = ArrayList<Page>()
        if(bookmarkFile.exists()){
            val reader = BufferedReader(FileReader(bookmarkFile))
            var line: String? = reader.readLine()
            while(line!=null){
                val seperatorIndex = line.indexOf("*")
                val title = line.substring(0,seperatorIndex)
                val url = line.substring(seperatorIndex+1)
                bookmarkList.add(Page(title, url))
                line = reader.readLine()
            }
            reader.close()
        }
        return bookmarkList
    }


    fun saveBM(pages: ArrayList<Page>){
        BufferedOutputStream(bookmarkFile.outputStream()).apply {
            val text = StringBuilder()
            for (page in pages) {
                text.append(page.title + "*")
                text.append(page.url + "\n")
            }
            write(text.toString().toByteArray())
            close()
        }
    }

    fun getSession():ArrayList<String>{
        val sessionFile = File(context.filesDir,"last_session")
        val sessionList = ArrayList<String>()
        if(sessionFile.exists()){
            val reader = BufferedReader(FileReader(sessionFile))
            var line: String? = reader.readLine()
            while(line!=null){
                Log.d("Bookmarks", "getSession: "+line)
                sessionList.add(line)
                line = reader.readLine()
            }
            reader.close()
        }
        return sessionList

    }

    fun saveSession(urls:ArrayList<String>){
        val sessionFile = File(context.filesDir, "last_session")
        BufferedOutputStream(sessionFile.outputStream()).apply {
            val text = StringBuilder()
            for (url in urls) {
                text.append(url + "\n")
            }
            Log.d("Bookmarks", "saveSession: "+text.toString())
            write(text.toString().toByteArray())
            close()
        }
    }

}