package de.erikspall.audiobookapp.domain.util

import com.google.android.material.slider.LabelFormatter

class TimeFormatter : LabelFormatter {
    override fun getFormattedValue(value: Float): String {
        return Conversion.millisToStr(value.toLong())
    }


}