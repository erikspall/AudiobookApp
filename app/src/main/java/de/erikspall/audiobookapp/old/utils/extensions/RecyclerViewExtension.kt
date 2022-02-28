package de.erikspall.audiobookapp.old.utils.extensions

import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView

// Source: https://stackoverflow.com/a/64351936 by Nicolas Duponchel
fun RecyclerView.runWhenReady(action: () -> Unit) {
    val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}