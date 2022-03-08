package de.erikspall.audiobookapp.domain.services.playback.background

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.Intent.CATEGORY_LAUNCHER
import android.net.Uri
import android.os.Build
import androidx.core.animation.doOnEnd
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.MainActivity
import de.erikspall.audiobookapp.data.data_source.local.player_controller.MediaItemTree
import de.erikspall.audiobookapp.domain.const.PlaybackService.ACTION_QUIT
import de.erikspall.audiobookapp.domain.services.playback.background.callbacks.CustomMediaLibrarySessionCallback
import de.erikspall.audiobookapp.domain.services.playback.background.filler.CustomMediaItemFiller
import de.erikspall.audiobookapp.domain.use_case.audiobook.AudiobookUseCases
import de.erikspall.audiobookapp.domain.use_case.playback.PlaybackUseCases
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : MediaLibraryService() {
    @Inject
    lateinit var audiobookUseCases: AudiobookUseCases

    @Inject
    lateinit var playbackUseCases: PlaybackUseCases


    private lateinit var player: ExoPlayer
    private lateinit var mediaLibrarySession: MediaLibrarySession


    companion object {
        const val SEARCH_QUERY_PREFIX_COMPAT = "androidx://media3-session/playFromSearch"
        const val SEARCH_QUERY_PREFIX = "androidx://media3-session/setMediaUri"
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }


    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
    }

    override fun onDestroy() {
        player.release()
        mediaLibrarySession.release()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                ACTION_QUIT -> {
                    MainScope().launch {
                        audiobookUseCases.set(
                            (playbackUseCases.getCurrent.mediaMetaData().mediaUri
                                ?: Uri.EMPTY).toString(),
                            playbackUseCases.getCurrent.positionInBook(player)
                        )
                    }
                    val fadeOut = ValueAnimator.ofFloat(1f, 0f)
                    fadeOut.duration = 10000
                    fadeOut.addUpdateListener {
                        player.volume = it.animatedValue as Float
                    }
                    fadeOut.doOnEnd {
                        player.pause()
                        player.volume = 1f
                        stopSelf()
                    }
                    fadeOut.start()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializeSessionAndPlayer() {
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()

        playbackUseCases.initizialize.mediaItemTree(audiobookUseCases.getBooksWithInfo)

        val librarySessionCallback = CustomMediaLibrarySessionCallback(
            ::setMediaItemFromSearchQuery,
            audiobookUseCases,
            playbackUseCases
        )

        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, librarySessionCallback)
                .setMediaItemFiller(CustomMediaItemFiller())
                .setSessionActivity(buildIntent())
                .build()
    }

    private fun setMediaItemFromSearchQuery(query: String) {
        // Only accept query with pattern "play [Title]" or "[Title]"
        // Where [Title]: must be exactly matched
        // If no media with exact name found, play a random media instead
        val mediaTitle =
            if (query.startsWith("play ", ignoreCase = true)) {
                query.drop(5)
            } else {
                query
            }

        val item = MediaItemTree.geBookFromTitle(mediaTitle) ?: MediaItemTree.getRandomItem()
        player.setMediaItem(item)
        player.prepare()
    }

    private fun buildIntent(): PendingIntent {
        val parentScreenIntent = Intent(this, MainActivity::class.java)
        parentScreenIntent.action = ACTION_MAIN
        parentScreenIntent.addCategory(CATEGORY_LAUNCHER)
        //parentScreenIntent.flags = FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP
        val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
        return PendingIntent.getActivity(this, 0, parentScreenIntent, immutableFlag)
    }


}