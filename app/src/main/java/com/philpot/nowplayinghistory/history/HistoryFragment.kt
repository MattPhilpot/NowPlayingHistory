package com.philpot.nowplayinghistory.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.content.ContextCompat
import android.system.Os.bind
import android.view.*
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.activity.NowPlayingActivity
import com.philpot.nowplayinghistory.fragment.NowPlayingFragment
import com.philpot.nowplayinghistory.info.bottomsheet.SongInfoBottomSheet
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.MusicAppPreference
import com.philpot.nowplayinghistory.model.Preferences
import com.philpot.nowplayinghistory.settings.SettingsBottomSheetDialog
import com.philpot.nowplayinghistory.util.RecyclerViewInitializer
import com.philpot.nowplayinghistory.util.Utils
import com.philpot.nowplayinghistory.util.Utils.setVisibilityIfNeeded
import com.philpot.nowplayinghistory.widget.RecyclerViewHolder
import com.philpot.nowplayinghistory.widget.RecyclerViewItemClicked
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class HistoryFragment : NowPlayingFragment(), HistoryController.HistoryView {

    override fun provideOverridingModule() = Kodein.Module {
        bind<HistoryController>() with provider {
            HistoryController(instance(), instance(), instance(), instance(), this@HistoryFragment)
        }

        bind<HistoryListAdapter>() with provider {
            HistoryListAdapter(instance())
        }
    }

    override val fragmentTitle: Int = R.string.title_activity_main
    override val fragmentTag: String = HistoryFragment::class.java.simpleName

    private val controller by instance<HistoryController>()
    private val listAdapter by instance<HistoryListAdapter>()
    private val preferences by instance<SharedPreferences>()

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
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

        listAdapter.itemOnClick = object: RecyclerViewItemClicked<HistoryItem> {
            override fun itemClicked(model: HistoryItem?, holder: RecyclerViewHolder<HistoryItem>) {
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

    override fun addPaginatedHistory(list: List<HistoryItem>) {
        fragment_history_list?.post {
            listAdapter.addPaginatedList(list)
        }
    }

    override fun addHistoryItemToTop(item: HistoryItem) {
        listAdapter.addItem(item)
        if (preferences.getBoolean(Preferences.ScrollToTop.value, false)) {
            fragment_history_list?.smoothScrollToPosition(0)
        }
    }

    override fun replaceHistoryListWith(list: List<HistoryItem>) {
        listAdapter.replaceListWith(list)
    }


    override fun addHistoryItemsToTop(list: List<HistoryItem>) {
        listAdapter.addItemsToTop(list)
    }
}