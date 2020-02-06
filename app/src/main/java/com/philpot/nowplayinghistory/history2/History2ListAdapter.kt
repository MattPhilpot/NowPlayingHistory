package com.philpot.nowplayinghistory.history2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.widget.recycler.RecyclerAdapter
import com.philpot.nowplayinghistory.widget.recycler.RecyclerSectionItemDecoration
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat


class History2ListAdapter : RecyclerAdapter<HistoryItem, History2ViewHolder>(History2Diff()), RecyclerSectionItemDecoration.SectionCallback {

    val fmt: SimpleDateFormat = SimpleDateFormat("yyyyMMdd") //TODO - make me better

    override fun constructViewHolder(parent: ViewGroup, viewType: Int): History2ViewHolder {
        return History2ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.viewholder_history_item, parent, false))
    }


    //for sections
    override val hasSections: Boolean = true
    override var showColumnCount: Int = 1

    override fun isSection(position: Int): Boolean =
        position != -1 && (position == 0 || !areSameDay(getItem(position)?.historyEntry?.timestamp, getItem(position - 1)?.historyEntry?.timestamp))

    override fun getSectionHeader(position: Int): CharSequence = getItem(position)?.historyEntry?.timestamp?.toString(DateTimeFormat.fullDate()) ?: ""

    private fun areSameDay(timestamp1: DateTime?, timestamp2: DateTime?): Boolean {
        return fmt.format(timestamp1) == fmt.format(timestamp2)
    }

    override fun getSectionColor(position: Int): Int = Color.parseColor("#2196F3")

    override fun getSectionTextColor(position: Int): Int = Color.WHITE

    override fun getSectionDrawable(position: Int): Int = 0
}
