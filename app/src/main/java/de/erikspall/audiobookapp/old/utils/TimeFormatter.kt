package de.erikspall.audiobookapp.old.utils

import com.google.android.material.slider.LabelFormatter
import de.erikspall.audiobookapp.domain.util.Conversion

class TimeFormatter : LabelFormatter {
    override fun getFormattedValue(value: Float): String {
        return Conversion.millisToStr(value.toLong())
    }


}