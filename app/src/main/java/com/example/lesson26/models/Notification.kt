package com.example.lesson26.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Notification(
    val text: String,
    val time: Long,
):Parcelable