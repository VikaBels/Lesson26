package com.example.lesson26.interfaes

import com.example.lesson26.models.RegisterRequestBody
import com.example.lesson26.models.RegisterResponseBody
import com.example.lesson26.repositories.RegisterRepository.Companion.REGISTER_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {

    @POST(REGISTER_URL)
    fun getRegisterResponseBody(@Body registerRequestBody: RegisterRequestBody): Call<RegisterResponseBody>
}