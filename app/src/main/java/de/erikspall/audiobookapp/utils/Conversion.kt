package de.erikspall.audiobookapp.utils

import android.content.res.Resources

class Conversion {
    companion object{
        fun pxToDp(pixel: Int): Int = (pixel / Resources.getSystem().displayMetrics.density).toInt()

        fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}