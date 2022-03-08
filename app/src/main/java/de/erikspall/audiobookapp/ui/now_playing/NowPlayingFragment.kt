package de.erikspall.audiobookapp.ui.now_playing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaMetadata
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.FragmentPlayerBinding
import de.erikspall.audiobookapp.domain.const.Player
import de.erikspall.audiobookapp.domain.const.Tags.TAG_CHAPTERS
import de.erikspall.audiobookapp.domain.const.Tags.TAG_SLEEPTIMER
import de.erikspall.audiobookapp.domain.util.TimeFormatter
import de.erikspall.audiobookapp.ui.bottomsheets.chapters.ChaptersSheet
import de.erikspall.audiobookapp.ui.bottomsheets.sleep_timer.SleepTimerSheet
import de.erikspall.audiobookapp.ui.global.listeners.SliderListener
import de.erikspall.audiobookapp.ui.global.viewmodels.PlayerViewModel
import de.erikspall.audiobookapp.ui.now_playing.event.NowPlayingEvent
import de.erikspall.audiobookapp.ui.now_playing.viewmodel.NowPlayingViewModel

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NowPlayingViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupInsets()

        binding.chapterSlider.setLabelFormatter(TimeFormatter())

        setupListeners()
        setupObservers()

        loadCover(playerViewModel.state.mediaMetadata.value!!)

        return root
    }

    private fun setupObservers() {
        playerViewModel.state.playbackState.observe(viewLifecycleOwner) { playbackState ->
            when (playbackState) {
                Player.STATE_READY -> {
                    // Load new metaData
                    loadCover(playerViewModel.state.mediaMetadata.value!!)
                }
                Player.STATE_PAUSED -> {
                    // Update icons
                    Log.d("LiveData-NowPlaying", "Player paused!")
                    viewModel.onEvent(NowPlayingEvent.OnPause)
                    binding.fabPlay.setImageDrawable(
                        ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_play)
                    )
                }
                Player.STATE_PLAYING -> {
                    // Update icons
                    Log.d("LiveData-NowPlaying", "Player playing!")
                    viewModel.onEvent(NowPlayingEvent.OnPlay)
                    binding.fabPlay.setImageDrawable(
                        ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_pause)
                    )
                }
            }
        }
        viewModel.state.static.observe(viewLifecycleOwner) { newStaticInfo ->
            binding.chapterSlider.valueTo = newStaticInfo.chapterDuration.toFloat()
            binding.bookName.text = newStaticInfo.bookTitle
            binding.chapterName.text = newStaticInfo.chapterTitle
            binding.chapterDurationText.text = newStaticInfo.chapterDurationAsString()
            binding.totalDurationText.text = newStaticInfo.bookDurationAsString()
        }

        viewModel.state.bookProgressValue.observe(viewLifecycleOwner) { value ->
            binding.totalBookProgress.setProgress(value, false)
        }

        viewModel.state.chapterSliderValue.observe(viewLifecycleOwner) { value ->
            // TODO: Check if value is in bounds

                binding.chapterSlider.value = value.toFloat()



        }

        viewModel.state.position.observe(viewLifecycleOwner) { newPositions ->
            binding.currentChapterProgressText.text = newPositions.chapterPositionAsString()
            binding.totalCurrentProgressText.text = newPositions.bookPositionAsString()
        }
    }

    private fun setupListeners() {
        binding.chapterSlider.addOnSliderTouchListener(SliderListener(
            {
                viewModel.onEvent(NowPlayingEvent.SliderDragged(true))
            },
            { seekPosition ->
                viewModel.onEvent(NowPlayingEvent.SliderDragged(false))
                viewModel.onEvent(NowPlayingEvent.SeekTo(seekPosition))
            }
        ))
        binding.fabPlay.setOnClickListener {
            viewModel.onEvent(NowPlayingEvent.TogglePlayPause)
        }
        binding.fabForward.setOnClickListener {
            viewModel.onEvent(NowPlayingEvent.SeekForward)
        }
        binding.fabSkip.setOnClickListener {
            viewModel.onEvent(NowPlayingEvent.SkipForward)
        }
        binding.fabBackward.setOnClickListener {
            viewModel.onEvent(NowPlayingEvent.SeekBackward)
        }
        binding.fabGoBack.setOnClickListener {
            viewModel.onEvent(NowPlayingEvent.SkipBackward)
        }
        binding.playerToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sleep_timer_button -> {
                    SleepTimerSheet().show(
                        requireActivity().supportFragmentManager,
                        TAG_SLEEPTIMER
                    )
                    true
                }
                R.id.chapter_button -> {
                    ChaptersSheet().show(
                        requireActivity().supportFragmentManager,
                        TAG_CHAPTERS
                    )
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.nowPlayingLayout){ view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams>{
                topMargin = insets.top
            }
            windowInsets
        }
    }

    private fun loadCover(data: MediaMetadata) {
        Glide.with(requireContext())
            .load(data.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_image)
            .into(binding.bookImage)
        Log.d("LiveData-NowPlaying", "Loading Cover")
        //binding.bookName.text = data.albumTitle
        //binding.chapterName.text = data.title


    }

    override fun onPause() {
        viewModel.onEvent(NowPlayingEvent.WentToBackground)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(NowPlayingEvent.WentToForeground)
    }
}