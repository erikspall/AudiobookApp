package de.erikspall.audiobookapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import de.erikspall.audiobookapp.const.Layout

class AppViewModel: ViewModel() {
    var layout = Layout.GRID

    /** Stuff for playback **/
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null
    /** ~~~~~~~~~~~~~~~~~~ **/

    init {
        Log.d("FragmentStuff", "AppViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("FragmentStuff", "AppViewModel destroyed!")
    }

    /** Stuff for playback **/

    /** ~~~~~~~~~~~~~~~~~~ **/
}