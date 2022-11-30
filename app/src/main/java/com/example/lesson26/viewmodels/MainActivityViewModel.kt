package com.example.lesson26.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson26.R
import com.example.lesson26.models.ItemMenu

class MainActivityViewModel : ViewModel() {
    private var listMenu = MutableLiveData<List<ItemMenu>>()

    val currentListMenu: LiveData<List<ItemMenu>>
        get() = listMenu

    init {
        fillingMenu()
    }

    private fun fillingMenu() {
        listMenu.value = listOf(
            ItemMenu(
                R.string.home_page,
                R.drawable.ic_baseline_home_24
            ),
            ItemMenu(
                R.string.list_notification_page,
                R.drawable.ic_baseline_notifications_active_24
            ),
            ItemMenu(
                R.string.exit_app,
                R.drawable.ic_baseline_exit_to_app_24
            )
        )
    }
}