package com.example.lesson26.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.R
import com.example.lesson26.models.Track
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.repositories.ListTrackRepository
import com.example.lesson26.utils.getIdError
import com.example.lesson26.utils.isOnline

class TrackListViewModel(
    private val token: String?,
    private val dataRepository: DataRepository
) : BaseViewModel() {
    private var uiError = MutableLiveData<UIError>()
    private val trackList = MutableLiveData<List<Track>>()

    val error: LiveData<UIError>
        get() = uiError

    val listTrack: LiveData<List<Track>>
        get() = trackList

    init {
        getAllTracks()
    }

    private fun getAllTracks() {
        if (token != null) {

            if (isOnline()) {
                startServerRequest(token)
            }else{
                startDataBaseRequest(token)
            }

        } else {
            //Change
            uiError.value = UIError(R.string.error_something_wrong)
        }
    }

    private fun startDataBaseRequest(token:String){
        dataRepository.getAllTrackTask(
            getToken(),
            token
        ).continueWith({

            if(it.result != null){
                this.trackList.value = it.result
            }
            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR)

    }

    private fun startServerRequest(token: String){
        val loginRepository = ListTrackRepository().getTracks(token)

        loginRepository?.continueWith({

            if (it.result != null) {
                checkServerResponseListTrack(it.result.tracks)
            }
            if (it.error != null) {
                uiError.value = UIError(getIdError(it.error))
            }

        }, Task.UI_THREAD_EXECUTOR, getToken())
    }

    private fun checkServerResponseListTrack(listTrack: List<Track>?) {
        when (listTrack) {
            null -> {
                //Change
                uiError.value = UIError(R.string.error_something_wrong)
            }
            else -> {
                this.trackList.value = listTrack.sortedByDescending { it.beginsAt }
            }
        }
    }
}