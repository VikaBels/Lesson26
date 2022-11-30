package com.example.lesson26.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lesson26.R
import com.example.lesson26.activities.JoggingActivity

class NotifyWorker(
    context: Context,
    params: WorkerParameters,
) : Worker(context, params) {
    companion object {
        const val NOTIFICATION_TEXT = "appName_notification_text"
        const val NOTIFICATION_ID = "appName_notification_id"

        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
        const val NOTIFICATION_WORK = "appName_notification_work"
    }

    override fun doWork(): Result {
        val id = inputData.getInt(NOTIFICATION_ID, 0)
        val text = inputData.getString(NOTIFICATION_TEXT)

        if (text != null) {
            sendNotification(id, text)
        }

        return success()
    }

    private fun sendNotification(id: Int, textNotification: String) {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            createNotificationChannel(channelNotification())
            notify(id, setNotification(textNotification).build())
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
            enableLights(true)
            lightColor = RED
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            setSound(ringtoneManager, audioAttributes)
        }

        return channel
    }

    private fun setNotification(textNotification: String): NotificationCompat.Builder {
        val titleNotification = applicationContext.getString(R.string.notification_title)
        val pendingIntent = getActivity(applicationContext, 0, getIntent(), FLAG_IMMUTABLE)

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

    private fun getIntent(): Intent {
        val intent = Intent(applicationContext, JoggingActivity::class.java)

        intent.apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NOTIFICATION_ID, id)
        }

        return intent
    }
}