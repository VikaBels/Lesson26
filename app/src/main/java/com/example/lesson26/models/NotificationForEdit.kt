package com.example.lesson26.models

import android.os.Parcelable
import com.example.lesson26.models.Notification
import kotlinx.parcelize.Parcelize

@Parcelize
class NotificationForEdit(
    val notification: Notification,
    val token: String?
): Parcelable