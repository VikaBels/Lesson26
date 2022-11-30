package com.example.lesson26.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson26.R
import com.example.lesson26.callbacks.DiffUtilsNotificationCallBack
import com.example.lesson26.databinding.ItemNotificationBinding
import com.example.lesson26.interfaes.NotificationListener
import com.example.lesson26.models.Notification
import com.example.lesson26.utils.getFormattedDate

class NotificationAdapter(
    private val notificationListener: NotificationListener,
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private var notificationList = listOf<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding, notificationListener)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notificationItem = notificationList[position]
        holder.bind(notificationItem)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    fun setListNotification(listNotification: List<Notification>) {
        val result = DiffUtil.calculateDiff(
            DiffUtilsNotificationCallBack(notificationList, listNotification), false
        )

        notificationList = listNotification
        result.dispatchUpdatesTo(this)
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding,
        private val notificationListener: NotificationListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val resources = binding.root.context.resources

        fun bind(notificationItem: Notification) {

            binding.textNotification.text = resources.getString(
                R.string.notification_text,
                notificationItem.text
            )
            binding.time.text = resources.getString(
                R.string.time_for_run,
                getFormattedDate(notificationItem.time)
            )

            binding.imageBtnDelete.setOnClickListener {
                notificationListener.onBtnDeleteClick(notificationItem)
            }
            binding.oneNotification.setOnClickListener {
                notificationListener.onNotificationClick(notificationItem)
            }
        }
    }
}