package com.example.lesson26.interfaes

import com.example.lesson26.models.TracksRequestBody
import com.example.lesson26.models.TracksResponseBody
import com.example.lesson26.repositories.ListTrackRepository.Companion.TRACK_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TrackService {

    @POST(TRACK_URL)
    fun getTrackResponseBody(@Body trackRequestBody: TracksRequestBody): Call<TracksResponseBody>
}