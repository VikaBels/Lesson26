package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.viewmodels.JoggingViewModel

class JoggingViewModelFactory(
    private val dataRepository: DataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == JoggingViewModel::class.java) {
            return JoggingViewModel(dataRepository) as T
        }
        return super.create(modelClass)
    }
}