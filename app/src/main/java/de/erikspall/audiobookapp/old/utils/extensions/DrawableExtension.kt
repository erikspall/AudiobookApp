package de.erikspall.audiobookapp.old.utils.extensions

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.drawable.Drawable


    fun Drawable?.setDrawableColor(color: Int) :Drawable? {
        return when (this) {
            null -> null
            else -> {
                this.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
                this
            }
        }
    }
