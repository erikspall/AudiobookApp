package de.erikspall.audiobookapp.domain.use_case.playback

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.SystemClock
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.domain.const.PlaybackService
import de.erikspall.audiobookapp.domain.services.playback.background.PlayerService
import de.erikspall.audiobookapp.domain.util.Conversion
import de.erikspall.audiobookapp.ui.now_playing.event.NowPlayingEvent
import java.lang.reflect.Constructor
import javax.inject.Inject


class SleepTimer(
    private val context: Context,
    private val  sharedPref: SharedPreferences
) {

    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    @SuppressLint("ObsoleteSdkInt")
    fun set(time: Long) {
        Log.d("SleepTimer", "Set sleep timer for ${Conversion.millisToExtendedStr(time)}")

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= 31) {
            hasPermission = am.canScheduleExactAlarms()
            Log.d("AlarmManagerAudiobook", "$hasPermission")
        }

       /* val sharedPreferences = context.getSharedPreferences(
            "de.erikspall.audiobookapp.SLEEPTIMER_INTENT",
            Context.MODE_PRIVATE
        ) ?: return

        with (sharedPreferences.edit()) {
            putInt()
        }*/
        //Log.d("SleepTimer", "Setting SleepTimer ...")

        with (sharedPref.edit()) {
            android.util.Log.d("SharedPreferences", "Setting sleepTimerIsSet to true")
            putBoolean(context.getString(R.string.sleep_timer_is_set_shared_pref_key), true)
            commit()
        }

        if (hasPermission)
            am.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time,
                PendingIntent.getService(
                    context,
                    0,
                    Intent(
                        context,
                        PlayerService::class.java
                    ).setAction(PlaybackService.ACTION_QUIT),
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        else
            am.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time,
                PendingIntent.getService(
                    context, 0,
                    Intent(
                        context,
                        PlayerService::class.java
                    ).setAction(PlaybackService.ACTION_QUIT),
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )


    }

    fun cancel() {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        onEnd()
        Log.d("SleepTimer", "Canceling or Ending ...")
        am.cancel(
            PendingIntent.getService(
                context, 0,
                Intent(
                    context,
                    PlayerService::class.java
                ).setAction(PlaybackService.ACTION_QUIT),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            ) ?: return
        )
    }

    fun onEnd() {
        with (sharedPref.edit()) {
            putBoolean(context.getString(R.string.sleep_timer_is_set_shared_pref_key), false)
            commit()
        }
    }

    fun registerOnChangeListener(
        listener: (SharedPreferences, String) -> Unit
    ) {
        if (this.listener == null){
            this.listener = SharedPreferences.OnSharedPreferenceChangeListener(listener)

            Log.d("SleepTimer", "Registering Listener ...")
            sharedPref.registerOnSharedPreferenceChangeListener(this.listener)
        } else {
            Log.d("SleepTimer", "There is already a listener!")
        }

    }

    fun unregisterLastListener() {
        Log.d("SleepTimer", "Unregistering Listener ...")
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
        listener = null
    }

    private fun unregisterOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        Log.d("SleepTimer", "Unregistering Listener ...")
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun isScheduled(): Boolean {
        return sharedPref.getBoolean(context.getString(R.string.sleep_timer_is_set_shared_pref_key), false)
    }

    fun getSharedPrefName(): String {
        return context.getString(R.string.sleep_timer_shared_pref_name)
    }

    fun getSharedPrefKey(): String {
        return context.getString(R.string.sleep_timer_is_set_shared_pref_key)
    }
}