package com.philpot.nowplayinghistory.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.db2.manager.SongAlbumManager
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.Song
import com.philpot.nowplayinghistory.widget.RecyclerAdapter
import com.philpot.nowplayinghistory.widget.RecyclerViewHolder
import com.philpot.nowplayinghistory.widget.RecyclerViewItemExpandCollapseListener


/**
 * Created by MattPhilpot on 12/2/2017.
 */
class FavoriteListAdapter(private val songAlbumManager: SongAlbumManager,
                          private val historyDao: HistoryDao) : RecyclerAdapter<Song, FavoriteViewHolder>() {

    private val songHistoryList = mutableMapOf<String, List<HistoryEntry>>()

    var lastFmEnabled = false

    override fun onBindViewHolder(holder: FavoriteViewHolder?, position: Int) {
        holder?.let { viewHolder ->
            val songItem = getItemAt(position)

            viewHolder.updateWithItem(songItem)
            songItem?.let {
                viewHolder.updateHistoryList(songHistoryList[SongInfoDao.getSongInfoKey(it)])
            }
        }
    }

    override fun constructViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_favorite_item, parent, false)
        val retVal = FavoriteViewHolder(itemView)

        retVal.expandCollapseListener = object : RecyclerViewItemExpandCollapseListener<Song> {
            override fun onExpanded(model: Song?, holder: RecyclerViewHolder<Song>) {
                model?.let {
                    it.isExpanded = true
                    songAlbumManager.saveExpandedOrCollapsed(it)
                }
            }

            override fun onCollapsed(model: Song?, holder: RecyclerViewHolder<Song>) {
                model?.let {
                    it.isExpanded = false
                    songAlbumManager.saveExpandedOrCollapsed(it)
                }
            }
        }

        return retVal
    }

    override fun getItemAt(position: Int): Song? {
        val retVal =  super.getItemAt(position)

        retVal?.let {
            val songKey = SongInfoDao.getSongInfoKey(it)
            if (!songHistoryList.containsKey(songKey)) {
                songHistoryList.put(songKey, historyDao.getHistoryOfSong(it))
            }

            it.album = songAlbumManager.getAlbumInfoFor(it)
        }

        return retVal
    }

    fun replaceListWith(list: List<Song>) {
        songHistoryList.clear()
        entityList.clear()
        entityList.addAll(list)
        notifyDataSetChanged()
    }
}
