package com.example.lesson26.interfaes

import com.example.lesson26.models.Track

interface TracksScreenNavigationListener {

    fun showTrackFragment(track: Track)

    fun showJoggingActivity()
}