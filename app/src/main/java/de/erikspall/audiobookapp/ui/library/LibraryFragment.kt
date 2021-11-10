package de.erikspall.audiobookapp.ui.library

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import de.erikspall.audiobookapp.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private lateinit var libraryViewModel: LibraryViewModel
    private var _binding: FragmentLibraryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        libraryViewModel =
            ViewModelProvider(this).get(LibraryViewModel::class.java)

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.gridRecyclerView.adapter = AudioBookCardAdapter(
            this.requireContext()
        )

        // Specify fixed size to improve performance
        binding.gridRecyclerView.setHasFixedSize(true)

        val chip = Chip(this.context)
        chip.text = "Mark-Uwe-Kling"
        chip.isCheckable = true
        chip.isClickable = true

        binding.libraryChipGroup.addView(chip)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}