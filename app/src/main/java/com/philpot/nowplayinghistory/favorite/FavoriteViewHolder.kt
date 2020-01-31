package com.philpot.nowplayinghistory.favorite

import android.view.View
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.info.bottomsheet.SongInfoHistoryItemView
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.widget.RecyclerViewHolder
import com.philpot.nowplayinghistory.util.Utils.setVisibilityIfNeeded
import com.philpot.nowplayinghistory.widget.RecyclerViewItemExpandCollapseListener
import kotlinx.android.synthetic.main.viewholder_favorite_item.view.*

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class FavoriteViewHolder(itemView: View) : RecyclerViewHolder<SongInfo>(itemView) {

    var expandCollapseListener: RecyclerViewItemExpandCollapseListener<SongInfo>? = null

    init {

        itemView.viewholder_favorite_item_click_to_expand_collapse?.let { expandCollapseView ->
            expandCollapseView.setOnClickListener {
                itemView.viewholder_favorite_item_history_root?.let { root ->
                    if (root.visibility == View.VISIBLE) {
                        root.visibility = View.GONE
                        expandCollapseView.setText(R.string.favorites_view_holder_show_history)
                        expandCollapseListener?.onCollapsed(entity, this)
                    } else {
                        root.visibility = View.VISIBLE
                        expandCollapseView.setText(R.string.favorites_view_holder_hide_history)
                        expandCollapseListener?.onExpanded(entity, this)
                    }
                }
            }
        }
    }

    override fun updateView(newEntity: SongInfo?) {
        itemView.viewholder_favorite_item_title?.text = newEntity?.title ?: ""
        itemView.viewholder_favorite_item_artist?.text = newEntity?.artist ?: ""
        itemView.viewholder_favorite_item_album?.text = newEntity?.album ?: ""

        if (newEntity?.isExpanded == true) {
            itemView.viewholder_favorite_item_history_root?.setVisibilityIfNeeded(View.VISIBLE)
            itemView.viewholder_favorite_item_click_to_expand_collapse?.setText(R.string.favorites_view_holder_hide_history)
        } else {
            itemView.viewholder_favorite_item_history_root?.setVisibilityIfNeeded(View.GONE)
            itemView.viewholder_favorite_item_click_to_expand_collapse?.setText(R.string.favorites_view_holder_show_history)
        }

        newEntity?.albumInfo?.albumBitmap?.let {
            itemView.viewholder_favorite_item_album_art?.setImageBitmap(it)
            return
        }

        itemView.viewholder_favorite_item_album_art?.setImageResource(R.drawable.ic_music_note_white)
    }

    fun updateHistoryList(list: List<HistoryItem>?) {
        itemView.viewholder_favorite_item_history_root?.let { view ->
            view.removeAllViews()

            list?.forEach {
                view.addView(getHistoryView(it))
            }
        }
    }

    private fun getHistoryView(item: HistoryItem): View {
        val retVal = SongInfoHistoryItemView(itemView.context)
        retVal.updateWith(item)
        return retVal
    }

}