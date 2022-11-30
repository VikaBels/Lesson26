package com.example.lesson26.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bolts.Task
import com.example.lesson26.fragments.JoggingFragment.Companion.POINT_LIST_QUANTITY
import com.example.lesson26.models.Point
import com.example.lesson26.models.UIError
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.repositories.SaveTrackRepository
import com.example.lesson26.utils.getIdError
import com.example.lesson26.utils.isOnline
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt
import com.google.android.gms.tasks.CancellationToken as CancellationTokenTask

class JoggingViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel() {
    companion object {
        const val LATITUDE_LENGTH = 111200000
    }

    private var uiError = MutableLiveData<UIError>()

    private val points = mutableListOf<Point>()
    private var distance: Long = 0L

    val listPoints: List<Point>
        get() = points.toList()

    val currentDistance: Long
        get() = distance

    val error: LiveData<UIError>
        get() = uiError


    fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient,
        context: Context,
        onComplete: (() -> Unit)? = null
    ) {
        val listPermission = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val isGranted = listPermission.any {
            ActivityCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (!isGranted) {
            return
        }

        fusedLocationClient.getCurrentLocation(
            CurrentLocationRequest.Builder().build(),
            object : CancellationTokenTask() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationTokenTask {
                    return CancellationTokenSource().token
                }

                override fun isCancellationRequested() = false
            }
        ).addOnSuccessListener { location ->
            val longitude = location.longitude
            val latitude = location.latitude

            points.add(
                Point(
                    longitude,
                    latitude
                )
            )

            checkListPoints()

        }.addOnFailureListener {
            uiError.value = UIError(getIdError(it))
        }.addOnCompleteListener {

            onComplete?.invoke()
        }
    }

    private fun checkListPoints() {
        if (points.size == POINT_LIST_QUANTITY) {

            val pointStart = points.first()
            val pointEnd = points.last()

            distance = calculateDistance(
                pointStart.lat,
                pointStart.lng,
                pointEnd.lat,
                pointEnd.lng
            ).toLong()
        }
    }

    private fun calculateDistance(
        latStart: Double,
        lngStart: Double,
        latEnd: Double,
        lngEnd: Double
    ): Double {
        return LATITUDE_LENGTH * sqrt(
            (lngStart - lngEnd) * (lngStart - lngEnd) + (latStart - latEnd) * cos(
                PI * lngStart
            ) * (latStart - latEnd) * cos(PI * lngStart)
        )
    }


    fun addNewTrack(
        token: String?,
        id: Int? = null,
        beginsAt: Long,
        time: Double,
        distance: Long,
        listPoints: List<Point>
    ) {
        addNewTrackDateBase(
            token,
            beginsAt,
            time,
            distance,
            listPoints
        )

        if (isOnline()) {
            addNewTrackService(
                token,
                id,
                beginsAt,
                time,
                distance,
                listPoints
            )
        }
    }

    private fun addNewTrackDateBase(
        token: String?,
        beginsAt: Long,
        time: Double,
        distance: Long,
        listPoints: List<Point>
    ) {
        if (token != null) {
            dataRepository.addNewTrackTask(
                getToken(),
                beginsAt,
                time,
                distance,
                token
            ).continueWith({

                if (it.error != null) {
                    uiError.value = UIError(getIdError(it.error))
                } else {
                    addNewPoints(
                        token,
                        listPoints
                    )
                }

            }, Task.UI_THREAD_EXECUTOR)
        }
    }

    private fun addNewTrackService(
        token: String?,
        id: Int? = null,
        beginsAt: Long,
        time: Double,
        distance: Long,
        listPoints: List<Point>
    ) {
        if (token != null) {
            val saveTrackRepository = SaveTrackRepository().getTrackSave(
                token,
                id,
                beginsAt,
                time.toLong(),
                distance,
                listPoints
            )

            saveTrackRepository?.continueWith({

                if (it.error != null) {
                    uiError.value = UIError(getIdError(it.error))
                }

            }, Task.UI_THREAD_EXECUTOR, getToken())
        }
    }

    private fun addNewPoints(
        token: String?,
        listPoints: List<Point>
    ) {
        if (token != null) {
            dataRepository.addNewPointsTask(
                getToken(),
                listPoints,
                token
            ).continueWith({

                if (it.error != null) {
                    uiError.value = UIError(getIdError(it.error))
                }

            }, Task.UI_THREAD_EXECUTOR)
        }
    }
}