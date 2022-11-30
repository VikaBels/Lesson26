package com.example.lesson26.repositories

import bolts.Task
import bolts.TaskCompletionSource
import com.example.lesson26.BASE_URL
import com.example.lesson26.interfaes.SaveTrackService
import com.example.lesson26.models.Point
import com.example.lesson26.models.SaveRequestBody
import com.example.lesson26.models.SaveResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SaveTrackRepository {
    companion object {
        const val SAVE_URL = "senla-training-addition/lesson-26.php?method=save"
    }

    //rename
    fun getTrackSave(
        token: String,
        id: Int? = null,
        beginsAt: Long,
        time: Long,
        distance: Long,
        points: List<Point>
    ): Task<SaveResponseBody>? {
        return startRequestTrackSave(token, id, beginsAt, time, distance, points).task
    }

    private fun startRequestTrackSave(
        token: String,
        id: Int? = null,
        beginsAt: Long,
        time: Long,
        distance: Long,
        points: List<Point>
    ): TaskCompletionSource<SaveResponseBody> {
        val retrofitSaveTrack = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceInstanceSaveTrack = retrofitSaveTrack.create(SaveTrackService::class.java)

        val requestBody = SaveRequestBody(
            token,
            id,
            beginsAt,
            time,
            distance,
            points
        )

        return getCompletableTrackSave(serviceInstanceSaveTrack, requestBody)
    }

    private fun getCompletableTrackSave(
        serviceInstance: SaveTrackService,
        requestBody: SaveRequestBody
    ): TaskCompletionSource<SaveResponseBody> {
        val completable = TaskCompletionSource<SaveResponseBody>()

        serviceInstance
            .saveTrackResponseBody(requestBody)
            .enqueue(object : Callback<SaveResponseBody> {
                override fun onResponse(
                    call: Call<SaveResponseBody>,
                    response: Response<SaveResponseBody>
                ) {
                    val body = response.body()

                    completable.setResult(body)
                }

                override fun onFailure(call: Call<SaveResponseBody>, t: Throwable) {
                    completable.setError(t as Exception?)
                }
            })
        return completable
    }
}