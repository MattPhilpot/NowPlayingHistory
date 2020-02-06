package com.philpot.nowplayinghistory.history2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.philpot.nowplayinghistory.databinding.FragmentHistoryBinding
import com.philpot.nowplayinghistory.fragment.NowPlayingFragment
import com.philpot.nowplayinghistory.info.bottomsheet.SongInfoBottomSheet
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.repo.SyncResult
import com.philpot.nowplayinghistory.viewmodel.NowPlayingViewModelFactory
import com.philpot.nowplayinghistory.widget.recycler.RecyclerViewHolder
import com.philpot.nowplayinghistory.widget.recycler.RecyclerViewInitializer
import com.philpot.nowplayinghistory.widget.recycler.RecyclerViewItemClicked
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

class History2Fragment : NowPlayingFragment() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)

        bind<History2ListAdapter>() with provider {
            History2ListAdapter()
        }
    }

    private lateinit var binding: FragmentHistoryBinding
    private val adapter by instance<History2ListAdapter>()

    private val viewModel: History2ViewModel by viewModels { NowPlayingViewModelFactory(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false).apply {
            RecyclerViewInitializer.initRecyclerView(context, fragmentHistoryList, adapter, RecyclerView.VERTICAL, true)
            hasContentToDisplay = true
            loading = true

            fragmentHistorySwipeRefresh.setOnRefreshListener {
                viewModel.refresh()
            }
        }

        val bottomSheetBehavior = BottomSheetBehavior.from<SongInfoBottomSheet>(binding.fragmentHistorySongInfo)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) { /* do nothing */ }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.fragmentHistoryBottomSheetFab.animate().scaleX(0F).scaleY(0F).setDuration(150L).start()
                    //fragment_history_shadow?.setVisibilityIfNeeded(View.INVISIBLE)
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (binding.fragmentHistoryBottomSheetFab.scaleX < 1F) {
                        binding.fragmentHistoryBottomSheetFab.animate().scaleX(1F).scaleY(1F).setDuration(150L).start()
                    }
                }
            }
        })

        /*
        adapter.itemOnClick = object: RecyclerViewItemClicked<HistoryItem> {
            override fun itemClicked(model: HistoryItem?, holder: RecyclerViewHolder<HistoryItem>) {
                model?.let {
                    if (adapter.isDeleteModeEnabled() && holder is HistoryViewHolder) {
                        holder.toggleDelete(adapter.deleteToggled(it))
                    } else {
                        binding.fragmentHistorySongInfo.updateWith(it)
                        bottomSheetBehavior.let { behavior ->
                            if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                //fragment_history_shadow?.setVisibilityIfNeeded(View.VISIBLE)
                                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        }
                    }
                }
            }
        }

        adapter.confirmDeleteListener = object : HistoryListAdapter.DeleteableListener {
            override fun updateConfirmDelete() {
                activity?.invalidateOptionsMenu()
            }
        }
        */


        refreshContent()
        return binding.root
    }

    private fun refreshContent() {
        viewModel.data().observe(viewLifecycleOwner) { result ->
            when (result.status) {
                SyncResult.Status.LOADING -> {
                    binding.loading = true
                    binding.hasContentToDisplay = true
                }
                SyncResult.Status.SUCCESS_LOCAL, SyncResult.Status.SUCCESS_REMOTE -> {
                    binding.loading = false
                    binding.hasContentToDisplay = result.data?.isNotEmpty() == true || adapter.itemCount > 0
                    adapter.submitList(result.data)
                }
                SyncResult.Status.ERROR -> {
                    binding.loading = false
                    binding.hasContentToDisplay = false
                }
            }
        }
    }

    interface ItemClickedCallback {
        fun onItemClicked(item: HistoryItem?)
    }
}