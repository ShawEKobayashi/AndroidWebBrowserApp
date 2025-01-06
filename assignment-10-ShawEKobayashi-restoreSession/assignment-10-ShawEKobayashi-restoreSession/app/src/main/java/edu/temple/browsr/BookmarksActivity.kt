package edu.temple.browsr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val BOOKMARK_ARRAYLIST = "bookmark_arraylist"
const val BM_DELETE_LIST = "bookmark_arraylist_delete"
const val SELECTED_BOOKMARK = "selected_bookmark"
class BookmarksActivity : AppCompatActivity() {

    private var deleteList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bookmarks)

        savedInstanceState?.run{
            deleteList = this.getStringArrayList(BM_DELETE_LIST)!!
        }

        val titleList = intent.getStringArrayListExtra(BOOKMARK_ARRAYLIST)!!
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val bmSelected:(String)->Unit = {
            setResult(RESULT_OK, Intent().putExtra(BM_DELETE_LIST, deleteList).putExtra(
                SELECTED_BOOKMARK,it))
            finish()
        }

        val bmDeleted:(Int)->Unit = {
            deleteList.add(titleList[it])
            titleList.removeAt(it)
            recyclerView.adapter!!.notifyItemRemoved(it)
        }

        recyclerView.adapter = BookmarkAdapter(titleList,bmSelected,bmDeleted)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.button).setOnClickListener{
            setResult(RESULT_OK, Intent().putExtra(BM_DELETE_LIST, deleteList).putExtra(
                SELECTED_BOOKMARK, "none"))
            finish()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(BM_DELETE_LIST,deleteList)
    }

}