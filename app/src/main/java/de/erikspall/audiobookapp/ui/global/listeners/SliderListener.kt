package de.erikspall.audiobookapp.ui.global.listeners

import android.annotation.SuppressLint
import com.google.android.material.slider.Slider

class SliderListener(
    private val onStart: () -> Unit,
    private val onEnd: (Long) -> Unit
): Slider.OnSliderTouchListener {
    @SuppressLint("RestrictedApi")
    override fun onStartTrackingTouch(slider: Slider) {
        onStart()
    }

    @SuppressLint("RestrictedApi")
    override fun onStopTrackingTouch(slider: Slider) {
        onEnd(slider.value.toLong())
    }
}