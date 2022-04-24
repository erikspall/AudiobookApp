package de.erikspall.audiobookapp.domain.util

import android.content.Context
import android.content.res.TypedArray
import com.google.android.material.color.DynamicColors
import de.erikspall.audiobookapp.R

object ColorExtractor {
    fun getPrimaryColor(context : Context): Int {
        val dynamicColors = DynamicColors.wrapContextIfAvailable(context, R.style.ThemeOverlay_Material3_DynamicColors_DayNight)
        val attrsToResolve = IntArray(1)
        attrsToResolve[0] = R.attr.colorPrimary
        val typedArray = dynamicColors.obtainStyledAttributes(attrsToResolve)
        val primary = typedArray.getColor(0, 0)
        typedArray.recycle()
        return primary
    }
}