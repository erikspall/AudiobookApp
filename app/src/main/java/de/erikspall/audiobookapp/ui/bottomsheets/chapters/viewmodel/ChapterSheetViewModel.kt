package de.erikspall.audiobookapp.ui.bottomsheets.chapters.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.erikspall.audiobookapp.domain.model.Chapter
import de.erikspall.audiobookapp.domain.use_case.audiobook.AudiobookUseCases
import de.erikspall.audiobookapp.domain.use_case.playback.PlaybackUseCases
import javax.inject.Inject

@HiltViewModel
class ChapterSheetViewModel @Inject constructor(
    audiobookUseCases: AudiobookUseCases,
    playbackUseCases: PlaybackUseCases
) : ViewModel() {
    val chapters: LiveData<List<Chapter>> = audiobookUseCases.getChapters(playbackUseCases.getCurrent.bookId())
}