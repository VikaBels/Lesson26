package com.example.lesson26.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.R
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.utils.cancelNotification
import com.example.lesson26.utils.getIdError
import com.example.lesson26.utils.scheduleNotification

class EditNotificationViewModel(
    private val dataRepository: DataRepository,
) : BaseViewModel() {
    private var uiError = MutableLiveData<UIError>()
    private val errorTextNotification = MutableLiveData<Int>()
    private val _isCorrectData = MutableLiveData<Boolean>()

    val error: LiveData<UIError>
        get() = uiError

    val errorNotificationField: LiveData<Int>
        get() = errorTextNotification

    val isCorrectData: LiveData<Boolean>
        get() = _isCorrectData

    fun editNotification(
        time: Long,
        text: String,
        newTime: Long,
        newText: String?,
        customTime: Long,
        currentTime: Long,
        token: String?
    ) {
        val notification = getValidNotificationText(newText)

        if (customTime > currentTime &&
            notification != null &&
            token != null
        ) {
            _isCorrectData.value = true

            cancelNotification(time, text)

            scheduleNotification(
                customTime,
                currentTime,
                notification,
                token
            )

            changeNotificationDataBase(
                time,
                text,
                newTime,
                notification
            )
        } else {
            _isCorrectData.value = false
            uiError.value = UIError(R.string.notification_schedule_error)
        }
    }

    private fun changeNotificationDataBase(
        time: Long,
        text: String,
        newTime: Long,
        notification: String
    ) {
        dataRepository.changeNotificationTask(
            getToken(),
            time,
            text,
            newTime,
            notification
        ).continueWith({

            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun getValidNotificationText(notification: String?): String? {
        return when {
            notification.isNullOrEmpty() -> {
                errorTextNotification.value = R.string.error_empty_notification_field
                null
            }
            else -> {
                errorTextNotification.value = R.string.no_error
                notification
            }
        }
    }
}