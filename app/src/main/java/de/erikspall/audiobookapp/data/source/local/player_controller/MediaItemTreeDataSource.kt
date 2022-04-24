package de.erikspall.audiobookapp.data.source.local.player_controller

import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.use_case.audiobook.GetBooksWithInfo

class MediaItemTreeDataSource() {
    fun updateTree(books: List<AudiobookWithInfo>) {
        MediaItemTree.update(books)
    }

    fun updateTree(getBooksWithInfo: GetBooksWithInfo) {
        MediaItemTree.update(getBooksWithInfo)
    }

    fun getTree() : MediaItemTree {
        return MediaItemTree
    }
}