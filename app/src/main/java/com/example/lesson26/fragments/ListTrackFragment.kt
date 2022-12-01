package com.example.lesson26.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lesson26.App.Companion.getDataRepository
import com.example.lesson26.R
import com.example.lesson26.adapters.TrackAdapter
import com.example.lesson26.databinding.FragmentListTrackBinding
import com.example.lesson26.factories.TrackListViewModelFactory
import com.example.lesson26.interfaes.TrackListener
import com.example.lesson26.interfaes.TracksScreenNavigationListener
import com.example.lesson26.models.Track
import com.example.lesson26.viewmodels.TrackListViewModel

class ListTrackFragment : Fragment(),
    TrackListener {
    companion object {
        private const val KEY_SEND_POST = "KEY_SEND_POST"

        fun newInstance(token: String?): Fragment {
            val listTrackFragment = ListTrackFragment()
            listTrackFragment.arguments = bundleOf(KEY_SEND_POST to token)
            return listTrackFragment
        }
    }

    private var tracksScreenNavigationListener: TracksScreenNavigationListener? = null
    private var bindingListTrackFragment: FragmentListTrackBinding? = null
    private var trackAdapter: TrackAdapter? = null

    private val trackListViewModel by viewModels<TrackListViewModel> {
        TrackListViewModelFactory(
            getToken(),
            getDataRepository()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        tracksScreenNavigationListener = context as? TracksScreenNavigationListener
            ?: error("$context${resources.getString(R.string.exceptionInterface)}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingListTrack = FragmentListTrackBinding.inflate(layoutInflater)
        this.bindingListTrackFragment = bindingListTrack

        return bindingListTrack.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(this)

        setVisibleProgressbar(true)

        setUpAdapter()

        observeError()

        observeListTrack()

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingListTrackFragment = null
    }

    override fun onDetach() {
        super.onDetach()
        tracksScreenNavigationListener = null
    }

    override fun onTrackClick(track: Track) {
        tracksScreenNavigationListener?.showTrackFragment(track)
    }

    private fun setVisibleProgressbar(isVisible: Boolean) {
        bindingListTrackFragment?.progressBar?.isVisible = isVisible
    }

    private fun getToken(): String? {
        return arguments?.getString(KEY_SEND_POST)
    }

    private fun setUpAdapter() {
        bindingListTrackFragment?.listTrack?.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeError() {
        trackListViewModel.error.observe(viewLifecycleOwner) { error ->
            setTextError(error.textId)
        }
    }

    private fun observeListTrack() {
        trackListViewModel.listTrack.observe(viewLifecycleOwner) { listTrack ->
            setVisibleProgressbar(false)

            if (listTrack.isEmpty()) {
                setTextError(R.string.error_no_track_list)
            } else {
                trackAdapter?.setListTrack(listTrack)
            }
        }
    }

    private fun setTextError(idError: Int) {
        bindingListTrackFragment?.apply {
            textError.text = resources.getString(idError)
            textError.isVisible = true
        }
    }

    private fun setUpListeners() {
        bindingListTrackFragment?.fab?.setOnClickListener {
            tracksScreenNavigationListener?.showJoggingActivity()
        }
    }
}