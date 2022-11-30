package com.example.lesson26.interfaes

import com.example.lesson26.models.SaveRequestBody
import com.example.lesson26.models.SaveResponseBody
import com.example.lesson26.repositories.SaveTrackRepository.Companion.SAVE_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SaveTrackService {

    @POST(SAVE_URL)
    fun saveTrackResponseBody(@Body trackRequestBody: SaveRequestBody): Call<SaveResponseBody>
}