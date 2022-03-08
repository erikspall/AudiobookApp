package de.erikspall.audiobookapp.ui.bottomsheets.chapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.databinding.ModalBottomSheetChaptersBinding
import de.erikspall.audiobookapp.databinding.ModalBottomSheetSleepTimerBinding
import de.erikspall.audiobookapp.domain.use_case.audiobook.AudiobookUseCases
import de.erikspall.audiobookapp.domain.use_case.playback.PlaybackUseCases
import de.erikspall.audiobookapp.ui.bottomsheets.chapters.adapter.ChapterItemAdapter
import de.erikspall.audiobookapp.ui.bottomsheets.chapters.viewmodel.ChapterSheetViewModel
import de.erikspall.audiobookapp.ui.library.viewmodel.LibraryViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ChaptersSheet : BottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetChaptersBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var audiobookUseCases: AudiobookUseCases
    @Inject
    lateinit var playbackUseCases: PlaybackUseCases

    private val viewModel: ChapterSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ModalBottomSheetChaptersBinding.inflate(inflater, container, false)

        binding.chapterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ChapterItemAdapter()
        binding.chapterRecyclerView.adapter = adapter

        if (!viewModel.chapters.hasActiveObservers())
            viewModel.chapters.observe(viewLifecycleOwner) { chapters ->
                chapters.let {
                    adapter.submitList(chapters)
                }
            }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
    }
}