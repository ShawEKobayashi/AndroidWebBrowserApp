package edu.temple.browsr

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.provider.Browser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class BookmarkBarFragment(): Fragment() {

    val SMS_STRING = "sms_body"

    private val browserModel: BrowserViewModel by lazy{
        ViewModelProvider(requireActivity() as ViewModelStoreOwner)[BrowserViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmarkbar,container,false).apply {
            findViewById<ImageView>(R.id.bookmarkPage).setOnClickListener{saveToBookmark()}
            findViewById<ImageView>(R.id.openBookmarks).setOnClickListener{openBookmarks()}
            findViewById<ImageView>(R.id.sharePage).setOnClickListener{sharePage()}
        }
    }

    private fun openBookmarks(){
        val arrayListExtra = ArrayList<String>()
        for (page in browserModel.getBookmarks()){
            arrayListExtra.add(page.title)
        }
        val sendIntent = Intent(requireActivity(),BookmarksActivity::class.java).apply {
            putExtra(BOOKMARK_ARRAYLIST, arrayListExtra)
        }
        (requireActivity() as ICurrentPage).getBookmarkLauncher().launch(sendIntent)
    }

    private fun saveToBookmark(){
        val currentPage = (requireActivity() as ICurrentPage).currentPage()
        currentPage?.let {
            if (browserModel.getTitleIndex(currentPage.title)==-1){
            browserModel.getBookmarks().add(currentPage)
            Toast.makeText(requireActivity(),"Bookmark Created!", Toast.LENGTH_LONG)
                .show()
            }
            else{
                Toast.makeText(
                    requireActivity(),
                    "Bookmark Already Exists! No Bookmark Added",
                    Toast.LENGTH_LONG).show()
        }
        }
    }

    private fun sharePage(){
        val currentPage= (requireActivity() as ICurrentPage).currentPage()
        currentPage?.let {
            val sendIntent = Intent().apply {
                var shareString = currentPage.title + " - " + currentPage.url
                action = Intent.ACTION_SEND
                putExtra(SMS_STRING, shareString)
                type = "text/plain"
            }
            startActivity(sendIntent)
        }
    }

    interface ICurrentPage{
        fun currentPage(): Page?
        fun getBookmarkLauncher():ActivityResultLauncher<Intent>

    }


}