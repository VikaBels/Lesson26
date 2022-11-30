package com.example.lesson26.repositories

import com.example.lesson26.interfaes.PointsService

import bolts.Task
import bolts.TaskCompletionSource
import com.example.lesson26.BASE_URL
import com.example.lesson26.models.TrackInfo
import com.example.lesson26.models.PointsRequestBody
import com.example.lesson26.models.PointsResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackRepository {
    companion object {
        const val POINTS_URL = "senla-training-addition/lesson-26.php?method=points"
    }

    fun getPoints(
        trackInfo: TrackInfo
    ): Task<PointsResponseBody>? {
        return startRequestPoints(trackInfo).task
    }

    private fun startRequestPoints(
        trackInfo: TrackInfo,
    ): TaskCompletionSource<PointsResponseBody> {
        val retrofitPoints = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceInstancePoints = retrofitPoints.create(PointsService::class.java)

        val requestBody = PointsRequestBody(
            token = trackInfo.token,
            id = trackInfo.track.id
        )

        return getCompletable(serviceInstancePoints, requestBody)
    }

    private fun getCompletable(
        serviceInstance: PointsService,
        requestBody: PointsRequestBody?
    ): TaskCompletionSource<PointsResponseBody> {
        val completable = TaskCompletionSource<PointsResponseBody>()

        if (requestBody != null) {
            serviceInstance
                .getPointsResponseBody(requestBody)
                .enqueue(object : Callback<PointsResponseBody> {
                    override fun onResponse(
                        call: Call<PointsResponseBody>,
                        response: Response<PointsResponseBody>
                    ) {
                        val body = response.body()

                        completable.setResult(body)
                    }

                    override fun onFailure(call: Call<PointsResponseBody>, t: Throwable) {
                        completable.setError(t as Exception?)
                    }
                })
        }
        return completable
    }
}