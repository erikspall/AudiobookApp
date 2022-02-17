package de.erikspall.audiobookapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import de.erikspall.audiobookapp.const.Layout

class AppViewModel: ViewModel() {
    var layout = Layout.GRID

    override fun onCleared() {
        super.onCleared()
        Log.d("FragmentStuff", "AppViewModel destroyed!")
    }
}