package com.example.lesson26.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.R
import com.example.lesson26.models.Notification
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.utils.cancelNotification
import com.example.lesson26.utils.getIdError

class ListNotificationViewModel(
    private val dataRepository: DataRepository,
    private val token: String?
) : BaseViewModel() {
    private var uiError = MutableLiveData<UIError>()
    private var notificationList = MutableLiveData<List<Notification>>()

    val error: LiveData<UIError>
        get() = uiError

    val listNotification: LiveData<List<Notification>>
        get() = notificationList

    init {
        getAllNotification()
    }

    private fun getAllNotification() {
        if (token != null) {
            startDataBaseRequest(token)
        } else {
            //Change
            uiError.value = UIError(R.string.error_something_wrong)
        }
    }

    private fun startDataBaseRequest(
        token: String
    ) {
        dataRepository.getAllNotificationTask(
            getToken(),
            token
        ).continueWith({
            if (it.result != null) {
                checkResponseListNotification(it.result)
            }

            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR)
    }

    fun deleteNotification(
        time: Long,
        text: String,
        tokenUser: String?
    ) {
        if (tokenUser != null) {
            dataRepository.deleteNotificationTask(
                getToken(),
                time,
                text,
                tokenUser
            ).continueWith({

                if (it.error != null) {
                    uiError.value = UIError(getIdError(it.error))
                } else {
                    startDataBaseRequest(tokenUser)
                    cancelNotification(time, text)
                }

            }, Task.UI_THREAD_EXECUTOR)

        } else {
            //change text error
            uiError.value = UIError(R.string.error_something_wrong)
        }
    }

    private fun checkResponseListNotification(listNotification: List<Notification>?) {
        when (listNotification) {
            null -> {
                //Change
                uiError.value = UIError(R.string.error_something_wrong)
            }
            else -> {
                this.notificationList.value = listNotification
            }
        }
    }
}