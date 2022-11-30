package com.example.lesson26.tasks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//rename!!!!!
@Parcelize
class TimeHelper(
    val timeString: String,
    val time: Double
):Parcelable