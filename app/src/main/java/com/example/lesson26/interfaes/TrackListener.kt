package com.example.lesson26.interfaes

import com.example.lesson26.models.Track

interface TrackListener {

    fun onTrackClick(track: Track)
}