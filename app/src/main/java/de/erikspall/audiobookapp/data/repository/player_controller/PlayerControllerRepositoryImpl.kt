package de.erikspall.audiobookapp.data.repository.player_controller

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import de.erikspall.audiobookapp.data.data_source.local.player_controller.ControllerDataSource
import de.erikspall.audiobookapp.data.data_source.local.player_controller.MediaItemTree
import de.erikspall.audiobookapp.data.data_source.local.player_controller.MediaItemTree.METADATA_KEY_DURATION
import de.erikspall.audiobookapp.data.data_source.local.player_controller.MediaItemTreeDataSource
import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository
import de.erikspall.audiobookapp.domain.use_case.audiobook.GetBooksWithInfo


class PlayerControllerRepositoryImpl (
    private val mediaItemTreeDataSource: MediaItemTreeDataSource,
    private val controllerDataSource: ControllerDataSource
) : PlayerControllerRepository {

    override fun getMediaTree(): MediaItemTree {
        return mediaItemTreeDataSource.getTree()
    }

    override fun updateMediaTree(books: List<AudiobookWithInfo>) {
        mediaItemTreeDataSource.updateTree(books)
    }

    override fun updateMediaTree(getBooksWithInfo: GetBooksWithInfo) {
        mediaItemTreeDataSource.updateTree(getBooksWithInfo)
    }


    override fun getController(): MediaController? {
        return controllerDataSource.controller
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun getCurrentMediaMetadata(): MediaMetadata {
        return getCurrentMediaItem().mediaMetadata
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun getCurrentMediaItem(): MediaItem {
        return controllerDataSource.controller?.currentMediaItem ?: MediaItem.EMPTY
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun getCurrentClippingConfig(): MediaItem.ClippingConfiguration {
        return getCurrentMediaItem().clippingConfiguration
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun getCurrentPositionInBook(): Long {
        return getCurrentPositionInChapter() + getCurrentClippingConfig().startPositionMs
    }

    override fun getCurrentPositionInChapter(): Long {
        return getController()?.currentPosition ?: 0
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun getEndPositionOfItemAt(index: Int): Long {
        return getController()?.getMediaItemAt(index)?.clippingConfiguration?.endPositionMs
            ?: throw IndexOutOfBoundsException("No media item at $index")
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun getStartPositionOfItemAt(index: Int): Long {
        return getController()?.getMediaItemAt(index)?.clippingConfiguration?.startPositionMs
            ?: throw IndexOutOfBoundsException("No media item at $index")
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun getCurrentChapterDuration(): Long {
        return getCurrentMediaMetadata().extras?.getLong(METADATA_KEY_DURATION) ?: 1
    }

    override fun releaseController() {
        controllerDataSource.releaseController()
    }

    override fun initController(onControllerCreated: () -> Unit) {
        controllerDataSource.initializeController(onControllerCreated)
    }

    override fun isPlaying(): Boolean {
        return getController()?.isPlaying ?: false
    }

    override fun play() {
        getController()?.play() ?: throw IllegalStateException("Controller has nothing to play!")
    }

    override fun pause() {
        getController()?.pause() ?: throw IllegalStateException("Controller has nothing to pause!")
    }

    override fun getChapterMediaItemsOf(bookId: Long): List<MediaItem> {
        return getMediaTree().getChapters(bookId) ?: emptyList()
    }

    override fun currentMediaItemCount(): Int {
        return getController()?.mediaItemCount ?: 0
    }

    override fun seekTo(chapterIndex: Int, position: Long) {
        getController()?.seekTo(chapterIndex, position) ?: return
    }

    override fun play(chapters: List<MediaItem>, fromStart: Boolean, lastPosition: Long) {
        getController()?.setMediaItems(chapters, fromStart)
        getController()?.prepare()
        if (!fromStart)
            moveToLastPosition(lastPosition)
        getController()?.play()
    }

    private fun moveToLastPosition(lastPosition: Long) {
        if (currentMediaItemCount() == 0){
            throw IllegalStateException("Cannot move to last position without any mediaItems prepared")
        }
        var chapterIndex = 0
        while (getEndPositionOfItemAt(chapterIndex) < lastPosition){
            chapterIndex++
            if (chapterIndex > currentMediaItemCount()-1){
                chapterIndex--
                break
            }
        }
        seekTo(chapterIndex, lastPosition - getStartPositionOfItemAt(chapterIndex))
    }


}