package com.philpot.nowplayinghistory.widget

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.SearchView
import com.philpot.nowplayinghistory.model.HistoryItem

/**
 * Created by MattPhilpot on 10/30/2017.
 */
abstract class RecyclerAdapter<E, H : RecyclerViewHolder<E>> : RecyclerView.Adapter<H>() {

    var itemOnClick: RecyclerViewItemClicked<E>? = null
    var itemOnLongClick: RecyclerViewItemLongClicked<E>? = null

    protected var entityList: MutableList<E> = arrayListOf()

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

    override fun getItemCount(): Int = entityList.size

    open fun getItemAt(position: Int): E? {
        if (position < entityList.size) {
            return entityList[position]
        }
        return null
    }

    open fun addItem(item: E) {
        entityList.add(0, item)
        //doFiltering()
        notifyItemInserted(0)
    }

    open fun addItemsToTop(list: List<E>) {
        entityList.addAll(0, list)
        notifyItemRangeInserted(0, list.size)
    }

    fun removeItem(item: E) {
        val index = entityList.indexOf(item)
        if (index >= 0 && index < entityList.size) {
            entityList.removeAt(index)
            //doFiltering()
            notifyItemRemoved(index)
        }
    }

    fun addPaginatedList(list: List<E>) {
        val startIndex = entityList.size
        entityList.addAll(list)
        //doFiltering()
        notifyItemRangeInserted(startIndex, list.size)
    }

    /*
    override final fun onQueryTextSubmit(constraint: String?): Boolean {
        return true
    }

    override final fun onQueryTextChange(constraint: String?): Boolean {
        searchValue = constraint?.toLowerCase()?.trim()?.replace(" ", "") ?: ""
        doFiltering()
        notifyDataSetChanged()
        return true
    }

    private fun doFiltering() {
        entityList = filterEntityList(unFilteredList)
    }

    abstract fun filterEntityList(baseList: List<E>): List<E>
    */
}