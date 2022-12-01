package com.example.lesson26.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TwoTypesTime(
    val timeString: String,
    val time: Double
) : Parcelable