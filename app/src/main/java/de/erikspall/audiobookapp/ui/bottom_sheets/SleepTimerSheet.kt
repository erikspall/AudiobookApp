package de.erikspall.audiobookapp.ui.bottom_sheets

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.ModalBottomSheetSleepTimerBinding
import de.erikspall.audiobookapp.utils.Conversion
import java.util.*
import kotlin.math.abs

class SleepTimerSheet(
    private val onSetCallback: (Long) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetSleepTimerBinding? = null
    private val binding get() = _binding!!
    private val STOPPING_IN = 1
    private val STOPPING_AT = 2
    private var currentState = STOPPING_IN
    private var defaultTextColor: Int? = null

    //inflater.inflate(R.layout.modal_bottom_sheet_sleep_timer, container, false)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ModalBottomSheetSleepTimerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        defaultTextColor = binding.stopInfoText.currentTextColor

        binding.stopInButton.setOnClickListener {
            currentState = STOPPING_IN
            updateState()
        }
        binding.stopAtButton.setOnClickListener {
            currentState = STOPPING_AT
            updateState()
        }

        binding.picker.setOnTimeChangedListener { view, hourOfDay, minute ->

            if (currentState == STOPPING_AT) {
                setInfoStoppingAtText(hourOfDay, minute)
            } else {
                setInfoStoppingInText(hourOfDay, minute)
            }
        }

        binding.cancelButton.setOnClickListener {
            this.dismiss()
        }

        binding.setButton.setOnClickListener {
            val time = getTimerTimeInMs()
            //If sleep time is under one minute
            if (time <= 59999) {
                /* binding.stopInfoText.setCompoundDrawablesWithIntrinsicBounds(
                     ContextCompat.getDrawable(
                         requireContext(),
                         R.drawable.ic_round_error_outline
                     ).setDrawableColor(Color.RED), null, null, null
                 )*/
                binding.stopInfoText.setTextColor(Color.RED) //TODO: Use dynamic ERROR color
                binding.stopInfoText.text = getString(R.string.sleep_timer_error)
            } else {
                onSetCallback(getTimerTimeInMs())
                dismiss()
            }
        }

        updateState()
    }

    private fun updateState() {
        if (currentState == STOPPING_AT) {
            //TODO: Set currrent hours and minutes
            set24hIfApplicable()
            binding.picker.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            binding.picker.minute = Calendar.getInstance().get(Calendar.MINUTE)
            setInfoStoppingAtText(binding.picker.hour, binding.picker.minute)
        } else {
            binding.picker.setIs24HourView(true) //force
            binding.picker.hour = 0
            binding.picker.minute = 15
            setInfoStoppingInText(binding.picker.hour, binding.picker.minute)
        }
    }

    private fun set24hIfApplicable() {
        if (DateFormat.is24HourFormat(context))
            binding.picker.setIs24HourView(true)
        else
            binding.picker.setIs24HourView(false)
    }

    private fun setInfoStoppingAtText(hourOfDay: Int, minute: Int) {
        if (binding.stopInfoText.currentTextColor != defaultTextColor) {
            binding.stopInfoText.setTextColor(defaultTextColor!!)
            /*binding.stopInfoText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null
            )*/
        }

        binding.stopInfoText.text = context?.getString(
            R.string.playback_stop_info_at,
            Conversion.formatTimeToString(hourOfDay, minute, DateFormat.is24HourFormat(context))
        )

    }

    private fun setInfoStoppingInText(hours: Int, minute: Int) {
        if (binding.stopInfoText.currentTextColor != defaultTextColor) {
            binding.stopInfoText.setTextColor(defaultTextColor!!)
            /*binding.stopInfoText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, null, null
            )*/
        }
        binding.stopInfoText.text = if (hours != 0) context?.getString(
            R.string.playback_stop_info_in,
            hours,
            minute
        ) else context?.getString(
            R.string.playback_stop_info_in_no_hours,
            minute
        )
    }

    private fun getTimerTimeInMs(): Long {
        if (currentState == STOPPING_AT) {
            val hour = binding.picker.hour
            val minute = binding.picker.minute
            // Calc delta between time in picker and now
            var hourDelta = abs(hour - Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            // Only get abs value if it is not the same hour
            val minuteDelta = if (hourDelta == 0 || hourDelta == 12)
                minute - Calendar.getInstance().get(Calendar.MINUTE)
            else
                abs(minute - Calendar.getInstance().get(Calendar.MINUTE))

            if (hourDelta == 0) {
                if (minuteDelta == 0) {
                    return -1
                } else if (minuteDelta < 0) {
                    hourDelta = 24
                }
            }
            return ((hourDelta * 60L) + minuteDelta) * 60L * 1000L

        } else {
            // Simple
            val hour = binding.picker.hour
            val minute = binding.picker.minute
            return ((hour * 60L) + minute) * 60L * 1000L
        }
    }

    companion object {
        const val TAG = "SleepTimerSheet"
    }
}