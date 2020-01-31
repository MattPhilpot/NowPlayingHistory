package com.philpot.nowplayinghistory.widget

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by MattPhilpot on 10/30/2017.
 */
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