package edu.temple.browsr

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.net.URL

class BrowserViewModel : ViewModel(){

    private var bookmarks: ArrayList<Page> = ArrayList()


    private val updateProxy : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply {
            value = 0
        }
    }

    private val urlLoadIndex: MutableLiveData<Int> by lazy{
        MutableLiveData<Int>().apply{
            value=-1
        }
    }

    private var urlToLoad: String = ""

    val tabs : ArrayList<Page> by lazy {
        ArrayList<Page>().apply {
            add(Page())
        }
    }

    fun getUpdate() : LiveData<Int> {
        return updateProxy
    }

    fun addTab() {
        tabs.add(Page())
        updateProxy.value = tabs.size - 1
    }

    fun getPage(position: Int) = tabs[position]

    fun getNumberOfTabs() = tabs.size

    fun updatePage(position: Int, page: Page) {
        tabs[position] = page
        updateProxy.value = position
    }

    fun setBookmarks(newBookmarks: ArrayList<Page>){
        bookmarks = newBookmarks
    }

    fun getBookmarks() = bookmarks

    fun getUrlList():ArrayList<String>{
        return ArrayList<String>().apply{
            for (page in tabs){
                add(page.url)
            }
        }
    }

    fun getTitleIndex(inTitle: String):Int {
        for (page in bookmarks) {
            if (page.title == inTitle) {
                return bookmarks.indexOf(page)
            }
        }
        return -1
    }

    fun getUrl(title: String): String{
        return bookmarks[getTitleIndex(title)].url
    }

    fun loadPage(index: Int, stringURL: String){
        urlToLoad=stringURL
        urlLoadIndex.value = index
    }
    fun getLoadIndex():MutableLiveData<Int> = urlLoadIndex

    fun getUrlToLoad(): String = urlToLoad

}