package de.erikspall.audiobookapp.uamp

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_MIXED
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_NONE
import com.google.common.collect.ImmutableList
import de.erikspall.audiobookapp.data.source.local.database.model.AudiobookWithInfo
import de.erikspall.audiobookapp.data.source.local.database.model.Chapter

/**
 * A sample media catalog that represents media items as a tree.
 *
 * It fetched the data from {@code catalog.json}. The root's children are folders containing media
 * items from the same album/artist/genre.
 *
 * Each app should have their own way of representing the tree. MediaItemTree is used for
 * demonstration purpose only.
 */
@androidx.media3.common.util.UnstableApi
object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var bookUriMap: MutableMap<String, MediaItemNode> = mutableMapOf()
    //private var bookMap: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var isInitialized = false
    private const val ROOT_ID = "[rootID]"
    private const val LIB_ID = "[bookID]"
    private const val GENRE_ID = "[genreID]"
    private const val AUTHOR_ID = "[authorID]"
    private const val NARRATOR_ID = "[narratorID]"
    private const val BOOK_PREFIX = "[book]"
    private const val CHAPTER_PREFIX = "[chapter]"
    private const val AUTHOR_PREFIX = "[author]"
    private const val NARRATOR_PREFIX = "[narrator]"
    private const val ITEM_PREFIX = "[item]"
    const val METADATA_KEY_DURATION = "1"

    private class MediaItemNode(val item: MediaItem) {
        private val children: MutableList<MediaItem> = ArrayList()

        fun addChild(childID: String) {
            this.children.add(treeNodes[childID]!!.item)
        }

        fun getChildren(): List<MediaItem> {
            return ImmutableList.copyOf(children)
        }
    }

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        @MediaMetadata.FolderType folderType: Int,
        book: String? = null,
        author: String? = null,
        narrator: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null,
        startPosition: Long? = null,
        endPosition: Long? = null,
        duration: Bundle? = null
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(book)
                .setTitle(title)
                .setArtist(book) //TODO: back to author? but this way finding book is easy
                .setComposer(narrator)
                .setGenre(genre)
                .setFolderType(folderType)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .setMediaUri(sourceUri)
                .setExtras(duration)
                .build()

        MediaItem.ClippingConfiguration.Builder().setStartPositionMs(100).build()
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startPosition ?: 0)
                    .setEndPositionMs(endPosition ?: C.TIME_END_OF_SOURCE)
                    .build()
            )
            .build()
    }

    fun initialize(mediaList: List<AudiobookWithInfo>) {
        if (isInitialized) return
        isInitialized = true
        // create root
        treeNodes[ROOT_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Root Folder",
                    mediaId = ROOT_ID,
                    isPlayable = false,
                    folderType = FOLDER_TYPE_MIXED
                )
            )
        // [...] skip folders
        treeNodes[LIB_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Library Folder",
                    mediaId = LIB_ID,
                    isPlayable = false,
                    folderType = FOLDER_TYPE_MIXED
                )
            )

        treeNodes[ROOT_ID]!!.addChild(LIB_ID)

        for (book in mediaList){
            val BOOK_ID = "[book]" + book.audiobook.audiobookId
            val duration: Bundle = Bundle()
            duration.putLong(METADATA_KEY_DURATION, book.audiobook.duration)

            treeNodes[BOOK_ID] =
                MediaItemNode(
                    buildMediaItem(
                        title = book.audiobook.title,
                        mediaId = BOOK_ID,
                        isPlayable = false, //TODO: it is playable!!!
                        folderType = FOLDER_TYPE_MIXED,
                        duration = duration
                    )
                )

            treeNodes[LIB_ID]!!.addChild(BOOK_ID)
            bookUriMap[book.audiobook.uri] = treeNodes[BOOK_ID]!!

            for (chapter in book.chapters) {
                addNodeToTree(BOOK_ID, book, chapter)
            }
        }

        // mediaList contains all Audiobooks,

        // TODO: find a way to make it work with livedata (dont update everything)

    }

    private fun addNodeToTree(BOOK_ID: String, audiobookWithInfo: AudiobookWithInfo, chapter: Chapter) {
        // TODO: It is probably better to pass chapters in here idk
        val id = BOOK_ID + CHAPTER_PREFIX + chapter.chapterId
        val book = audiobookWithInfo.audiobook.title
        val title = chapter.title
        val author = audiobookWithInfo.author.toString()
        val narrator = audiobookWithInfo.narrator.toString()
        val genre = audiobookWithInfo.genres[0].name ?: "No genre" //TODO: can be empty!!!
        val sourceUri = audiobookWithInfo.audiobook.uri.toUri()
        val imageUri = audiobookWithInfo.audiobook.coverUri.toUri()

        val duration: Bundle = Bundle()
        duration.putLong(METADATA_KEY_DURATION, ((chapter.end_time.toDouble() - chapter.start_time.toDouble())*1000).toLong())

        // Create Folder for each Chapter and add
        treeNodes[id] =
            MediaItemNode(
                buildMediaItem(
                    title = title,
                    mediaId = id,
                    isPlayable = true,
                    book = book,
                    author = author,
                    narrator = narrator,
                    genre = genre,
                    sourceUri = sourceUri,
                    imageUri = imageUri,
                    folderType = FOLDER_TYPE_NONE,
                    startPosition = (chapter.start_time.toDouble()*1000).toLong(),
                    endPosition = (chapter.end_time.toDouble()*1000).toLong(),
                    duration = duration
                )
            )

        //titleMap[title] = treeNodes[id]!!

        /*if (!treeNodes.containsKey(bookFolderIdInTree)) {
            treeNodes[bookFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = book,
                        mediaId = bookFolderIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
            treeNodes[BOOK_ID]!!.addChild(bookFolderIdInTree)
        }*/
        treeNodes[BOOK_ID]!!.addChild(id)
    }

    fun getItem(id: String): MediaItem? {
        return treeNodes[id]?.item
    }

    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_ID]!!.item
    }

    fun getChildren(id: String): List<MediaItem>? {
        return treeNodes[id]?.getChildren()
    }

    fun getRandomItem(): MediaItem {
        var curRoot = getRootItem()
        while (curRoot.mediaMetadata.folderType != FOLDER_TYPE_NONE) {
            val children = getChildren(curRoot.mediaId)!!
            curRoot = children.random()
        }
        return curRoot
    }

    fun getItemFromTitle(title: String): MediaItem? {
        return MediaItem.EMPTY
    }

    /**
     * Useful to get Book Media Item of chapter
     * @param uri - A String representing the media URI
     * @return The MediaItem with this uri, returns MediaItem.EMPTY if no MediaItem with this URI
     * was found
     */
    fun getItemFromUri(uri: String): MediaItem {
        return bookUriMap[uri]?.item ?: MediaItem.EMPTY
    }

    /**
     * Useful to get Book Media Item of chapter
     * @param bookId - An Int representing the unique audiobookId in the Room database
     * @return List of MediaItems representing chapters if bookId
     */
    fun getChaptersOfBook(bookId: Long): List<MediaItem> {
        if (!treeNodes.containsKey(BOOK_PREFIX + bookId))
            return emptyList()
        return treeNodes[BOOK_PREFIX + bookId]!!.getChildren()
    }

}