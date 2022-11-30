package com.example.lesson26.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.lesson26.App.Companion.getDataRepository
import com.example.lesson26.R
import com.example.lesson26.TIME_FORMAT_SHOW
import com.example.lesson26.databinding.FragmentTrackBinding
import com.example.lesson26.factories.TrackViewModelFactory
import com.example.lesson26.models.TrackInfo
import com.example.lesson26.models.Track
import com.example.lesson26.utils.getFormattedDistance
import com.example.lesson26.utils.getFormattedTime
import com.example.lesson26.viewmodels.TrackViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TrackFragment : Fragment() {
    companion object {
        private const val KEY_SEND_INFO = "KEY_SEND_INFO"
        private const val ZOOM = 13f
        private const val MIN_ZOOM = 2f

        fun newInstance(sendingInfo: TrackInfo?): Fragment {
            val trackFragment = TrackFragment()
            trackFragment.arguments = bundleOf(KEY_SEND_INFO to sendingInfo)
            return trackFragment
        }
    }

    private var bindingTrackFragment: FragmentTrackBinding? = null
    private var trackInfo: TrackInfo? = null

    private lateinit var myMap: MapView

    private val trackViewModel by viewModels<TrackViewModel> {
        TrackViewModelFactory(
            getInfo(),
            getDataRepository()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingTrackFragment = FragmentTrackBinding.inflate(layoutInflater)
        this.bindingTrackFragment = bindingTrackFragment

        myMap = bindingTrackFragment.mapView
        initializeGoogleMap(savedInstanceState)

        return bindingTrackFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackInfo = getInfo()

        setVisibleProgressbar(true)

        checkTrackItem(trackInfo?.track)

        observeError()

        observePoints()
    }

    override fun onResume() {
        super.onResume()
        myMap.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingTrackFragment = null
        myMap.onDestroy()
    }

    private fun setVisibleProgressbar(isVisible: Boolean) {
        bindingTrackFragment?.progressBar?.isVisible = isVisible
    }

    private fun observeError() {
        trackViewModel.error.observe(viewLifecycleOwner) { error ->
            setTextError(error.textId)
        }
    }

    private fun observePoints() {
        trackViewModel.points.observe(viewLifecycleOwner) { listPoint ->
            setVisibleProgressbar(false)

            if (listPoint.isEmpty()) {
                setTextError(R.string.error_no_coordinates)
            } else {

                setMapWithPoints(
                    listPoint[0].lat,
                    listPoint[0].lng,
                    listPoint[1].lat,
                    listPoint[1].lng,
                )
            }
        }
    }

    private fun initializeGoogleMap(
        savedInstanceState: Bundle?
    ) {
        myMap.onCreate(savedInstanceState)

        try {
            MapsInitializer.initialize(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //rename
    private fun setMapWithPoints(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
    ) {
        myMap.getMapAsync { mMap ->
            //map koordinati
            //https://www.mapsdirections.info/ru/GPS-%D0%BA%D0%BE%D0%BE%D1%80%D0%B4%D0%B8%D0%BD%D0%B0%D1%82%D1%8B-Google-%D0%9A%D0%B0%D1%80%D1%82%D0%B0%D1%85.html

            val markerStart = LatLng(startLat, startLng)
            mMap.addMarker(
                MarkerOptions()
                    .position(markerStart)
                    .title(
                        resources.getString(R.string.txt_marker_start)
                    )
            )

            val markerFinish = LatLng(endLat, endLng)
            mMap.addMarker(
                MarkerOptions()
                    .position(markerFinish)
                    .title(
                        resources.getString(R.string.txt_marker_finish)
                    )
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
            )

            val centerPath = getCenterOfPath(
                startLat, startLng,
                endLat, endLng
            )

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPath, ZOOM))
            mMap.setMinZoomPreference(MIN_ZOOM)
        }
    }

    //rename
    //mb don't need -> different ways ??
    private fun getCenterOfPath(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
    ): LatLng {
        val latHelper = (startLat + endLat) / 2
        val lngHelper = (startLng + endLng) / 2

        return LatLng(latHelper, lngHelper)
    }

    private fun checkTrackItem(track: Track?) {
        if (track != null) {
            setTrackInfo(track)
        } else {
            setTextError(R.string.error_no_info)
        }
    }

    private fun setTextError(idError: Int) {
        bindingTrackFragment?.apply {
            textError.text = resources.getString(idError)
            textError.isVisible = true
        }
    }

    private fun getInfo(): TrackInfo? {
        return arguments?.getParcelable(KEY_SEND_INFO)
    }

    private fun setTrackInfo(track: Track) {
        bindingTrackFragment?.textViewDistance?.text =
            resources.getString(
                R.string.txt_text_view_distance,
                getFormattedDistance(track.distance)
            )
        bindingTrackFragment?.textViewTime?.text =
            resources.getString(
                R.string.txt_text_view_time,
                getFormattedTime(track.time, TIME_FORMAT_SHOW)
            )
    }
}