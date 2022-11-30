package com.example.lesson26.tasks

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.lesson26.App.Companion.getInstanceApp
import com.example.lesson26.TIME_FORMAT_JOGGING
import com.example.lesson26.service.TimerService.Companion.BROADCAST_ACTION_UPDATE_TIMER
import com.example.lesson26.service.TimerService.Companion.EXTRA_RESULT_TIME
import com.example.lesson26.utils.getFormattedTime
import java.util.*

class TimeTask(
    private var time: Double
) : TimerTask() {

    override fun run() {
        time += 0.06

        val timeHelper = TimeHelper(
            getFormattedTime(time, TIME_FORMAT_JOGGING),
            time
        )

        val intent = Intent(BROADCAST_ACTION_UPDATE_TIMER)
        intent.putExtra(EXTRA_RESULT_TIME, timeHelper)
        LocalBroadcastManager.getInstance(getInstanceApp()).sendBroadcast(intent)
    }
}

