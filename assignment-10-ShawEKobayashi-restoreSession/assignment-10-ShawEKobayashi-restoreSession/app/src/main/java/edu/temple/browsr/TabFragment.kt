package edu.temple.browsr

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

class TabFragment : Fragment(), ControlFragment.ControlInterface, PageFragment.PageInterface {

    private val TAB_ID = "tabId"

    private var tabId = 0

    private lateinit var browserActivity: ControlInterface

    private lateinit var pageFragment: PageFragment

    private val browserViewModel : BrowserViewModel by lazy {
        ViewModelProvider(requireActivity())[BrowserViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        browserActivity = context as ControlInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            tabId = getInt(TAB_ID)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false).apply {
            pageFragment = childFragmentManager.findFragmentById(R.id.page) as PageFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        browserViewModel.getLoadIndex().observe(viewLifecycleOwner){
            if(tabId==it) {
                loadUrl(browserViewModel.getUrlToLoad())
            }
            if(it==-2){
                pageFragment.loadData(browserViewModel.getUrlToLoad())
            }
        }
    }

    override fun loadUrl(url: String) {
        pageFragment.loadUrl(url)
    }

    override fun nextPage() {
        pageFragment.goNext()
    }

    override fun backPage() {
        pageFragment.goBack()
    }

    // route invocation from child fragment to parent activity
    override fun newPage() {
        browserActivity.newPage()
    }

    interface ControlInterface {
        fun newPage()
        fun restoreSession()
    }

    interface SessionInterface {

    }

    companion object {
        fun newInstance(tabId: Int) = TabFragment().apply {
            arguments = Bundle().apply {
                putInt(TAB_ID, tabId)
            }
        }
    }

    override fun updatePage(page: Page) {
        host?.run {
            browserViewModel.updatePage(tabId, page)
        }
    }

    override fun restoreSession() {
        (requireActivity() as ControlInterface).restoreSession()
    }
}