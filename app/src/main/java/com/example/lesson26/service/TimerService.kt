package com.example.lesson26.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.lesson26.tasks.TimeTask
import java.util.*

class TimerService : Service() {
    companion object {
        const val BROADCAST_ACTION_UPDATE_TIMER = "TimerService.BROADCAST_ACTION_UPDATE_TIMER"
        const val EXTRA_RESULT_TIME = "EXTRA_RESULT_TIME"

        const val DELAY_TIME: Long = 0
        const val PERIOD_TIME: Long = 1
    }

    private val timer = Timer()

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getDoubleExtra(EXTRA_RESULT_TIME, 0.0)
        timer.scheduleAtFixedRate(
            TimeTask(time),
            DELAY_TIME,
            PERIOD_TIME
        )
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}
