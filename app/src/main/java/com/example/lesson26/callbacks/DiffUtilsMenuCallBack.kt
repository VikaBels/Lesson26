package com.example.lesson26.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.lesson26.models.ItemMenu

class DiffUtilsMenuCallBack(
    private val oldList: List<ItemMenu>,
    private val newList: List<ItemMenu>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].drawableId == newList[newItemPosition].drawableId
    }
}