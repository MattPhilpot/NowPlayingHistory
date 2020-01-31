package com.philpot.nowplayinghistory.favorite

import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.fragment.NowPlayingFragment
import com.philpot.nowplayinghistory.model.Preferences
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.util.RecyclerViewInitializer
import com.philpot.nowplayinghistory.widget.RecyclerViewHolder
import com.philpot.nowplayinghistory.widget.RecyclerViewItemClicked
import kotlinx.android.synthetic.main.fragment_favorites.*

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class FavoriteFragment : NowPlayingFragment(), FavoriteController.FavoriteView {

    override fun provideOverridingModule() = Kodein.Module {
        bind<FavoriteController>() with provider {
            FavoriteController(this@FavoriteFragment, instance(), instance())
        }

        bind<FavoriteListAdapter>() with provider {
            FavoriteListAdapter(instance(), instance())
        }
    }

    override val fragmentTitle: Int = R.string.title_activity_main_favorites
    override val fragmentTag: String = FavoriteFragment::class.java.simpleName

    private val controller by instance<FavoriteController>()
    private val listAdapter by instance<FavoriteListAdapter>()
    private val preferences by instance<SharedPreferences>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter.lastFmEnabled = preferences.getBoolean(Preferences.LastFmIntegration.value, false)

        listAdapter.itemOnClick = object : RecyclerViewItemClicked<SongInfo> {
            override fun itemClicked(model: SongInfo?, holder: RecyclerViewHolder<SongInfo>) {

            }
        }

        RecyclerViewInitializer.initRecyclerView(activity, fragment_favorites_list, listAdapter, false)

        controller.initialize()
    }

    override fun onResume() {
        super.onResume()
        onControllerResume()
    }

    fun onControllerResume() {
        if (isAdded) {
            controller.onResume()
        }
    }

    override fun updateFavorites(list: List<SongInfo>) {
        activity?.runOnUiThread {
            listAdapter.replaceListWith(list)
        }
    }
}