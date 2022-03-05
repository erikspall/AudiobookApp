package de.erikspall.audiobookapp.ui.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaMetadata
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding
import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.domain.const.Player
import de.erikspall.audiobookapp.ui.global.events.PlayerEvent
import de.erikspall.audiobookapp.ui.global.viewmodels.PlayerViewModel
import de.erikspall.audiobookapp.ui.library.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.ui.library.event.LibraryEvent
import de.erikspall.audiobookapp.ui.library.viewmodel.LibraryViewModel

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //lifecycleScope.launch {
         //   // Listens for changes in the layout variable
         //   viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
         //       viewModel.state
         //           .map { it.layout }
         //           .distinctUntilChanged()
          //          .collect { newLayout -> setAppropriateManagerAndAdapter(newLayout) }
          //  }
        //}
        viewModel.state.layout.observe(viewLifecycleOwner) { layout ->
            setAppropriateManagerAndAdapter(layout)
        }

        //binding.libraryRecyclerView.itemAnimator = AudiobookItemAnimator()
        //binding.libraryRecyclerView.setHasFixedSize(true)

        return root
    }

    @SuppressLint("UnsafeOptInUsageError")
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
        // Setup mini-player listeners
        binding.miniPlayer.playButton.setOnClickListener {
            playerViewModel.onEvent(PlayerEvent.TogglePlayPause)
        }

        // Start observing player
        playerViewModel.state.playbackState.observe(viewLifecycleOwner) { playbackState ->
            when (playbackState) {
                Player.STATE_NONE -> {
                    Log.d("PlaybackStateLib", "None -> Hiding mini-player")
                    // No media item set -> dont show anything
                    binding.miniPlayer.container.visibility = View.INVISIBLE
                    binding.miniPlayerBackground.visibility = View.INVISIBLE
                }
                Player.STATE_IDLE -> {
                    Log.d("PlaybackStateLib", "Idle -> Do nothing")
                }
                Player.STATE_BUFFERING -> {
                    Log.d("PlaybackStateLib", "Buffering -> Show miniPlayer with progress indicator")
                    showBufferingInMiniPlayer()
                }
                Player.STATE_READY -> {
                    Log.d("PlaybackStateLib", "Ready -> Begin observing metadata")
                    // Showing current info is handled by observing mediaMetadata
                    loadIntoMiniPlayer(playerViewModel.state.mediaMetadata.value!!)

                }
                Player.STATE_ENDED -> {
                    Log.d("PlaybackStateLib", "Ended -> Do nothing")
                }
                Player.STATE_PAUSED -> {
                    Log.d("PlaybackStateLib", "Paused -> update icon")

                    binding.miniPlayer.playButton.icon = getDrawable(requireContext(), R.drawable.ic_play)
                }
                Player.STATE_PLAYING -> {
                    Log.d("PlaybackStateLib", "Playing -> update icon")

                    binding.miniPlayer.playButton.icon =
                        getDrawable(requireContext(), R.drawable.ic_pause)
                }
                else -> {
                    Log.d("PlaybackStateLib", "Unknown -> Do nothing")
                }
            }
        }



        playerViewModel.state.sliderProgress.observe(viewLifecycleOwner) { progress ->
            binding.miniPlayer.currentBookProgress.setProgress(progress, true)
        }
    }

    private fun showBufferingInMiniPlayer() {
        binding.miniPlayer.container.visibility = View.VISIBLE
        binding.miniPlayerBackground.visibility = View.VISIBLE
        binding.miniPlayer.currentBookProgress.isIndeterminate = true
        Glide.with(requireContext())
            .load(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_image)
            .into(binding.miniPlayer.currentBookImage)
        binding.miniPlayer.currentBookTitle.text = ""
        binding.miniPlayer.currentBookChapter.text = "Loading ..."
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun loadIntoMiniPlayer(metaData: MediaMetadata){
        binding.miniPlayer.currentBookProgress.isIndeterminate = false
        binding.miniPlayer.currentBookProgress.setProgress(playerViewModel.progressBig(), false)
        Log.d("Library", "Loading metadata: ${metaData.title}")
        Glide.with(requireContext())
            .load(metaData.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_image)
            .into(binding.miniPlayer.currentBookImage)
        binding.miniPlayer.currentBookTitle.text = metaData.albumTitle
        binding.miniPlayer.currentBookChapter.text = metaData.title

    }

    private fun setAppropriateManagerAndAdapter(newLayout: Int) {
        //viewModel.books.removeObservers(viewLifecycleOwner)
        Log.d("LibraryFragment", "Layout changed to $newLayout!")
        binding.libraryRecyclerView.layoutManager = when (newLayout) {
            Layout.GRID -> GridLayoutManager(requireContext(), 2)
            Layout.LIST -> LinearLayoutManager(requireContext())
            else -> throw IllegalArgumentException("\"$newLayout\" is an unknown Layout!")
        }
        val adapter = AudioBookCardAdapter(
            requireContext(),
            { requestedAudiobook ->
                playerViewModel.onEvent(PlayerEvent.StartPlayback(requestedAudiobook))
            },
            newLayout//,
            //0
        )

        binding.libraryRecyclerView.adapter = adapter
        if (!viewModel.books.hasActiveObservers())
            viewModel.books.observe(viewLifecycleOwner) { books ->
                books.let { adapter.submitList(it) }
                Log.d("LiveData", "NewBooks/Information received!")
            }
    }
/*
    private fun updateViewHolderUI(playingBookId: Long) {
        val adapter = binding.libraryRecyclerView.adapter
        if (adapter is AudioBookCardAdapter) {
            val holder = adapter.getViewHolderOf(playingBookId)
            if (holder is AudioBookCardAdapter.GridCardViewHolder) {
                val previousBookId = holder.currentlyPlayingBookId //TODO: make that more global or something
                if (previousBookId != playingBookId) {
                    val previousHolder = adapter.getViewHolderOf(previousBookId)
                    if (previousHolder is AudioBookCardAdapter.GridCardViewHolder) {
                        previousHolder.playButton.setImageDrawable(
                            getDrawable(
                                requireContext(),
                                R.drawable.ic_play
                            )
                        )
                    }
                    holder.playButton.setImageDrawable(
                        getDrawable(
                            requireContext(),
                                R.drawable.ic_pause //TODO: Animate

                        )
                    )
                    holder.currentlyPlayingBookId = playingBookId
                }
            }
        }
    }*/

    override fun onResume() {
        super.onResume()
        playerViewModel.onEvent(PlayerEvent.AppWentToForeground)
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onEvent(PlayerEvent.AppWentToBackground)
    }
}