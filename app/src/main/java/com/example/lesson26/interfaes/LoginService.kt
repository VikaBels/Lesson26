package com.example.lesson26.interfaes

import com.example.lesson26.models.*
import com.example.lesson26.repositories.LoginRepository.Companion.LOGIN_URL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    
    @POST(LOGIN_URL)
    fun getLoginResponseBody(@Body loginRequestBody: LoginRequestBody): Call<LoginResponseBody>
}