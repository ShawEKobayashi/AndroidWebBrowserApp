package edu.temple.browsr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

class MainActivity : AppCompatActivity(),
    TabFragment.ControlInterface, BookmarkBarFragment.ICurrentPage{

     private val bookmarkSave: Bookmarks by lazy {
         Bookmarks(this)
     }

    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
    }

    private val recyclerView: RecyclerView? by lazy {
        findViewById(R.id.recyclerView)
    }

    private val tabLayout: TabLayout? by lazy {
        findViewById(R.id.tabLayout)
    }

    private val browserViewModel : BrowserViewModel by lazy {
        ViewModelProvider(this)[BrowserViewModel::class.java]
    }

    private val lastSession by lazy{bookmarkSave.getSession()}


    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK){
            it.data?.apply{
                deleteBookmarks(getStringArrayListExtra(BM_DELETE_LIST)!!)
                var urlToLoad = getStringExtra(SELECTED_BOOKMARK)!!
                if(urlToLoad!="none"){
                    newPage()
                    browserViewModel.loadPage(viewPager.currentItem,browserViewModel.getUrl(urlToLoad))
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = browserViewModel.getNumberOfTabs()

            // Each TabFragment maintains an ID assigned at instantiation.
            // This is used to notify the parent that a specific tab wants to update its title
            override fun createFragment(position: Int)=TabFragment.newInstance(position)
        }


        // Only if present (portrait)
        tabLayout?.run{

            // Keeps ViewPager and TabLayout selection in sync automatically
            // lambda updates title
            TabLayoutMediator(this, viewPager) { tab, position ->
                tab.text = browserViewModel.getPage(position).title
            }.attach()
        }

        // Only if present (landscape)
        recyclerView?.run{
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = PageAdapter(browserViewModel.tabs){
                viewPager.setCurrentItem(it, true)
            }
        }

        // Observe titles and update TabLayout or RecyclerView
        browserViewModel.getUpdate().observe(this) {
            viewPager.adapter?.notifyItemChanged(it)
            recyclerView?.adapter?.notifyItemChanged(it)
        }


        if (savedInstanceState==null){
            browserViewModel.setBookmarks(bookmarkSave.getBM())
            if(lastSession.isNotEmpty()) {
                if(!(lastSession.size==1)||!(lastSession.elementAt(0)=="")){
                val restorePage: String =
                    "<html><head><title>Restore Previous Session</title></head><script>clickHolder=-1</script><body style='padding-top:200px'><a href='https://restoresession.com/' style='display:block;padding:15px;border-radius:10px;font-size:24px;width:300px;height:100px;background-color:cyan;margin:auto'> Click Here to Restore Previous Session</body></html>"
                browserViewModel.loadPage(-2, restorePage)
                }
            }
        }

    }

    override fun onStop() {
        bookmarkSave.saveSession(browserViewModel.getUrlList())
        bookmarkSave.saveBM(browserViewModel.getBookmarks())
        super.onStop()
    }

    // TabFragment.ControlInterface callback
    override fun newPage() {
        browserViewModel.addTab()
        viewPager.setCurrentItem(browserViewModel.getNumberOfTabs() - 1, true)
    }

    override fun currentPage(): Page? {
        val pageTemp = browserViewModel.getPage(viewPager.currentItem)
        if(pageTemp.url=="")
        {
            Toast.makeText(this@MainActivity, "Visit a Page First!", Toast.LENGTH_LONG)
                .show()
            return null
        }
        else {
            return pageTemp
        }
    }

    override fun restoreSession() {

        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch {
            var count = 0
            for (url in lastSession) {
                delay(800)
                viewPager.currentItem = count
                browserViewModel.loadPage(count, url)
                if (count < lastSession.size - 1) {
                    newPage()
                }
                count++
            }
        }
    }

    override fun getBookmarkLauncher(): ActivityResultLauncher<Intent> = launcher

    fun deleteBookmarks(removedBookmarks: ArrayList<String>){
        for(title in removedBookmarks){
            browserViewModel.getBookmarks().removeAt(browserViewModel.getTitleIndex(title))
        }
    }
}