package com.philpot.nowplayinghistory.history

import androidx.recyclerview.widget.RecyclerView
import com.philpot.nowplayinghistory.widget.DeletableViewHolder


/**
 * Created by MattPhilpot on 12/2/2017.
 */
class HistoryViewHolder() : RecyclerView.ViewHolder(), DeletableViewHolder {

    /*
    var deleteToggledListener: RecyclerViewItemDeleteToggled<HistoryItem>? = null
    private var deleteEnabled = false
    private var lastFmEnabled = false
    */

    init {

    }

    /*
    override val favoriteView: FavoriteButton?
        get() = itemView?.viewholder_history_item_favorite

    fun updateWithItem(entity: HistoryItem?, deleteEnabled: Boolean, lastFmEnabled: Boolean) {
        this.deleteEnabled = deleteEnabled
        this.lastFmEnabled = lastFmEnabled
        updateWithItem(entity)
    }

    override fun updateView(newEntity: HistoryItem?) {
        itemView?.let { view ->
            view.viewholder_history_item_title?.text = newEntity?.title ?: ""
            view.viewholder_history_item_artist?.text = newEntity?.artist ?: ""
            view.viewholder_history_item_date?.text = Utils.formatDateTime(newEntity?.timestamp)
            view.viewholder_history_item_last_heard?.text = formatLastHeard(newEntity?.timestamp, newEntity?.songInfo)
            view.viewholder_history_item_art_flipview?.flipSilently(deleteEnabled)

            deleteToggledListener?.let { listener ->
                view.viewholder_history_item_art_flipview.setOnClickListener {
                    listener.deleteToggled(newEntity, this)
                }
            }

            view.viewholder_history_item_favorite?.isFavorite = newEntity?.songInfo?.favorite ?: false

            newEntity?.songInfo?.albumInfo?.albumBitmap?.let {
                view.viewholder_history_item_art_flipview?.setFrontImageBitmap(it)
                return
            }

            view.viewholder_history_item_art_flipview?.setFrontImage(R.drawable.ic_music_note_white)
            view.setBackgroundColor(ContextCompat.getColor(view.context, if (deleteEnabled) R.color.colorDisabledLight else R.color.colorBackground))
        }
    }


    private fun formatLastHeard(timeStamp: Long?, songInfo: SongInfo?): String {
        timeStamp?.let { currentStamp ->
            songInfo?.let {
                if (it.heardCount > 1 && currentStamp > it.lastHeard) {
                    return itemView.context.getString(R.string.last_heard_message, it.heardCount.toString(), SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date(it.lastHeard)))
                }
            }
        }
        return ""
    }

    override fun toggleDelete(enabled: Boolean) {
        this.deleteEnabled = enabled
        if (enabled) {
            animateChanges(R.color.colorBackground, R.color.colorDisabledLight)
        } else {
            animateChanges(R.color.colorDisabledLight, R.color.colorBackground)
        }
    }

    private fun animateChanges(fromColor: Int, toColor: Int, animate: Boolean = true) {
        if (animate) {
            itemView?.viewholder_history_item_art_flipview?.flip(deleteEnabled)
            val color1 = ContextCompat.getColor(itemView.context, fromColor)
            val color2 = ContextCompat.getColor(itemView.context, toColor)

            val anim = ValueAnimator()
            anim.setIntValues(color1, color2)
            anim.duration = 150
            anim.setEvaluator(ArgbEvaluator())
            anim.addUpdateListener { animation -> itemView.setBackgroundColor(animation.animatedValue as Int) }
            anim.start()
        } else {
            itemView?.viewholder_history_item_art_flipview?.flipSilently(deleteEnabled)
            itemView?.setBackgroundColor(toColor)
        }
    }
    */
}