package de.erikspall.audiobookapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import de.erikspall.audiobookapp.const.Layout

class AppViewModel: ViewModel() {
    var layout = Layout.GRID
    var currentlyPlayingPosition = -1
    var previouslyPlayingId = -1

    override fun onCleared() {
        super.onCleared()
        Log.d("FragmentStuff", "AppViewModel destroyed!")
    }
}