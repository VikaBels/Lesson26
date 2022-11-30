package com.example.lesson26.repositories

import bolts.Task
import bolts.TaskCompletionSource
import com.example.lesson26.BASE_URL
import com.example.lesson26.interfaes.TrackService
import com.example.lesson26.models.TracksRequestBody
import com.example.lesson26.models.TracksResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListTrackRepository {
    companion object {
        const val TRACK_URL = "senla-training-addition/lesson-26.php?method=tracks"
    }

    fun getTracks(
        token: String
    ): Task<TracksResponseBody>? {
        return startRequestTracks(token).task
    }

    private fun startRequestTracks(
        token: String,
    ): TaskCompletionSource<TracksResponseBody> {
        val retrofitTrackList = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceInstanceTrackList = retrofitTrackList.create(TrackService::class.java)

        val requestBody = TracksRequestBody(
            token = token
        )

        return getCompletableTracks(serviceInstanceTrackList, requestBody)
    }

    private fun getCompletableTracks(
        serviceInstance: TrackService,
        requestBody: TracksRequestBody
    ): TaskCompletionSource<TracksResponseBody> {
        val completable = TaskCompletionSource<TracksResponseBody>()

        serviceInstance
            .getTrackResponseBody(requestBody)
            .enqueue(object : Callback<TracksResponseBody> {
                override fun onResponse(
                    call: Call<TracksResponseBody>,
                    response: Response<TracksResponseBody>
                ) {
                    val body = response.body()

                    completable.setResult(body)
                }

                override fun onFailure(call: Call<TracksResponseBody>, t: Throwable) {
                    completable.setError(t as Exception?)
                }
            })
        return completable
    }
}