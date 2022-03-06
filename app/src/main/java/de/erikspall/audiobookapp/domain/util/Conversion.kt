package de.erikspall.audiobookapp.domain.util

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


    fun millisToExtendedStr(millis: Long): String =
        String.format(
            "%d hours and %d minutes",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millis)
            )
        ).removePrefix("0 hours and ")

    fun formatTimeToString(hours: Int, minutes: Int, is24HoursFormat: Boolean): String {
        var postfix = ""
        var amPM = ""
        var hours = hours
        if (!is24HoursFormat && hours > 12) {
            amPM = "pm"
        } else if (!is24HoursFormat) {
            amPM = "am"
        }
        if (amPM == "pm")
            hours -= 12
        return String.format(
            "%02d:%02d %s",
            hours,
            minutes,
            amPM
        )
            .trimEnd()
    }
    }
}