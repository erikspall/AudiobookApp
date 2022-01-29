package de.erikspall.audiobookapp.uamp

import android.content.ContentUris
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.*
import com.google.common.collect.ImmutableList
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor

/**
 * A sample media catalog that represents media items as a tree.
 *
 * It fetched the data from {@code catalog.json}. The root's children are folders containing media
 * items from the same album/artist/genre.
 *
 * Each app should have their own way of representing the tree. MediaItemTree is used for
 * demonstration purpose only.
 */
object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var titleMap: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var isInitialized = false
    private const val ROOT_ID = "[rootID]"
    private const val BOOK_ID = "[bookID]"
    private const val GENRE_ID = "[genreID]"
    private const val AUTHOR_ID = "[authorID]"
    private const val NARRATOR_ID = "[narratorID]"
    private const val BOOK_PREFIX = "[book]"
    private const val GENRE_PREFIX = "[genre]"
    private const val AUTHOR_PREFIX = "[author]"
    private const val NARRATOR_PREFIX = "[narrator]"
    private const val ITEM_PREFIX = "[item]"

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
        imageUri: Uri? = null
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(book)
                .setTitle(title)
                .setArtist(author)
                .setComposer(narrator)
                .setGenre(genre)
                .setFolderType(folderType)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .build()
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    fun initialize(mediaList: List<AudiobookWithAuthor>) {
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
        treeNodes[BOOK_ID] =
                MediaItemNode(
                    buildMediaItem(
                        title = "Book Folder",
                        mediaId = BOOK_ID,
                        isPlayable = false,
                        folderType = FOLDER_TYPE_MIXED
                    )
                )
        // mediaList contains all Audiobooks,
        treeNodes[ROOT_ID]!!.addChild(BOOK_ID)
        // TODO: find a way to make it work with livedata (dont update everything)
        for (i in mediaList.indices) {
            addNodeToTree(mediaList[i])
        }
    }

    private fun addNodeToTree(audiobookWithAuthor: AudiobookWithAuthor) {
        // TODO: It is probably better to pass chapters in here idk
        val id = ContentUris.parseId(audiobookWithAuthor.audiobook.uri.toUri()).toString()
        val book = audiobookWithAuthor.audiobook.title
        val title = audiobookWithAuthor.audiobook.title // TODO: Title of Chapter
        val author = audiobookWithAuthor.author.toString()
        val narrator = audiobookWithAuthor.author //TODO: ...
        val genre = "Dummy Genre"
        val sourceUri = audiobookWithAuthor.audiobook.uri.toUri()
        val imageUri = audiobookWithAuthor.audiobook.coverUri.toUri()
        // key of such items in tree
        val idInTree = ITEM_PREFIX + id
        val bookFolderIdInTree = BOOK_PREFIX + book
        // Genre Author etc. here if all works

        // Create Folder for each Audiobook and add
        treeNodes[idInTree] =
            MediaItemNode(
                buildMediaItem(
                    title = title,
                    mediaId = idInTree,
                    isPlayable = true,
                    book = book,
                    author = author,
                    narrator = narrator.toString(),
                    genre = genre,
                    sourceUri = sourceUri,
                    imageUri = imageUri,
                    folderType = FOLDER_TYPE_NONE

                )
            )

        titleMap[title] = treeNodes[idInTree]!!

        if (!treeNodes.containsKey(bookFolderIdInTree)) {
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
        }
        treeNodes[bookFolderIdInTree]!!.addChild(idInTree)

        // TODO: Add into diffrent folders here...
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
        return titleMap[title]?.item
    }

}