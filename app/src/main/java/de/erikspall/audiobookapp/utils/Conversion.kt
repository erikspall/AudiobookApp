package de.erikspall.audiobookapp.utils

import android.content.res.Resources
import java.util.concurrent.TimeUnit

class Conversion {
    companion object{
        fun pxToDp(pixel: Int): Int = (pixel / Resources.getSystem().displayMetrics.density).toInt()

        fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

        fun millisToStr(millis: Long): String =
            String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millis)
                ),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millis)
                )
            ).removePrefix("00:") //if hours = 0 remove
                .removePrefix("0") //remove first 0 => 2:11
    }
}