package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.viewmodels.LoginViewModel

class LoginViewModelFactory(
    private val dataRepository: DataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == LoginViewModel::class.java) {
            return LoginViewModel(dataRepository) as T
        }
        return super.create(modelClass)
    }
}