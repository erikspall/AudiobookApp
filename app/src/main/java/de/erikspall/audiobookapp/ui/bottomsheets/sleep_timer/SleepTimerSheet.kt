package de.erikspall.audiobookapp.ui.bottomsheets.sleep_timer

import android.content.res.Resources
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TimePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.ModalBottomSheetSleepTimerBinding
import de.erikspall.audiobookapp.domain.use_case.playback.PlaybackUseCases
import de.erikspall.audiobookapp.domain.util.Conversion
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SleepTimerSheet() : BottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetSleepTimerBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var playbackUseCases: PlaybackUseCases

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ModalBottomSheetSleepTimerBinding.inflate(inflater, container, false)

        binding.picker.setIs24HourView(true) // We don't want to display actual time
        binding.picker.hour = 0
        setupListeners() // Setup listener between so it only gets called once
        binding.picker.minute = 3 // *5 = 15 minutes
        setTimePickerInterval(binding.picker, 5)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupListeners() {
        binding.cancelButton.setOnClickListener {
            this.dismiss()
        }
        binding.picker.setOnTimeChangedListener { view, hourOfDay, minute ->
            val calendar: Calendar = Calendar.getInstance()
            var newMinute = minute*5 + calendar.get(Calendar.MINUTE)
            var hour = hourOfDay + calendar.get(Calendar.HOUR_OF_DAY)
            if (newMinute >= 60) {
                newMinute -= 60
                hour += 1
            }
            if (hour >= 24)
                hour -= 24
            binding.stopInfoText.text = requireContext().getString(
                R.string.playback_stop_info_at,
                Conversion.formatTimeToString(
                    hour,
                    newMinute,
                    DateFormat.is24HourFormat(requireContext())
                )
            )
        }
        binding.setButton.setOnClickListener {
            if (binding.picker.hour == 0 && binding.picker.minute == 0) {
                Log.d("SleepTimer", "Cannot set sleept timer for 0 minutes setting for 30s instead")
                playbackUseCases.sleepTimer.set(
                    5000L
                )
            } else
                playbackUseCases.sleepTimer.set(
                    ((binding.picker.hour * 60L) + binding.picker.minute*5) * 60L * 1000L
                )
            this.dismiss()
        }
    }

    /**
     * Set TimePicker interval by adding a custom minutes list
     *
     * @param timePicker
     */
    private fun setTimePickerInterval(timePicker: TimePicker, interval: Int) {
        try {
            val minutePicker = timePicker.findViewById(
                Resources.getSystem().getIdentifier(
                    "minute", "id", "android"
                )
            ) as NumberPicker
            minutePicker.minValue = 0
            minutePicker.maxValue = 60 / interval - 1
            val displayedValues: MutableList<String> = ArrayList()
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += interval
            }
            minutePicker.displayedValues = displayedValues.toTypedArray()
        } catch (e: Exception) {
            Log.e("SleepTimerSheet", "Exception: $e")
        }
    }


}