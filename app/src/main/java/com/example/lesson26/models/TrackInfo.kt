package com.example.lesson26.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TrackInfo(
    val track: Track,
    val token: String
):Parcelable