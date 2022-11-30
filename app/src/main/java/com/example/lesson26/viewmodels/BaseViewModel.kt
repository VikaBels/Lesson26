package com.example.lesson26.viewmodels

import androidx.lifecycle.ViewModel
import bolts.CancellationToken
import bolts.CancellationTokenSource

open class BaseViewModel : ViewModel() {
    private val cancellationTokenSource = CancellationTokenSource()

    override fun onCleared() {
        super.onCleared()
        cancellationTokenSource.cancel()
    }

    fun getToken(): CancellationToken {
        return cancellationTokenSource.token
    }
}