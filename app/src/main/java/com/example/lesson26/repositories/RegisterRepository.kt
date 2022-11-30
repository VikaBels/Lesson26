package com.example.lesson26.repositories

import bolts.Task
import bolts.TaskCompletionSource
import com.example.lesson26.BASE_URL
import com.example.lesson26.interfaes.RegisterService
import com.example.lesson26.models.RegisterRequestBody
import com.example.lesson26.models.RegisterResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterRepository {
    companion object {
        const val REGISTER_URL = "senla-training-addition/lesson-26.php?method=register"
    }

    //rename
    fun getRegisterResponse(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ): Task<RegisterResponseBody>? {
        return startRequestRegister(email, firstName, lastName, password).task
    }

    private fun startRequestRegister(
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ): TaskCompletionSource<RegisterResponseBody> {
        val retrofitRegister = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceInstanceRegister = retrofitRegister.create(RegisterService::class.java)

        val requestBody = RegisterRequestBody(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password
        )

        return getCompletable(serviceInstanceRegister, requestBody)
    }

    private fun getCompletable(
        serviceInstance: RegisterService,
        requestBody: RegisterRequestBody
    ): TaskCompletionSource<RegisterResponseBody> {
        val completable = TaskCompletionSource<RegisterResponseBody>()

        serviceInstance
            .getRegisterResponseBody(requestBody)
            .enqueue(object : Callback<RegisterResponseBody> {
                override fun onResponse(
                    call: Call<RegisterResponseBody>,
                    response: Response<RegisterResponseBody>
                ) {
                    val body = response.body()
                    
                    completable.setResult(body)
                }

                override fun onFailure(call: Call<RegisterResponseBody>, t: Throwable) {
                    completable.setError(t as Exception?)
                }
            })
        return completable
    }
}