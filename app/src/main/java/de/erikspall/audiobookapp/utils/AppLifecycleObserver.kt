package de.erikspall.audiobookapp.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.util.*

// https://stackoverflow.com/questions/4414171/how-to-detect-when-an-android-app-goes-to-the-background-and-come-back-to-the-fo
class AppLifecycleObserver(
    private val onAppGoesToBackground: () -> Unit = {},
    private val onAppEntersForeground: () -> Unit = {}
): LifecycleEventObserver {

    private val debounce = DebouncingTimer(timeout = 10)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        debounce.refresh {
            when (event.targetState) {
                Lifecycle.State.CREATED -> onAppGoesToBackground()
                Lifecycle.State.RESUMED -> onAppEntersForeground()
                else -> Unit
            }
        }
    }

    fun attach() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun detach() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    private class DebouncingTimer(private val timeout: Long) {

        private var timer: Timer? = null

        fun refresh(job: () -> Unit) {
            timer?.cancel()
            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() = job.invoke()
            }, timeout)
        }
    }
}