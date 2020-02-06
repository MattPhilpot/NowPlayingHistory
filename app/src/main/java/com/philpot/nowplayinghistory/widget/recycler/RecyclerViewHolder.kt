package com.philpot.nowplayinghistory.widget.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewHolder<E>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected var entity: E? = null

    fun updateWithItem(entity: E?) {
        this.entity = entity
        updateView(entity)
    }

    protected abstract fun updateView(newEntity: E?)

    fun setItemClickListener(itemOnClickListener: RecyclerViewItemClicked<E>) {
        itemView?.setOnClickListener {
            itemOnClickListener.itemClicked(entity, this)
        }
    }

    fun setItemLongClickListener(itemOnLongClickListener: RecyclerViewItemLongClicked<E>) {
        itemView?.setOnLongClickListener {
            itemOnLongClickListener.itemLongClicked(entity, this)
        }
    }
}