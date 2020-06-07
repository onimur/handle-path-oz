/*
 *
 *  * Created by Murillo Comino on 06/06/20 22:23
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 06/06/20 22:04
 *
 */

package br.com.comino.example.handlepathoz.kotlin

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.comino.example.handlepathoz.R

class ListUriAdapter(private val list: MutableList<Uri>) :
    RecyclerView.Adapter<ListUriAdapter.ListUriViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListUriViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.text_view_recycler, parent, false) as TextView
        return ListUriViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holderListUri: ListUriViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val string = "${position + 1} - ${list[position]}"
        holderListUri.textView.text = string
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }

    //UpdateList
    fun updateListChanged(newList: List<Uri>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    inner class ListUriViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

}
