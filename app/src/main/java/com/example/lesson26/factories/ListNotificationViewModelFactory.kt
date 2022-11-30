package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.viewmodels.ListNotificationViewModel

class ListNotificationViewModelFactory(
    private val dataRepository: DataRepository,
    private val token: String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == ListNotificationViewModel::class.java) {
            return ListNotificationViewModel(dataRepository, token) as T
        }
        return super.create(modelClass)
    }
}