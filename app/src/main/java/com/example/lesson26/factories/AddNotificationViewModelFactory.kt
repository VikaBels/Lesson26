package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.viewmodels.AddNotificationViewModel

class AddNotificationViewModelFactory(
    private val dataRepository: DataRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == AddNotificationViewModel::class.java) {
            return AddNotificationViewModel(dataRepository) as T
        }
        return super.create(modelClass)
    }
}