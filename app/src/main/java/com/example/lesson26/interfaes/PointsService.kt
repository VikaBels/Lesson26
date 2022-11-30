package com.example.lesson26.interfaes

import com.example.lesson26.models.PointsRequestBody
import com.example.lesson26.models.PointsResponseBody
import com.example.lesson26.repositories.TrackRepository.Companion.POINTS_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PointsService {

    @POST(POINTS_URL)
    fun getPointsResponseBody(@Body pointsRequestBody: PointsRequestBody): Call<PointsResponseBody>
}