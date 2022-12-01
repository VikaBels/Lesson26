package com.example.lesson26.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.os.bundleOf
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lesson26.R
import com.example.lesson26.activities.JoggingActivity
import com.example.lesson26.activities.JoggingActivity.Companion.TAG_FOR_SEND_TOKEN_NOTIFICATION

class NotifyWorker(
    context: Context,
    params: WorkerParameters,
) : Worker(context, params) {
    companion object {
        const val NOTIFICATION_TEXT = "appName_notification_text"
        const val NOTIFICATION_ID = "appName_notification_id"
        const val USER_TOKEN = "USER_TOKEN"

        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
        const val NOTIFICATION_WORK = "appName_notification_work"

        const val REQUEST_CODE = 0
    }

    override fun doWork(): Result {
        val id = inputData.getInt(NOTIFICATION_ID, 0)
        val text = inputData.getString(NOTIFICATION_TEXT)
        val token = inputData.getString(USER_TOKEN)

        if (text != null && token != null) {
            sendNotification(id, text, token)
        }

        return success()
    }

    private fun sendNotification(id: Int, textNotification: String, token: String) {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            createNotificationChannel(channelNotification())
            notify(id, setNotification(textNotification, token).build())
        }
    }

    private fun channelNotification(): NotificationChannel {
        val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
            .setContentType(CONTENT_TYPE_SONIFICATION).build()

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_NAME,
            IMPORTANCE_HIGH
        )

        channel.apply {
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            enableVibration(true)
            setSound(ringtoneManager, audioAttributes)
        }

        return channel
    }

    private fun setNotification(
        textNotification: String,
        token: String
    ): NotificationCompat.Builder {
        val titleNotification = applicationContext.getString(R.string.notification_title)
        val pendingIntent =
            getActivity(applicationContext, REQUEST_CODE, getIntent(token), FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
            .setContentTitle(titleNotification)
            .setContentText(textNotification)
            .setDefaults(DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notification.apply {
            priority = PRIORITY_MAX
            setChannelId(NOTIFICATION_CHANNEL)
        }

        return notification
    }

    private fun getIntent(token: String): Intent {
        return Intent(applicationContext, JoggingActivity::class.java).apply {
            putExtras(
                bundleOf(TAG_FOR_SEND_TOKEN_NOTIFICATION to token)
            )
        }
    }
}