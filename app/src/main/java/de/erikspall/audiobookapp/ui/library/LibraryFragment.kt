package de.erikspall.audiobookapp.ui.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding
import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.old.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.ui.library.event.LibraryEvent
import de.erikspall.audiobookapp.ui.library.viewmodel.LibraryViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        lifecycleScope.launch {
            // Listens for changes in the layout variable
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .map { it.layout }
                    .distinctUntilChanged()
                    .collect { newLayout -> setAppropriateManagerAndAdapter(newLayout) }

            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup all listeners etc.
        binding.libraryToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_search -> {
                    true
                }
                R.id.menu_add -> {
                    viewModel.onEvent(LibraryEvent.Import)
                    true
                }
                R.id.menu_switch_layout -> {
                    viewModel.onEvent(LibraryEvent.SwitchLayout)
                    true
                }
                else -> false
            }

        }
    }

    private fun setAppropriateManagerAndAdapter(newLayout: Int) {
        Log.d("LibraryFragment", "Layout changed to $newLayout!")
        binding.libraryRecyclerView.layoutManager = when (newLayout) {
            Layout.GRID -> GridLayoutManager(requireContext(), 2)
            Layout.LIST -> LinearLayoutManager(requireContext())
            else -> throw IllegalArgumentException("\"$newLayout\" is an unknown Layout!")
        }
        val adapter = AudioBookCardAdapter(
            requireContext(),
            { _, _ -> },
            newLayout,
            0
        )

        binding.libraryRecyclerView.adapter = adapter
        viewModel.books.observe(viewLifecycleOwner) { books ->
            books.let { adapter.submitList(it) }
        }
    }
}