package com.example.lesson26.utils

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lesson26.App
import com.example.lesson26.App.Companion.getInstanceApp
import com.example.lesson26.workers.NotifyWorker
import com.example.lesson26.workers.NotifyWorker.Companion.NOTIFICATION_ID
import com.example.lesson26.workers.NotifyWorker.Companion.NOTIFICATION_TEXT
import com.example.lesson26.workers.NotifyWorker.Companion.NOTIFICATION_WORK
import com.example.lesson26.workers.NotifyWorker.Companion.USER_TOKEN
import java.util.concurrent.TimeUnit

private const val DEFAULT_ID = 0

fun scheduleNotification(
    customTime: Long,
    currentTime: Long,
    textNotification: String,
    token: String
) {
    val delay = customTime - currentTime

    val data = Data.Builder()
        .putInt(NOTIFICATION_ID, DEFAULT_ID)
        .putString(
            NOTIFICATION_TEXT,
            textNotification
        )
        .putString(USER_TOKEN, token)
        .build()

    val instanceWorkManager = WorkManager.getInstance(getInstanceApp())

    val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .addTag("${refactorDateTime(customTime)}$textNotification")
        .build()

    instanceWorkManager.beginUniqueWork(
        "${NOTIFICATION_WORK}_$data",
        ExistingWorkPolicy.APPEND,
        notificationWork
    ).enqueue()
}

fun cancelNotification(time: Long, text: String) {
    WorkManager.getInstance(getInstanceApp()).cancelAllWorkByTag("$time$text")
}