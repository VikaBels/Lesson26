package com.example.lesson26.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson26.models.TrackInfo
import com.example.lesson26.repositories.DataRepository
import com.example.lesson26.viewmodels.TrackViewModel

class TrackViewModelFactory(
    private val trackInfo: TrackInfo?,
    private val dataRepository: DataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == TrackViewModel::class.java) {
            return TrackViewModel(trackInfo,dataRepository) as T
        }
        return super.create(modelClass)
    }
}