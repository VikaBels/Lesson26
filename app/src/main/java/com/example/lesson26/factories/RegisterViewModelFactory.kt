package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.viewmodels.RegisterViewModel

class RegisterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == RegisterViewModel::class.java) {
            return RegisterViewModel() as T
        }
        return super.create(modelClass)
    }
}