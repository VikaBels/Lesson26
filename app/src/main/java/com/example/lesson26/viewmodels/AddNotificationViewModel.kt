package com.example.lesson26.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.R
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.utils.getIdError
import com.example.lesson26.utils.scheduleNotification

class AddNotificationViewModel(
    private val dataRepository: DataRepository,
) : BaseViewModel() {
    private val uiError = MutableLiveData<UIError>()
    private val errorTextNotification = MutableLiveData<Int>()
    private val _isCorrectData = MutableLiveData<Boolean>()

    val error: LiveData<UIError>
        get() = uiError

    val errorNotificationField: LiveData<Int>
        get() = errorTextNotification

    val isCorrectData: LiveData<Boolean>
        get() = _isCorrectData

    fun addNewNotification(
        notificationText: String?,
        customTime: Long,
        currentTime: Long,
        dateTime: Long,
        userToken: String?
    ) {
        val notification = getValidNotificationText(notificationText)

        if (customTime > currentTime &&
            notification != null &&
            userToken != null
        ) {
            _isCorrectData.value = true

            scheduleNotification(
                customTime,
                currentTime,
                notification,
                userToken
            )

            addNewReminderDataBase(
                dateTime,
                notification,
                userToken
            )
        } else {
            _isCorrectData.value = false
            uiError.value = UIError(R.string.notification_schedule_error)
        }
    }

    private fun addNewReminderDataBase(
        time: Long,
        text: String,
        tokenUser: String
    ) {
        dataRepository.addNewNotificationTask(
            getToken(),
            time,
            text,
            tokenUser
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