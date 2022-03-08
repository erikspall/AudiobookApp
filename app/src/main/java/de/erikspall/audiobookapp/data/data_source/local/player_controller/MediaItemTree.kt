package de.erikspall.audiobookapp.data.data_source.local.player_controller

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.*
import com.google.common.collect.ImmutableList
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.AUTHOR_ID
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.BOOK_PREFIX
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.CHAPTER_PREFIX
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.LIBRARY_ID
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.METADATA_BOOK_ID
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.METADATA_CHAPTER_ID
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.METADATA_KEY_DURATION
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.NARRATOR_ID
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.PERSON_PREFIX
import de.erikspall.audiobookapp.domain.const.MediaTreeConst.ROOT_ID
import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.model.Chapter
import de.erikspall.audiobookapp.domain.model.Person
import de.erikspall.audiobookapp.domain.use_case.audiobook.GetBooksWithInfo
import de.erikspall.audiobookapp.domain.util.audiobook.order.AudiobookOrder
import de.erikspall.audiobookapp.domain.util.audiobook.order.OrderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()

    private var bookUriMap: MutableMap<Uri, MediaItemNode> = mutableMapOf()
    private var bookTitleMap: MutableMap<String, MediaItemNode> = mutableMapOf()

    private var isInitialized = false


    private class MediaItemNode(val item: MediaItem) {
        private val children: MutableList<MediaItem> = ArrayList()

        @SuppressLint("UnsafeOptInUsageError")
        fun addChild(childId: String) {
            if (!this.children.contains(treeNodes[childId]!!.item))
                this.children.add(treeNodes[childId]!!.item)
            else
                Log.d("MediaItemTree", "$childId is already child of ${this.item.mediaId}")
        }

        fun getChildren(): List<MediaItem> {
            return ImmutableList.copyOf(children)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun buildMediaItem( //MediaItem == Chapter
        title: String, // Title
        mediaId: String,
        isPlayable: Boolean,
        @FolderType folderType: Int,
        bookTitle: String? = null, //Album
        author: String? = null, //Artist
        narrator: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        coverUri: Uri? = null,
        startTime: Long? = null,
        endTime: Long? = null,
        extras: Bundle? = null
    ): MediaItem {
        val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
            .setStartPositionMs(startTime ?: 0)
            .setEndPositionMs(endTime ?: C.TIME_END_OF_SOURCE)
            .build()
        val metadata = MediaMetadata.Builder()
            .setAlbumTitle(bookTitle)
            .setTitle(title)
            .setArtist(author)
            .setGenre(genre)
            .setFolderType(folderType)
            .setIsPlayable(isPlayable)
            .setArtworkUri(coverUri)
            .setAlbumArtist(narrator)
            .setMediaUri(sourceUri)
            .setExtras(extras)
            .build()
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .setClippingConfiguration(clippingConfiguration)
            .build()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun initFolders() {
        if (isInitialized) {
            // TODO: Update
            return
        } else {
            isInitialized = true
            Log.d("MediaItemTree", "Creating folders...")
            // Create root, library, author and narrator folders in tree
            treeNodes[ROOT_ID] =
                MediaItemNode(
                    buildMediaItem(
                        title = "Root",
                        mediaId = ROOT_ID,
                        isPlayable = false,
                        folderType = FOLDER_TYPE_MIXED
                    )
                )
            treeNodes[LIBRARY_ID] =
                MediaItemNode(
                    buildMediaItem(
                        title = "All Books",
                        mediaId = LIBRARY_ID,
                        isPlayable = false,
                        folderType = FOLDER_TYPE_ALBUMS //Album == Book
                    )
                )
            treeNodes[AUTHOR_ID] =
                MediaItemNode(
                    buildMediaItem(
                        title = "Authors",
                        mediaId = AUTHOR_ID,
                        isPlayable = false,
                        folderType = FOLDER_TYPE_ARTISTS
                    )
                )
            treeNodes[NARRATOR_ID] =
                MediaItemNode(
                    buildMediaItem(
                        title = "Narrators",
                        mediaId = NARRATOR_ID,
                        isPlayable = false,
                        folderType = FOLDER_TYPE_ARTISTS
                    )
                )

            treeNodes[ROOT_ID]!!.addChild(LIBRARY_ID)
            treeNodes[ROOT_ID]!!.addChild(AUTHOR_ID)
            treeNodes[ROOT_ID]!!.addChild(NARRATOR_ID)
            Log.d("MediaItemTree", "Folders created!")

        }
    }

    /**
     * This is called from the playback service
     * Main folders such as Library, Author, Narrator, ... are all created
     * in sync, otherwise a null pointer exception can occur
     * Books are added async
     */
    fun update(getBooksWithInfo: GetBooksWithInfo) {
        initFolders()
        // Now add all books to tree ...
        MainScope().launch {
            val books = withContext(Dispatchers.IO){
                getBooksWithInfo(AudiobookOrder.Title(OrderType.Ascending)).first()
            }
            Log.d("MediaItemTree", "Adding books ...")
            Log.d("MediaItemTree", "Total amount of books: ${books.count()}")
            var debugCount = 1
            for (book in books) {
                Log.d("MediaItemTree", "Adding book $debugCount/${books.count()}")
                addPersonToTree(true, book.author)
                addPersonToTree(false, book.narrator)
                addBookToTree(book)
                debugCount++

                Log.d("MediaItemTree", "Books added!")
            }
        }

    }

    /**
     * This is called everytime livedata changes
     * Main folders such as Library, Author, Narrator, ... are all created
     * in sync, otherwise a null pointer exception can occur
     * Books are added async
     */
    @SuppressLint("UnsafeOptInUsageError")
    fun update(books: List<AudiobookWithInfo>) {
        initFolders()
        // Now add all books to tree ...
        MainScope().launch {
            Log.d("MediaItemTree", "Adding books ...")
            Log.d("MediaItemTree", "Total amount of books: ${books.count()}")
            var debugCount = 1
            for (book in books) {
                Log.d("MediaItemTree", "Adding book $debugCount/${books.count()}")
                addPersonToTree(true, book.author)
                addPersonToTree(false, book.narrator)
                addBookToTree(book)
                debugCount++

                Log.d("MediaItemTree", "Books added!")
            }
        }
    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun addBookToTree(book: AudiobookWithInfo) {
        // Created book media item as to be added to ALL correct parent nodes
        // currently only library exists
        val id = book.audiobook.audiobookId
        val bookFolderIdInTree = BOOK_PREFIX + id

        val extras: Bundle = Bundle()
        extras.putLong(
            METADATA_KEY_DURATION,
            book.audiobook.duration
        )
        extras.putLong(
            METADATA_BOOK_ID,
            book.audiobook.audiobookId
        )

        // Create Book in Tree
        if (!treeNodes.containsKey(bookFolderIdInTree)) {
            treeNodes[bookFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = book.audiobook.title,
                        mediaId = bookFolderIdInTree,
                        isPlayable = true,
                        author = book.author.toString(),
                        narrator = book.narrator.toString(),
                        genre = (book.genres.getOrNull(0) ?: "No genre").toString(),
                        folderType = FOLDER_TYPE_PLAYLISTS,
                        coverUri = book.audiobook.coverUri.toUri(),
                        extras = extras
                    )
                )
            // Add book to all needed folders
            treeNodes[LIBRARY_ID]!!.addChild(bookFolderIdInTree)
            treeNodes[PERSON_PREFIX + book.author.personId]!!.addChild(bookFolderIdInTree)
            treeNodes[PERSON_PREFIX + book.narrator.personId]!!.addChild(bookFolderIdInTree)

            bookTitleMap[book.audiobook.title] = treeNodes[bookFolderIdInTree]!!
            bookUriMap[book.audiobook.uri.toUri()] = treeNodes[bookFolderIdInTree]!!


            addChaptersToTree(bookFolderIdInTree, book.audiobook.uri.toUri(), book.chapters)
        } else
            Log.d("MediaItemTree", "Book $bookFolderIdInTree already in tree")

    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun addPersonToTree(asAuthor: Boolean, person: Person) {
        val id = person.personId
        val personIdInTree = PERSON_PREFIX + id

        if (!treeNodes.containsKey(personIdInTree)) {
            treeNodes[personIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = person.toString(),
                        mediaId = personIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
        } else {
            Log.d("MediaItemTree", "Person $personIdInTree already in tree")
        }
        if (asAuthor) {
            treeNodes[AUTHOR_ID]!!.addChild(personIdInTree)
        } else {
            treeNodes[NARRATOR_ID]!!.addChild(personIdInTree)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun addChaptersToTree(
        bookFolderIdInTree: String,
        sourceUri: Uri,
        chapters: List<Chapter>
    ) {
        val book = treeNodes[bookFolderIdInTree]!!.item
        for (chapter in chapters) {
            val id =
                chapter.chapterId // Chapter ids != primary key, only in combination with bookId
            val chapterIdInTree = bookFolderIdInTree + CHAPTER_PREFIX + id
            if (!treeNodes.containsKey(chapterIdInTree)) {

                val extras: Bundle = Bundle()
                extras.putLong(
                    METADATA_KEY_DURATION,
                    ((chapter.end_time.toDouble() - chapter.start_time.toDouble()) * 1000).toLong()
                )
                extras.putLong(
                    METADATA_BOOK_ID,
                    chapter.audiobookId
                )
                extras.putLong(
                    METADATA_CHAPTER_ID,
                    chapter.chapterId
                )

                treeNodes[chapterIdInTree] =
                    MediaItemNode(
                        buildMediaItem(
                            title = chapter.title,
                            mediaId = chapterIdInTree,
                            isPlayable = true,
                            bookTitle = book.mediaMetadata.title.toString(),
                            author = book.mediaMetadata.artist.toString(),
                            narrator = book.mediaMetadata.albumArtist.toString(),
                            genre = book.mediaMetadata.genre.toString(),
                            sourceUri = sourceUri,
                            coverUri = book.mediaMetadata.artworkUri,
                            folderType = FOLDER_TYPE_NONE,
                            startTime = (chapter.start_time.toDouble() * 1000).toLong(),
                            endTime = (chapter.end_time.toDouble() * 1000).toLong(),
                            extras = extras
                        )
                    )
                treeNodes[bookFolderIdInTree]!!.addChild(chapterIdInTree)
            } else {
                Log.d("MediaItemTree", "Chapter $chapterIdInTree already in tree")
            }
        }
    }


    /** Getters **/
    fun getBook(id: Int): MediaItem? {
        return treeNodes[BOOK_PREFIX + id]?.item
    }

    fun getItem(id: String): MediaItem? {
        return treeNodes[id]?.item
    }

    fun getChildren(id: String): List<MediaItem>? {
        return treeNodes[id]?.getChildren()
    }

    fun getChapters(bookId: Long): List<MediaItem>? {
        return treeNodes[BOOK_PREFIX + bookId]?.getChildren()
    }

    fun getChapter(bookId: Int, chapterId: Int): MediaItem? {
        return treeNodes[BOOK_PREFIX + bookId + CHAPTER_PREFIX + chapterId]?.item
    }

    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_ID]!!.item
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getRandomItem(): MediaItem {
        var curRoot = getRootItem()
        while (curRoot.mediaMetadata.folderType != FOLDER_TYPE_NONE) {
            val children = getChildren(curRoot.mediaId)!!
            curRoot = children.random()
        }
        return curRoot
    }

    fun geBookFromTitle(title: String): MediaItem? {
        return bookTitleMap[title]?.item
    }

    fun getBookFromUri(uri: Uri): MediaItem? {
        return bookUriMap[uri]?.item
    }

    fun getDurationOfBook(uri: Uri): Long {
        return getBookFromUri(uri)?.mediaMetadata?.extras?.getLong(
            METADATA_KEY_DURATION) ?: 1
    }

    fun getDurationOfChapter(mediaId: String): Long {
        return getItem(mediaId)?.mediaMetadata?.extras?.getLong(METADATA_KEY_DURATION) ?: 1
    }

    fun getBooksOfPerson(personId: Int): List<MediaItem> {
        return treeNodes[PERSON_PREFIX + personId]!!.getChildren()
    }

}