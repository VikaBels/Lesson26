package com.example.lesson26.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Track(
    val id: Int,
    val beginsAt: Long,
    val time: Double,
    val distance: Long
) : Parcelable