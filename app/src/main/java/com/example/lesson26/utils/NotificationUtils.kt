package com.example.lesson26.utils

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lesson26.App
import com.example.lesson26.workers.NotifyWorker
import com.example.lesson26.workers.NotifyWorker.Companion.NOTIFICATION_ID
import com.example.lesson26.workers.NotifyWorker.Companion.NOTIFICATION_TEXT
import com.example.lesson26.workers.NotifyWorker.Companion.NOTIFICATION_WORK
import java.util.concurrent.TimeUnit

fun scheduleNotification(
    customTime: Long,
    currentTime: Long,
    textNotification: String
) {
    val delay = customTime - currentTime

    val data = Data.Builder()
        .putInt(NOTIFICATION_ID, 0)
        .putString(
            NOTIFICATION_TEXT,
            textNotification
        )
        .build()

    val instanceWorkManager = WorkManager.getInstance(App.getInstanceApp())

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
    WorkManager.getInstance(App.getInstanceApp()).cancelAllWorkByTag("$time$text")
}