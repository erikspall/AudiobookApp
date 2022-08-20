package de.erikspall.audiobookapp.data.source.local.player_controller

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import de.erikspall.audiobookapp.domain.services.playback.background.PlayerService

class ControllerDataSource(
    private val context: Context,
    onControllerCreated: () -> Unit
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture!!.get() else null


    @SuppressLint("UnsafeOptInUsageError")
    val sessionToken =
        SessionToken(context, ComponentName(context, PlayerService::class.java))

    //init {
    //    initializeController(onControllerCreated)
    //}

    @SuppressLint("UnsafeOptInUsageError")
    fun initializeController(onControllerCreated: () -> Unit) {
        Log.d("ControllerDataSource", "Initialize that shit!")
        controllerFuture = MediaController.Builder(
            context,
            sessionToken
        ).buildAsync()

        controllerFuture!!.addListener(
            {
                onControllerCreated()
            }, MoreExecutors.directExecutor()
        )
    }

    fun releaseController() {
        Log.d("ControllerDataSource", "Release that shit!")
        MediaController.releaseFuture(controllerFuture!!)
        controller?.release()
    }
}