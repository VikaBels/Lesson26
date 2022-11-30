package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.viewmodels.MainActivityViewModel

class MainActivityViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == MainActivityViewModel::class.java) {
            return MainActivityViewModel() as T
        }
        return super.create(modelClass)
    }
}