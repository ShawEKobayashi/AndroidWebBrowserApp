package edu.temple.browsr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

class BookmarkAdapter(private val titleList:ArrayList<String>, val selectFun:(String)->Unit,val deleteFun:(Int)->Unit):
    RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {


    inner class BookmarkViewHolder(layout: View): RecyclerView.ViewHolder(layout) {
            val textView: TextView=layout.findViewById(R.id.textView)
            val deleteButton: ImageView=layout.findViewById(R.id.delete)

        init{
            textView.setOnClickListener{selectFun(textView.text.toString())}
            deleteButton.setOnClickListener{deleteFun(adapterPosition)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        return BookmarkViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.bookmark_recyclerview,
                parent,false))
    }

    override fun getItemCount(): Int = titleList.size

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.textView.text=titleList[position]
    }


}