package de.erikspall.audiobookapp.ui.library

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import de.erikspall.audiobookapp.AudioBookApp
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.handling.import.Importer
import de.erikspall.audiobookapp.data.model.Audiobook
import de.erikspall.audiobookapp.data.viewmodels.DatabaseViewModel
import de.erikspall.audiobookapp.data.viewmodels.DatabaseViewModelFactory
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding
import de.erikspall.audiobookapp.ui.bottom_sheets.ModalBottomSheet
import de.erikspall.audiobookapp.utils.Conversion
import kotlinx.coroutines.launch

class LibraryFragment : Fragment() {


    private var _binding: FragmentLibraryBinding? = null
    private var isGridLayout = true
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val databaseViewModel: DatabaseViewModel by activityViewModels {
        DatabaseViewModelFactory(
            (activity?.application as AudioBookApp).repository
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = AudioBookCardAdapter(
            this.requireContext(),
            Layout.GRID // Use Grid as standard
        )

        binding.libraryRecyclerView.adapter = adapter

        databaseViewModel.allAudiobooks.observe(viewLifecycleOwner) {audiobooks ->
            audiobooks.let { adapter.submitList(it) }
        }

        //TODO: Check if something was played and show card view
        binding.miniPlayer.container.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToNowPlayingFragment()
            binding.miniPlayer.container.findNavController().navigate(action)
            binding.miniPlayer.container.isVisible = false
        }



        // Prevent mini player from being under nav bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.miniPlayer.container) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.

            view.updateLayoutParams<ViewGroup.MarginLayoutParams>{
                leftMargin = insets.left + 5
                bottomMargin = insets.bottom
                rightMargin = insets.right + 5
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            //WindowInsetsCompat.CONSUMED
            windowInsets
        }

        // Specify fixed size to improve performance
        binding.libraryRecyclerView.setHasFixedSize(true)

        val chip = Chip(this.context)
        chip.text = "Mark-Uwe-Kling"
        chip.isCheckable = true
        chip.isClickable = true

        binding.libraryChipGroup.addView(chip)

        binding.libraryToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_search -> {
                    Toast.makeText(requireContext(), "Search!", Toast.LENGTH_SHORT).show()

                    true
                }
                R.id.menu_add -> {
                    val modalBottomSheet = ModalBottomSheet()
                    modalBottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)
                    true
                }
                R.id.menu_switch_layout -> {
                    isGridLayout = !isGridLayout
                    // Sets layout and icon
                    chooseLayout()
                    setIconAndTitle(menuItem)

                    true
                }
                R.id.menu_settings -> {

                    /*viewModel.getAllAudiobooksOfGenre(1).observe(viewLifecycleOwner, Observer {
                        for (a in it){
                            Toast.makeText(requireContext(), a.audiobookTitle, Toast.LENGTH_LONG).show()
                        }

                    })*/

                    when {//TODO: move away
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED -> {
                           // Toast.makeText(requireContext(), "Already given", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                           //Toast.makeText(requireContext(), "Request that shit", Toast.LENGTH_LONG).show()
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION), 0)
                        }
                    }
                    var test: List<Audiobook> = listOf()
                    databaseViewModel.viewModelScope.launch {
                         test = Importer.createLocalImporter(requireContext()).getAllAsync()

                        Toast.makeText(requireContext(), "Finished", Toast.LENGTH_LONG).show()
                    }




                    true
                }
                else -> false
            }
        }

/*
        binding.libraryToolbar.setNavigationOnClickListener {
            Toast.makeText(requireContext(), "Exit search", Toast.LENGTH_LONG).show()
            leaveSearchState()
        }
*/





                // This is a workaround for a bug. CollapsingToolbar layout is consuming the insets and not
                // passing them to child. DO NOT REMOVE
                ViewCompat.setOnApplyWindowInsetsListener(binding.libraryCollapsingtoolbarlayout){ view, windowInsets ->
                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                    view.updateLayoutParams<ViewGroup.MarginLayoutParams>{
                        topMargin = insets.top
                    }
                    windowInsets
                }

                ViewCompat.setOnApplyWindowInsetsListener(binding.libraryRecyclerView) { view, windowInsets ->
                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                    // Apply the insets as a margin to the view. Here the system is setting
                    // only the bottom, left, and right dimensions, but apply whichever insets are
                    // appropriate to your layout. You can also update the view padding
                    // if that's more appropriate.

                    view.updateLayoutParams<ViewGroup.MarginLayoutParams> {

                        bottomMargin =
                            insets.bottom + Conversion.dpToPx(80) //TODO: Find a way to obtain height of mini player
                    }

                    // Return CONSUMED if you don't want want the window insets to keep being
                    // passed down to descendant views.
                    WindowInsetsCompat.CONSUMED
                }


        /*ViewCompat.setOnApplyWindowInsetsListener(binding.libraryRecyclerView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.

            view.updateLayoutParams<ViewGroup.MarginLayoutParams>{

                bottomMargin = insets.bottom + Conversion.pxToDp(binding.miniPlayer.container.height)
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }*/



        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    private fun chooseLayout() {
        var layoutToUse = Layout.GRID

        if (isGridLayout) {
            binding.libraryRecyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)
        } else {
            binding.libraryRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
            layoutToUse = Layout.LIST
        }
        val adapter = AudioBookCardAdapter(this.requireContext(), layoutToUse)
        binding.libraryRecyclerView.adapter = adapter
        databaseViewModel.allAudiobooks.observe(viewLifecycleOwner) {audiobooks ->
            audiobooks.let { adapter.submitList(it) }
        }
    }



    private fun setIconAndTitle(menuItem: MenuItem?) {
        if (menuItem == null)
            return

        menuItem.icon =
            if (isGridLayout)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_list)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_grid)

        menuItem.title =
            if (isGridLayout)
                getString(R.string.menu_switch_layout_list)
            else getString(R.string.menu_switch_layout_grid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
