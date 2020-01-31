package com.philpot.nowplayinghistory.history

import android.util.LongSparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db.manager.SongAlbumManager

import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.widget.*
import com.philpot.nowplayinghistory.widget.favorite.RecyclerViewItemFavoriteClicked
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class HistoryListAdapter(private val songAlbumManager: SongAlbumManager) :
        RecyclerAdapter<HistoryItem, HistoryViewHolder>(),
        RecyclerSectionItemDecoration.SectionCallback,
        RecyclerViewItemDeleteToggled<HistoryItem> {

    private val deleteToggledList = LongSparseArray<HistoryItem>()
    var lastFmEnabled = false
        set(value) {
            songAlbumManager.allowLastFM = value
            field = value
        }
    var confirmDeleteListener: DeleteableListener? = null

    val firstItemId: Long?
        get() = entityList.firstOrNull()?.timestamp

    init {
        itemOnLongClick = object : RecyclerViewItemLongClicked<HistoryItem> {
            override fun itemLongClicked(model: HistoryItem?, holder: RecyclerViewHolder<HistoryItem>): Boolean {
                model?.let {
                    if (holder is HistoryViewHolder) {
                        holder.toggleDelete(deleteToggled(it))
                    }
                }
                return true
            }
        }
    }

    fun isDeleteModeEnabled(): Boolean = deleteToggledList.size() > 0

    internal fun deleteToggled(item: HistoryItem): Boolean {
        return if (deleteToggledList.indexOfKey(item.timestamp) == -1) {
            deleteToggledList.put(item.timestamp, item)
            confirmDeleteListener?.updateConfirmDelete()
            true
        } else {
            deleteToggledList.remove(item.timestamp)
            confirmDeleteListener?.updateConfirmDelete()
            false
        }
    }

    /*
    override fun addItem(item: HistoryItem) {
        item.songInfo = songAlbumManager.getSongInfoFor(item, lastFmEnabled)
        super.addItem(item)
    }
    */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val retVal =  super.onCreateViewHolder(parent, viewType)
        retVal.deleteToggledListener = this
        return retVal
    }

    override fun onBindViewHolder(holder: HistoryViewHolder?, position: Int) {
        holder?.let { viewHolder ->
            val historyItem = getItemAt(position)

            historyItem?.let {
                viewHolder.updateWithItem(it, deleteToggledList.indexOfKey(it.timestamp) >= 0, lastFmEnabled)
                viewHolder.setItemFavoriteListener(object : RecyclerViewItemFavoriteClicked<HistoryItem> {
                    override fun itemFavoriteClicked(model: HistoryItem?, holder: RecyclerViewHolder<HistoryItem>, isFavorite: Boolean) {
                        model?.songInfo?.let {
                            it.favorite = isFavorite
                            songAlbumManager.saveFavorite(it)
                        }
                    }
                })
            }
        }
    }

    override fun getItemAt(position: Int): HistoryItem? {
        val retVal = super.getItemAt(position)

        retVal?.let {
            if (it.songInfo?.albumInfo?.albumBitmap == null) {
                it.songInfo = songAlbumManager.getSongInfoFor(it)
            }
        }

        return retVal
    }

    override fun constructViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_history_item, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun isSection(position: Int): Boolean =
            position != -1 && (position == 0 || !areSameDay(entityList[position].timestamp, entityList[position - 1].timestamp))

    override fun getSectionHeader(position: Int): CharSequence =
            formatDateTime(entityList[position].timestamp)

    private fun areSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = Date(timestamp1)
        cal2.time = Date(timestamp2)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun formatDateTime(timestamp: Long?): String {
        timestamp?.let {
            return SimpleDateFormat("EEEE, MMMM dd, yyyy ", Locale.ENGLISH).format(Date(it))
        }
        return ""
    }

    fun replaceListWith(list: List<HistoryItem>) {
        entityList.clear()
        entityList.addAll(list)
        notifyDataSetChanged()
    }

    override fun deleteToggled(model: HistoryItem?, holder: DeletableViewHolder) {
        model?.let {
            holder.toggleDelete(deleteToggled(it))
        }
    }

    fun deleteAndReturnItems(): List<HistoryItem> {
        val retVal = mutableListOf<HistoryItem>()
        (0 until deleteToggledList.size()).mapTo(retVal) { deleteToggledList.valueAt(it) }
        for(each in retVal.reversed()) {
            removeItem(each)
        }
        deleteToggledList.clear()
        return retVal
    }

    interface DeleteableListener {
        fun updateConfirmDelete()
    }

    /*
    fun deleteAndReturnItems(): List<HistoryItem> {
        val retVal = mutableListOf<HistoryItem>()
        for (i in deleteToggledList.size() - 1..0) {
            val item = deleteToggledList.valueAt(i)
            retVal.add(item)
            removeItem(item)
        }
        deleteToggledList.clear()
        return retVal
    }
     */
}
