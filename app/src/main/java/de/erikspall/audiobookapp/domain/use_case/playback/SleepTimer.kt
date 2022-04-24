package de.erikspall.audiobookapp.domain.use_case.playback

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import de.erikspall.audiobookapp.domain.const.PlaybackService
import de.erikspall.audiobookapp.domain.services.playback.background.PlayerService
import de.erikspall.audiobookapp.domain.util.Conversion

class SleepTimer(
    private val context: Context
) {

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

    fun isScheduled(): Boolean {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return PendingIntent.getService(
            context, 0,
            Intent(
                context,
                PlayerService::class.java
            ).setAction(PlaybackService.ACTION_QUIT),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null
    }
}