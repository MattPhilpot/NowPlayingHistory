package com.philpot.nowplayinghistory.widget.recycler

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

abstract class RecyclerAdapter<E, H : RecyclerViewHolder<E>>(diffUtil: DiffUtil.ItemCallback<E>) : PagedListAdapter<E, H>(diffUtil) {

    var itemOnClick: RecyclerViewItemClicked<E>? = null
    var itemOnLongClick: RecyclerViewItemLongClicked<E>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        val viewHolder = constructViewHolder(parent, viewType)
        itemOnClick?.let {
            viewHolder.setItemClickListener(it)
        }

        itemOnLongClick?.let {
            viewHolder.setItemLongClickListener(it)
        }

        return viewHolder
    }

    protected abstract fun constructViewHolder(parent: ViewGroup, viewType: Int): H

    override fun onBindViewHolder(holder: H, position: Int) {
        holder.updateWithItem(getItem(position))
    }
}
