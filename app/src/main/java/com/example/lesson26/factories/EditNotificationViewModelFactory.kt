package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.viewmodels.EditNotificationViewModel

class EditNotificationViewModelFactory(
    private val dataRepository: DataRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == EditNotificationViewModel::class.java) {
            return EditNotificationViewModel(dataRepository) as T
        }
        return super.create(modelClass)
    }
}