package com.philpot.nowplayinghistory.history

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.activity.NowPlayingActivity
import com.philpot.nowplayinghistory.databinding.FragmentHistoryBinding
import com.philpot.nowplayinghistory.fragment.NowPlayingFragment
import com.philpot.nowplayinghistory.info.bottomsheet.SongInfoBottomSheet
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.MusicAppPreference
import com.philpot.nowplayinghistory.model.Preferences
import com.philpot.nowplayinghistory.util.RecyclerViewInitializer
import com.philpot.nowplayinghistory.util.Utils
import com.philpot.nowplayinghistory.widget.RecyclerViewHolder
import com.philpot.nowplayinghistory.widget.RecyclerViewItemClicked
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class HistoryFragment : NowPlayingFragment(), HistoryController.HistoryView {

    override val kodein: Kodein = Kodein.lazy {
        bind<HistoryListAdapter>() with provider {
            HistoryListAdapter(instance())
        }
    }

    private val controller by instance<HistoryController>()
    private val listAdapter by instance<HistoryListAdapter>()
    private val preferences by instance<SharedPreferences>()

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false).apply {
            fragmentHistoryList.adapter = listAdapter
        }

        return binding.root
    }

    private fun enableDisableMenuItem(item: MenuItem?, enable: Boolean) {
        item?.let {
            it.isEnabled = enable
            it.isVisible = enable
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_history_options, menu)

        val deleteModeEnabled = listAdapter.isDeleteModeEnabled()
        //enableDisableMenuItem(menu.findItem(R.id.activity_main_option_delete_all), deleteModeEnabled)
        enableDisableMenuItem(menu.findItem(R.id.fragment_history_option_delete), deleteModeEnabled)

        if (deleteModeEnabled) {
            activity?.activity_main_toolbar?.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        } else {
            activity?.activity_main_toolbar?.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item?.itemId) {
        R.id.fragment_history_option_delete -> {
            controller.doDeleteHistoryItems(listAdapter.deleteAndReturnItems())
            activity?.invalidateOptionsMenu()
            //invalidateOptionsMenu()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val kodein = appKodein.invoke()
        fragment_history_song_info?.initialize(kodein.instance(), kodein.instance(), kodein.instance())

        val bottomSheetBehavior = BottomSheetBehavior.from<SongInfoBottomSheet>(fragment_history_song_info)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    fragment_history_bottom_sheet_fab.animate().scaleX(0F).scaleY(0F).setDuration(150L).start()
                    //fragment_history_shadow?.setVisibilityIfNeeded(View.INVISIBLE)
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (fragment_history_bottom_sheet_fab.scaleX < 1F) {
                        fragment_history_bottom_sheet_fab.animate().scaleX(1F).scaleY(1F).setDuration(150L).start()
                    }
                }
            }

        })

        listAdapter.lastFmEnabled = preferences.getBoolean(Preferences.LastFmIntegration.value, false)

        listAdapter.itemOnClick = object: RecyclerViewItemClicked<HistoryEntry> {
            override fun itemClicked(model: HistoryEntry?, holder: RecyclerViewHolder<HistoryEntry>) {
                model?.let {
                    if (listAdapter.isDeleteModeEnabled() && holder is HistoryViewHolder) {
                        holder.toggleDelete(listAdapter.deleteToggled(it))
                    } else {
                        fragment_history_song_info?.updateWith(it)
                        bottomSheetBehavior?.let { behavior ->
                            if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                //fragment_history_shadow?.setVisibilityIfNeeded(View.VISIBLE)
                                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        }
                    }
                }
            }
        }

        listAdapter.confirmDeleteListener = object : HistoryListAdapter.DeleteableListener {
            override fun updateConfirmDelete() {
                activity?.invalidateOptionsMenu()
            }
        }

        RecyclerViewInitializer.initRecyclerView(activity, fragment_history_list, listAdapter, true, controller)

        updateFabColor()

        fragment_history_bottom_sheet_fab?.setOnClickListener {
            fragment_history_song_info?.currentHistoryItem?.let {
                Utils.startMusicAppIntent(activity, it, MusicAppPreference.getFromOrdinal(preferences.getInt(Preferences.CurrentMusicApp.value, 0)))
            }
        }

        controller.initialize()
    }

    override fun onResume() {
        super.onResume()
        onControllerResume()
    }

    fun onControllerResume() {
        if (isAdded) {
            controller.onResume(listAdapter.firstItemId)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }

    private fun updateFabColor() {
        fragment_history_bottom_sheet_fab?.let { view ->
            view.post {
                val musicAppPreference = MusicAppPreference.getFromOrdinal(preferences.getInt(Preferences.CurrentMusicApp.value, 0))
                view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, musicAppPreference.color))
                if (musicAppPreference == MusicAppPreference.None) {
                    view.setVisibilityIfNeeded(View.GONE)
                } else {
                    view.setVisibilityIfNeeded(View.VISIBLE)
                }
            }
        }
    }

    override fun updateListAdapterSettings() {
        listAdapter.lastFmEnabled = preferences.getBoolean(Preferences.LastFmIntegration.value, false)
        updateFabColor()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NowPlayingActivity.REQUEST_CODE) {
            controller.onResume(listAdapter.firstItemId)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun addPaginatedHistory(list: List<HistoryEntry>) {
        fragment_history_list?.post {
            listAdapter.addPaginatedList(list)
        }
    }

    override fun addHistoryItemToTop(entry: HistoryEntry) {
        listAdapter.addItem(entry)
        if (preferences.getBoolean(Preferences.ScrollToTop.value, false)) {
            fragment_history_list?.smoothScrollToPosition(0)
        }
    }

    override fun replaceHistoryListWith(list: List<HistoryEntry>) {
        listAdapter.replaceListWith(list)
    }


    override fun addHistoryItemsToTop(list: List<HistoryEntry>) {
        listAdapter.addItemsToTop(list)
    }
}