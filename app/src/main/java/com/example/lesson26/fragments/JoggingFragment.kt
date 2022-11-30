package com.example.lesson26.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.lesson26.App
import com.example.lesson26.App.Companion.getDataRepository
import com.example.lesson26.App.Companion.getInstanceApp
import com.example.lesson26.R
import com.example.lesson26.databinding.FragmentJoggingBinding
import com.example.lesson26.factories.JoggingViewModelFactory
import com.example.lesson26.interfaes.JoggingFragmentListener
import com.example.lesson26.service.TimerService
import com.example.lesson26.service.TimerService.Companion.BROADCAST_ACTION_UPDATE_TIMER
import com.example.lesson26.service.TimerService.Companion.EXTRA_RESULT_TIME
import com.example.lesson26.tasks.TimeHelper
import com.example.lesson26.utils.getFormattedDistance
import com.example.lesson26.utils.isOnline
import com.example.lesson26.utils.refactorDateTime
import com.example.lesson26.viewmodels.JoggingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class JoggingFragment : Fragment() {
    companion object {
        private const val KEY_SEND_TOKEN = "KEY_SEND_TOKEN"
        const val POINT_LIST_QUANTITY = 2

        fun newInstance(token: String?): Fragment {
            val joggingFragment = JoggingFragment()
            joggingFragment.arguments = bundleOf(KEY_SEND_TOKEN to token)
            return joggingFragment
        }
    }

    private var bindingJoggingFragment: FragmentJoggingBinding? = null
    private var joggingFragmentListener: JoggingFragmentListener? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var serviceIntent: Intent? = null

    private var timeStart: Long = 0
    private var time = 0.0
    private var distance = 0L

    private val timerServiceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val timerService = intent.getParcelableExtra<TimeHelper>(EXTRA_RESULT_TIME)
            onNewServiceReceiver(timerService)
        }
    }

    private val joggingViewModel by viewModels<JoggingViewModel> {
        JoggingViewModelFactory(
            getDataRepository()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        joggingFragmentListener = context as? JoggingFragmentListener
            ?: error("$context${resources.getString(R.string.exceptionInterface)}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingJoggingFragment = FragmentJoggingBinding.inflate(layoutInflater)
        this.bindingJoggingFragment = bindingJoggingFragment

        return bindingJoggingFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLocationService()

        serviceIntent = Intent(context, TimerService::class.java)

        setUpListeners()

        observeError()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(getInstanceApp()).registerReceiver(
            timerServiceReceiver,
            IntentFilter(BROADCAST_ACTION_UPDATE_TIMER)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(getInstanceApp()).unregisterReceiver(
            timerServiceReceiver
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingJoggingFragment = null
    }

    override fun onDetach() {
        super.onDetach()
        joggingFragmentListener = null
    }

    private fun observeError() {
        joggingViewModel.error.observe(viewLifecycleOwner) { error ->
            showTextError(error.textId)
        }
    }

    private fun setLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getInstanceApp())
    }

    //mb don't Toast ???
    private fun showTextError(idError: Int) {
        Toast.makeText(
            requireContext(),
            resources.getString(idError),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun onNewServiceReceiver(currentTime: TimeHelper?) {
        if (currentTime != null && currentTime.time != 0.0) {
            time = currentTime.time
            setTimeField(currentTime.timeString)
        }
    }

    private fun setTimeField(currentTime: String) {
        bindingJoggingFragment?.textViewTimer?.text = currentTime
    }

    private fun setUpListeners() {
        bindingJoggingFragment?.apply {
            buttonStart.setOnClickListener {
                onClickStart()
            }

            buttonFinish.setOnClickListener {
                onClickFinish()
            }
        }
    }

    private fun onClickStart() {
        fusedLocationClient?.let { client ->
            startTimer()

            joggingViewModel.getCurrentLocation(client, requireContext()){
                timeStart = refactorDateTime(System.currentTimeMillis())

                showJoggingFields()

                joggingFragmentListener?.onClickStart(true)
            }
        }
    }

    private fun onClickFinish() {
        if (!isOnline()) {
            showTextError(R.string.error_no_internet)
        }

        fusedLocationClient?.let { client ->
            stopTimer()

            joggingViewModel.getCurrentLocation(client, requireContext()){
                saveInfoJogging()

                showStatisticJoggingFields()

                joggingFragmentListener?.onClickStart(false)
            }
        }
    }

    private fun startTimer() {
        serviceIntent?.putExtra(EXTRA_RESULT_TIME, time)
        context?.startService(serviceIntent)
    }

    private fun stopTimer() {
        context?.stopService(serviceIntent)
    }

    private fun showJoggingFields() {
        bindingJoggingFragment?.apply {
            textViewTimer.isVisible = true
            buttonFinish.isVisible = true
            buttonStart.isVisible = false
        }
    }

    private fun showStatisticJoggingFields() {
        bindingJoggingFragment?.apply {
            buttonFinish.isVisible = false
            textViewDistance.isVisible = true

            textViewDistance.text = resources.getString(
                R.string.txt_text_view_distance_metre,
                getFormattedDistance(distance)
            )
        }
    }

    private fun saveInfoJogging() {
        val points = joggingViewModel.listPoints

        distance = joggingViewModel.currentDistance

        if (points.size < POINT_LIST_QUANTITY) return

        val pointStart = points.first()
        val pointEnd = points.last()


        ///delete
        val help = "${pointStart.lat} " +
                "${pointStart.lng} " +
                "${pointEnd.lat} " +
                "${pointEnd.lng} "

        Toast.makeText(requireContext(), help, Toast.LENGTH_LONG).show()
        ///

        joggingViewModel.addNewTrack(
            getToken(),
            null,
            timeStart,
            time,
            distance,
            listOf(pointStart, pointEnd)
        )
    }

    private fun getToken(): String? {
        return arguments?.getString(KEY_SEND_TOKEN)
    }
}