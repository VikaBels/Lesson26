package com.example.lesson26.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.R
import com.example.lesson26.models.TrackInfo
import com.example.lesson26.models.Point
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.repositories.TrackRepository
import com.example.lesson26.utils.getIdError
import com.example.lesson26.utils.isOnline

class TrackViewModel(
    private val trackInfo: TrackInfo?,
    private val dataRepository: DataRepository
) : BaseViewModel() {
    private val listPoint = MutableLiveData<List<Point>>()
    private val uiError = MutableLiveData<UIError>()

    val error: LiveData<UIError>
        get() = uiError

    val points: LiveData<List<Point>>
        get() = listPoint

    init {
        startTrackTask()
    }

    private fun startTrackTask() {
        if (trackInfo != null) {

            if (isOnline()) {
                startServerRequest(trackInfo)
            } else {
                startDataBaseRequest(trackInfo)
            }

        }else{
            //Change
            uiError.value = UIError(R.string.error_something_wrong)
        }
    }

    private fun startDataBaseRequest(trackInfo: TrackInfo) {
        dataRepository.getAllPointsTask(
            getToken(),
            trackInfo.token,
            trackInfo.track.id.toLong()
        ).continueWith({

            if (it.result != null) {
                this.listPoint.value = it.result
            }
            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun startServerRequest(trackInfo: TrackInfo) {
        val trackRepository = TrackRepository().getPoints(trackInfo)

        trackRepository?.continueWith({

            if (it.result != null) {
                checkServerResponsePoints(it.result.points)
            }
            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR, getToken())


    }

    private fun checkServerResponsePoints(listPoints: List<Point>?) {
        when (listPoints) {
            null -> {
                //change
                uiError.value = UIError(R.string.error_invalid_data_login)
            }
            else -> {
                this.listPoint.value = listPoints
            }
        }
    }
}