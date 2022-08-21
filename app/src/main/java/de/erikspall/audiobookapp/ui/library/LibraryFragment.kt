package de.erikspall.audiobookapp.ui.library

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaMetadata
import androidx.mediarouter.app.MediaRouteButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding
import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.domain.const.Player
import de.erikspall.audiobookapp.domain.util.ColorExtractor
import de.erikspall.audiobookapp.domain.util.Conversion
import de.erikspall.audiobookapp.ui.global.events.PlayerEvent
import de.erikspall.audiobookapp.ui.global.viewmodels.PlayerViewModel
import de.erikspall.audiobookapp.ui.library.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.ui.library.event.LibraryEvent
import de.erikspall.audiobookapp.ui.library.viewmodel.LibraryViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    private var isCastPlayerButtonBuild = false

    private lateinit var castContext: CastContext

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        castContext = CastContext.getSharedInstance(requireContext())

        CastButtonFactory.setUpMediaRouteButton(requireContext(), binding.miniPlayer.castButton)
        colorWorkaroundForCastIcon(binding.miniPlayer.castButton)

        pushMiniPlayerAboveNavBar()
        pushRecyclerViewItemsAboveNavBar()

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

        binding.miniPlayer.container.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToNowPlayingFragment()
            binding.miniPlayer.container.findNavController().navigate(action)
            //binding.miniPlayer.container.isVisible = false
        }

        // Start observing player
        playerViewModel.state.playbackState.observe(viewLifecycleOwner) { playbackState ->
            when (playbackState) {
                Player.STATE_NONE -> {
                    Log.d("PlaybackStateLib", "None -> Hiding mini-player")
                    // No media item set -> dont show anything and reset recyclerview
                    pushRecyclerContentAboveMiniPlayer(0)
                    binding.miniPlayer.container.visibility = View.INVISIBLE
                    binding.miniPlayerBackground.visibility = View.INVISIBLE
                }
                Player.STATE_IDLE -> {
                    Log.d("PlaybackStateLib", "Idle -> Do nothing")
                }
                Player.STATE_BUFFERING -> {
                    Log.d(
                        "PlaybackStateLib",
                        "Buffering -> do nothing"
                    )

                    showBufferingInMiniPlayer()
                }
                Player.STATE_READY -> {
                    Log.d("PlaybackStateLib", "Ready -> Begin observing metadata")
                    // Showing current info is handled by observing mediaMetadata
                    pushRecyclerContentAboveMiniPlayer()
                    loadIntoMiniPlayer(playerViewModel.state.mediaMetadata.value!!)
                }
                Player.STATE_ENDED -> {
                    Log.d("PlaybackStateLib", "Ended -> Do nothing")
                }
                Player.STATE_PAUSED -> {
                    Log.d("PlaybackStateLib", "Paused -> update icon")

                    binding.miniPlayer.playButton.icon =
                        getDrawable(requireContext(), R.drawable.ic_play)
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
            if (!binding.miniPlayer.currentBookProgress.isIndeterminate)
                binding.miniPlayer.currentBookProgress.setProgress(progress, true)
        }
    }

    private fun showBufferingInMiniPlayer() {
        binding.miniPlayer.container.visibility = View.VISIBLE
        binding.miniPlayerBackground.visibility = View.VISIBLE
        binding.miniPlayer.playButton.visibility = View.INVISIBLE
        binding.miniPlayer.castButton.visibility = View.INVISIBLE
        binding.miniPlayer.currentBookProgress.isIndeterminate = true
        binding.miniPlayer.currentBookImage.setImageDrawable(null)
        binding.miniPlayer.currentBookTitle.text = ""
        binding.miniPlayer.currentBookChapter.text = ""
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun loadIntoMiniPlayer(metaData: MediaMetadata) {
        binding.miniPlayer.container.visibility = View.VISIBLE
        binding.miniPlayerBackground.visibility = View.VISIBLE
        binding.miniPlayer.playButton.visibility = View.VISIBLE

        binding.miniPlayer.castButton.visibility = View.VISIBLE
        binding.miniPlayer.currentBookProgress.isIndeterminate = false
        //binding.miniPlayer.currentBookProgress.setProgress(playerViewModel.progressBig(), false)
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
        if (viewModel.books.hasActiveObservers())
            viewModel.books.removeObservers(viewLifecycleOwner)

        viewModel.books.observe(viewLifecycleOwner) { books ->
            books.let { adapter.submitList(it) }
            Log.d("LiveData", "NewBooks/Information received!")
        }
    }

    override fun onResume() {
        super.onResume()
        playerViewModel.onEvent(PlayerEvent.LibraryWentToForeground)
    }

    override fun onPause() {
        playerViewModel.onEvent(PlayerEvent.LibraryWentToBackground)
        super.onPause()
    }

    private fun pushMiniPlayerAboveNavBar() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.miniPlayer.container) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left + 16
                bottomMargin = insets.bottom
                rightMargin = insets.right + 16
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            //WindowInsetsCompat.CONSUMED
            windowInsets
        }
    }

    private fun pushRecyclerViewItemsAboveNavBar() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.libraryRecyclerView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {

                bottomMargin =
                    insets.bottom //TODO: Find a way to obtain height of mini player
            }

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun pushRecyclerContentAboveMiniPlayer(miniPlayerHeight: Int = 50) {
        binding.libraryRecyclerView.setPadding(8, 0, 8, Conversion.dpToPx(miniPlayerHeight + 8))
    }

    private fun colorWorkaroundForCastIcon(button: MediaRouteButton?) {
        Log.d("RouteProvider", "I was called!!")
        if (button == null) return
        val castContext: Context =
            ContextThemeWrapper(context, androidx.mediarouter.R.style.Theme_MediaRouter)
        val a = castContext.obtainStyledAttributes(
            null,
            androidx.mediarouter.R.styleable.MediaRouteButton,
            androidx.mediarouter.R.attr.mediaRouteButtonStyle,
            0
        )
        val drawable =
            a.getDrawable(androidx.mediarouter.R.styleable.MediaRouteButton_externalRouteEnabledDrawable)
        a.recycle()
        DrawableCompat.setTint(drawable!!, ColorExtractor.getPrimaryColor(requireContext()))
        drawable.state = button.drawableState
        button.setRemoteIndicatorDrawable(drawable)
    }
}