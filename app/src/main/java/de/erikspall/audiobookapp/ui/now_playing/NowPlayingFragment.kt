package de.erikspall.audiobookapp.ui.now_playing

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
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
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.FragmentPlayerBinding
import de.erikspall.audiobookapp.domain.const.Player
import de.erikspall.audiobookapp.domain.const.Tags.TAG_CHAPTERS
import de.erikspall.audiobookapp.domain.const.Tags.TAG_SLEEPTIMER
import de.erikspall.audiobookapp.domain.util.ColorExtractor
import de.erikspall.audiobookapp.domain.util.TimeFormatter
import de.erikspall.audiobookapp.ui.bottomsheets.chapters.ChaptersSheet
import de.erikspall.audiobookapp.ui.bottomsheets.sleep_timer.SleepTimerSheet
import de.erikspall.audiobookapp.ui.global.listeners.SliderListener
import de.erikspall.audiobookapp.ui.global.viewmodels.PlayerViewModel
import de.erikspall.audiobookapp.ui.now_playing.event.NowPlayingEvent
import de.erikspall.audiobookapp.ui.now_playing.viewmodel.NowPlayingViewModel
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalBadgeUtils
class NowPlayingFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NowPlayingViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    private var justStarted = true
    private lateinit var sleepTimerBadge: BadgeDrawable


    //lateinit var sleepTimerSharedPref: SharedPreferences



    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the Kotlin extension in the fragment-ktx artifact
        childFragmentManager.setFragmentResultListener("isSleepTimerSet", this) { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val set = bundle.getBoolean("sleepTimerSet")
            Log.d("[FragmentResult]", "SleepTimer set is $set")
            // Do something with the result
            if (set) {
                viewModel.onEvent(NowPlayingEvent.SleepTimerSet)
            } else {
                viewModel.onEvent(NowPlayingEvent.SleepTimerCanceled)
            }
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupInsets()

        binding.chapterSlider.setLabelFormatter(TimeFormatter())
        //sharedPref = requireActivity().getSharedPreferences(getString(R.string.sleep_timer_shared_pref_name), Context.MODE_PRIVATE)



        setupBadge()
        setupListeners()
        setupObservers()

        //if (sharedPref.getBoolean(getString(R.string.sleep_timer_is_set_shared_pref_key), false))
         //   showSleepTimerBadge()


        loadCover(playerViewModel.state.mediaMetadata.value!!)

        return root
    }

    private fun setupBadge() {
        sleepTimerBadge = BadgeDrawable.create(requireContext())
        sleepTimerBadge.isVisible = false
        sleepTimerBadge.backgroundColor = ColorExtractor.getPrimaryColor(requireContext())
        sleepTimerBadge.horizontalOffset = 40
        sleepTimerBadge.verticalOffset = 40


    }

    private fun showSleepTimerBadge() {
        sleepTimerBadge.isVisible = true
        BadgeUtils.attachBadgeDrawable(sleepTimerBadge, binding.bottomAppBar.findViewById(R.id.sleep_timer_button))
    }

    private fun hideSleepTimerBadge() {
        sleepTimerBadge.isVisible = false
        //BadgeUtils.detachBadgeDrawable(sleepTimerBadge, binding.bottomAppBar.findViewById(R.id.sleep_timer_button))
    }

    private fun setupObservers() {
        playerViewModel.state.playbackState.observe(viewLifecycleOwner) { playbackState ->
            when (playbackState) {
                Player.STATE_READY -> {
                    binding.totalBookProgress.isIndeterminate = false
                    // Load new metaData
                    loadCover(playerViewModel.state.mediaMetadata.value!!)
                }
                Player.STATE_PAUSED -> {
                    binding.totalBookProgress.isIndeterminate = false
                    // Update icons
                    Log.d("NowPlaying", "Player paused!")
                    if (justStarted){
                        justStarted = false
                        viewModel.onEvent(NowPlayingEvent.StartedInPause)
                    } else
                        viewModel.onEvent(NowPlayingEvent.OnPause)

                    binding.fabPlay.setImageDrawable(
                        ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_play)
                    )
                }
                Player.STATE_PLAYING -> {
                    binding.totalBookProgress.isIndeterminate = false
                    // Update icons
                    Log.d("NowPlaying", "Player playing!")
                    viewModel.onEvent(NowPlayingEvent.OnPlay)
                    binding.fabPlay.setImageDrawable(
                        ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_pause)
                    )
                }
                else -> {
                    // Do nothing?
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
                binding.chapterSlider.value = value.toFloat()



        }

        viewModel.state.position.observe(viewLifecycleOwner) { newPositions ->
            binding.currentChapterProgressText.text = newPositions.chapterPositionAsString()
            binding.totalCurrentProgressText.text = newPositions.bookPositionAsString()
        }

        // Check if sleepTimer is set before observing value
        /*Log.d("SleepTimerBadge", "Checking if SleepTimer is set ...")
        if (sleepTimerSharedPref.getBoolean(getString(R.string.sleep_timer_is_set_shared_pref_key), false)) {
            Log.d("SleepTimerBadge", "It is set!")
            viewModel.onEvent(NowPlayingEvent.SleepTimerSet)
        } else
            viewModel.onEvent(NowPlayingEvent.CancelSleepTimer)*/



        viewModel.state.isSleepTimerSet.observe(viewLifecycleOwner) { isSleepTimerSet ->
            Log.d("SleepTimerBadge", "SleepTimer making badge visible $isSleepTimerSet")
            if (isSleepTimerSet) {
                showSleepTimerBadge()
            } else
                hideSleepTimerBadge()
            //sleepTimerBadge.setVisible(isSleepTimerSet, true)

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
                    if (viewModel.state.isSleepTimerSet.value == false) {
                        val sleepTimerSheet = SleepTimerSheet()

                        sleepTimerSheet.show(
                            childFragmentManager,
                            TAG_SLEEPTIMER
                        )
                    } else {
                        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(getString(R.string.cancle_sleep_timer_dialog_title))
                            .setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_moon))
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setPositiveButton("Yes") { _, _ ->

                                viewModel.onEvent(NowPlayingEvent.CancelSleepTimer)
                            }
                            .show()
                    }
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
        /*Log.d("SleepTimerBadge", "Registering Listener ...")
        sleepTimerSharedPref.registerOnSharedPreferenceChangeListener(sharedPrefListener)*/

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
        //TODO: unregister sharedPref Listener
        /*Log.d("SleepTimerBadge", "Unregistering Listener ...")
        sleepTimerSharedPref.unregisterOnSharedPreferenceChangeListener(sharedPrefListener)*/
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(NowPlayingEvent.WentToForeground)
    }
}