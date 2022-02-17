package de.erikspall.audiobookapp.ui.library

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import de.erikspall.audiobookapp.AudioBookApp
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.handling.import.Importer
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor
import de.erikspall.audiobookapp.data.viewmodels.DatabaseViewModel
import de.erikspall.audiobookapp.data.viewmodels.DatabaseViewModelFactory
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding
import de.erikspall.audiobookapp.uamp.PlayerListener
import de.erikspall.audiobookapp.ui.bottom_sheets.ModalBottomSheet
import de.erikspall.audiobookapp.utils.Conversion
import de.erikspall.audiobookapp.viewmodels.AppViewModel
import de.erikspall.audiobookapp.viewmodels.PlayerViewModel
import kotlinx.coroutines.launch

@androidx.media3.common.util.UnstableApi
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null

    //private var isGridLayout = true
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //private val isVisible = true
    private var isInBackground = false


    private val handler = Handler(Looper.getMainLooper())

    private val libraryPlayerListener = PlayerListener(::updateStaticUI, ::onPlaybackToggled)

    private var isUpdatingDynamicUI = false

    private val sharedViewModel: AppViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()
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

        //TODO: Das unten funktionert wenigstens...

        Log.d("FragmentStuff", "LibraryFragment created/re-created!")
        Log.d("FragmentStuff", "<!-- Values here -->")

        chooseLayout()


        //TODO: Check if something was played and show card view
        binding.miniPlayer.container.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToNowPlayingFragment()
            binding.miniPlayer.container.findNavController().navigate(action)
            //binding.miniPlayer.container.isVisible = false
        }


        // Prevent mini player from being under nav bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.miniPlayer.container) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
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
                    true
                }
                R.id.menu_add -> {
                    val modalBottomSheet = ModalBottomSheet()
                    modalBottomSheet.show(
                        requireActivity().supportFragmentManager,
                        ModalBottomSheet.TAG
                    )
                    true
                }
                R.id.menu_switch_layout -> {
                    if (sharedViewModel.layout == Layout.GRID)
                        sharedViewModel.layout = Layout.LIST
                    else
                        sharedViewModel.layout = Layout.GRID
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
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION),
                                0
                            )
                        }
                    }

                    databaseViewModel.viewModelScope.launch {
                        Importer.createLocalImporter(requireContext()).getAllAsync()
                        Toast.makeText(requireContext(), "Finished", Toast.LENGTH_LONG).show()
                    }




                    true
                }
                else -> false
            }
        }

        // This is a workaround for a bug. CollapsingToolbar layout is consuming the insets and not
        // passing them to child. DO NOT REMOVE
        ViewCompat.setOnApplyWindowInsetsListener(binding.libraryCollapsingtoolbarlayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
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

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.miniPlayer.playButton.setOnClickListener {
            playerViewModel.togglePlayback()
        }
        playerViewModel.controllerCreated.observe(viewLifecycleOwner) {
            if (it) {
                playerViewModel.addListener(libraryPlayerListener)

                // Fragment assumes no media is playing, but it could be created while Media is
                // already/still playing -> get In Sync with Session!
                getBackInSyncWithSession()
            }
        }
        Log.d("AppLifecycle", "LibraryFragment created!")
    }

    private fun chooseLayout() {
        if (sharedViewModel.layout == Layout.GRID)
            binding.libraryRecyclerView.layoutManager = GridLayoutManager(this.requireContext(), 2)
        else
            binding.libraryRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        val adapter =
            AudioBookCardAdapter(this.requireContext(), ::startPlayback, sharedViewModel.layout)

        binding.libraryRecyclerView.adapter = adapter
        databaseViewModel.allAudiobooksWithAuthor.observe(viewLifecycleOwner) { audiobooks ->
            audiobooks.let { adapter.submitList(it) }
        }
    }


    private fun setIconAndTitle(menuItem: MenuItem?) {
        if (menuItem == null)
            return

        menuItem.icon =
            if (sharedViewModel.layout == Layout.GRID)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_list)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_grid)

        menuItem.title =
            if (sharedViewModel.layout == Layout.GRID)
                getString(R.string.menu_switch_layout_list)
            else getString(R.string.menu_switch_layout_grid)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        playerViewModel.removeListener(libraryPlayerListener)
        Log.d("AppLifecycle", "LibraryFragment destroyed!")
    }



    private fun startPlayback(audiobookWithAuthor: AudiobookWithAuthor) {
        playerViewModel.playMedia(audiobookWithAuthor)
    }

    private fun hideMiniPlayer() {
        binding.miniPlayer.container.visibility = View.INVISIBLE
        //TODO: Abstände resetten sowie evtl Animieren
    }

    private fun updateStaticUI(mediaMetadata: MediaMetadata) {
        binding.miniPlayer.container.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(mediaMetadata.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_image)
            .into(binding.miniPlayer.currentBookImage)
        binding.miniPlayer.currentBookTitle.text = mediaMetadata.albumTitle
        binding.miniPlayer.currentBookChapter.text = mediaMetadata.title
    }

    private fun onPlaybackToggled(isPlaying: Boolean) {
        if (!isPlaying) {
             binding.miniPlayer.playButton.icon =
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_play)

        } else {
            binding.miniPlayer.playButton.icon =
               ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_pause)
            startUpdatingDynamicUI()
        }

    }

    private fun startUpdatingDynamicUI(){
        if (!isUpdatingDynamicUI){
            Log.d("ProgressTracker", "Started updating UI")
            isUpdatingDynamicUI = true
            updateDynamicUI()
        }
    }

    private fun updateDynamicUI(): Boolean = handler.postDelayed({
        //Checks if Fragment is visible
        if (_binding != null) {
            val progress = playerViewModel.getChapterProgress()
            //Log.d("Progress", "$progress")
            //Ensure that progressbar is always visible
            //if (progress == 0)
            //    progress = 1
            //else
            // binding.miniPlayer.currentBookProgress.progress = progress
            binding.miniPlayer.currentBookProgress.setProgress(progress, true)

            if (!isInBackground && playerViewModel.isPlaying) {
                updateDynamicUI()
            } else {
                isUpdatingDynamicUI = false
                Log.d("ProgressTracker", "Stopped updating UI")
            }
        }
    }, 100)

    private fun getBackInSyncWithSession() {
        if (playerViewModel.isPlaying || playerViewModel.isPaused) {
            updateStaticUI(playerViewModel.getCurrentMediaMetadata())
            // Begin tracking progress
            onPlaybackToggled(playerViewModel.isPlaying)
        }
    }

    // Called when Fragment reaches foreground
    override fun onResume() {
        super.onResume()
        isInBackground = false
        getBackInSyncWithSession()
        Log.d("AppLifecycle", "LibraryFragment resumed!")
    }


    override fun onPause() {
        super.onPause()
        isInBackground = true
        Log.d("AppLifecycle", "LibraryFragment paused!")
    }
}
