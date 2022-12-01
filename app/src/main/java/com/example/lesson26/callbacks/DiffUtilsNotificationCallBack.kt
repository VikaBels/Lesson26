package com.example.lesson26.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.lesson26.models.Notification

class DiffUtilsNotificationCallBack(
    private val oldList: List<Notification>,
    private val newList: List<Notification>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].time == newList[newItemPosition].time
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].text == newList[newItemPosition].text
    }
}