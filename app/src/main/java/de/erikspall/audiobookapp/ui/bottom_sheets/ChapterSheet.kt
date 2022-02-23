package de.erikspall.audiobookapp.ui.bottom_sheets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.erikspall.audiobookapp.adapter.bottomsheets.ChapterItemAdapter
import de.erikspall.audiobookapp.databinding.ModalBottomSheetChaptersBinding

class ChapterSheet(
    private val chapters: Array<MediaItem> //TODO: Not allowed...
): BottomSheetDialogFragment() {
    private var _binding: ModalBottomSheetChaptersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ModalBottomSheetChaptersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("ChapterSheet", "Chapters: ${chapters.size}")
        binding.chapterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chapterRecyclerView.adapter = ChapterItemAdapter(chapters)
    }
}