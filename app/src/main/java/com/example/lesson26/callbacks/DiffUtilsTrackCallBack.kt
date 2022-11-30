package com.example.lesson26.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.lesson26.models.Track

class DiffUtilsTrackCallBack(
    private val oldList: List<Track>,
    private val newList: List<Track>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].beginsAt == newList[newItemPosition].beginsAt
                && oldList[oldItemPosition].time == newList[newItemPosition].time
                && oldList[oldItemPosition].distance == newList[newItemPosition].distance
    }
}