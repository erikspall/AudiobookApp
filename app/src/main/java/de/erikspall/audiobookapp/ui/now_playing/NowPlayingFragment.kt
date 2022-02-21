package de.erikspall.audiobookapp.ui.now_playing

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaMetadata
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.databinding.FragmentPlayerBinding
import de.erikspall.audiobookapp.uamp.PlaybackService
import de.erikspall.audiobookapp.uamp.PlayerListener
import de.erikspall.audiobookapp.ui.bottom_sheets.SleepTimerSheet
import de.erikspall.audiobookapp.utils.Conversion
import de.erikspall.audiobookapp.utils.TimeFormatter
import de.erikspall.audiobookapp.utils.extensions.setDrawableColor
import de.erikspall.audiobookapp.viewmodels.PlayerViewModel


class NowPlayingFragment: Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private var isInBackground = false
    private var isUpdatingDynamicUI = false
    private var isUpdatingSlider = false
    @SuppressLint("UnsafeOptInUsageError")
    private val nowPlayingPlayerListener = PlayerListener(::updateStaticUI, ::onPlaybackToggled)
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private var handler = Handler(Looper.getMainLooper())
    private var sliderIsBeingDragged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.playerToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }





        binding.chapterSlider.setLabelFormatter(TimeFormatter())
        ViewCompat.setOnApplyWindowInsetsListener(binding.nowPlayingLayout){ view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams>{
                topMargin = insets.top
            }
            windowInsets
        }
        return root
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.chapterSlider.addOnSliderTouchListener(SliderListener(
            ::onStartSeek,
            ::seek
        ))

        binding.fabGoBack.setOnClickListener {
            onGoBackChapter()
        }

        binding.fabSkip.setOnClickListener {
            onSkipChapter()
        }

        binding.fabForward.setOnClickListener {
            onForward(30000)
        }

        binding.fabPlay.setOnClickListener {
            playerViewModel.togglePlayback()
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sleep_timer_button -> {
                    val sleepTimerSheet = SleepTimerSheet(::setSleepTimer)
                    sleepTimerSheet.show(
                        requireActivity().supportFragmentManager,
                        SleepTimerSheet.TAG
                    )
                    true
                }
                else -> false
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("FragmentStuff", "NowPlayingFragment destroyed!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerViewModel.removeListener(nowPlayingPlayerListener)
        _binding = null
    }

    private fun getBackInSyncWithSession() {
        if (playerViewModel.isPlaying || playerViewModel.isPaused) {
            Log.d("StateManagment", "Time to get back to sync!!!")
            updateStaticUI(playerViewModel.getCurrentMediaMetadata())
            // Begin tracking progress
            onPlaybackToggled(playerViewModel.isPlaying)
        } else {
            Log.d("StateManagment", "Player is not playing nor paused, don't get back in sync")
        }
    }

    private fun onStartSeek() {
        sliderIsBeingDragged = true
    }

    private fun seek(pos: Long) {
        sliderIsBeingDragged = false
        if (pos <= playerViewModel.getChapterDuration()) {
            playerViewModel.seekTo(pos)
        }
        startUpdatingDynamicUI()
    }

    private fun onPlaybackToggled(isPlaying: Boolean) {
        if (!isPlaying) {
            binding.fabPlay.setImageDrawable(
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_play))
            if (binding.currentChapterProgressText.text.isEmpty())
                startUpdatingDynamicUI()
        } else {
            binding.fabPlay.setImageDrawable(
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_pause))
            startUpdatingDynamicUI()
        }

    }


    private fun startUpdatingDynamicUI(){
        if (!isUpdatingDynamicUI){
            Log.d("ProgressTracker", "Started updating NowPlaying-UI")
            isUpdatingDynamicUI = true
            updateDynamicUI()
        }
        if (!isUpdatingSlider){
            Log.d("ProgressTracker", "Started updating NowPlaying-Slider")
            isUpdatingSlider = true
            updateSlider()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun updateStaticUI(mediaMetadata: MediaMetadata) {
        Glide.with(requireContext())
            .load(mediaMetadata.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_image)
            .into(binding.bookImage)
        binding.bookName.text = mediaMetadata.albumTitle
        binding.chapterName.text = mediaMetadata.title
        binding.chapterDurationText.text = Conversion.millisToStr(playerViewModel.getChapterDuration())
        binding.totalDurationText.text = Conversion.millisToStr(playerViewModel.getBookDuration())
        binding.chapterSlider.value = 0f //Otherwise may crash because valueTo gets set below current value
        binding.chapterSlider.valueTo = playerViewModel.getChapterDuration().toFloat()
    }

    /**
     * Updates components that do need a smooth value increase
     */
    private fun updateSlider(): Boolean = handler.postDelayed({
        //Checks if Fragment is visible
        if (_binding != null) {
            //val totalProgress = playerViewModel.getBookProgress()
            val chapterProgress = playerViewModel.getCurrentPositionInChapter().toFloat()

           // binding.totalBookProgress.setProgress(totalProgress, false) // Wont be visible so dont animate
            //binding.chapterSlider.value = chapterProgress.toFloat()
            if (chapterProgress < binding.chapterSlider.valueFrom){
                binding.chapterSlider.value = binding.chapterSlider.valueFrom
            } else if (chapterProgress > binding.chapterSlider.valueTo) {
                binding.chapterSlider.value = binding.chapterSlider.valueTo
            } else {

                //TODO: Animation makes dragging slider clunky
                /*ValueAnimator.ofFloat(binding.chapterSlider.value, chapterProgress.toFloat())
                    .apply {
                        duration = 100

                        addUpdateListener { updatedAnimation ->
                            binding.chapterSlider.value = updatedAnimation.animatedValue as Float
                        }

                        start()
                    }*/
                binding.chapterSlider.value = chapterProgress
            }
            if (!isInBackground && playerViewModel.isPlaying && !sliderIsBeingDragged) {
                updateSlider()
            } else {
                isUpdatingSlider = false
                Log.d("ProgressTracker", "Stopped updating NowPlaying-Slider {${!isInBackground}, ${playerViewModel.isPlaying}, ${!binding.chapterSlider.isInTouchMode}}")
            }
        }
    }, 100)

    /**
     * Updates components that dont need a smooth value increase
     */
    private fun updateDynamicUI(): Boolean = handler.postDelayed({
        //Checks if Fragment is visible
        if (_binding != null) {
            val totalProgress = playerViewModel.getBookProgress()
            binding.totalBookProgress.setProgress(totalProgress, false) //Dont animate, wont be visible anyways

            val bookPos = Conversion.millisToStr(playerViewModel.getCurrentPositionInBook())
            val chapterPos = Conversion.millisToStr(playerViewModel.getCurrentPositionInChapter())

            binding.currentChapterProgressText.text = chapterPos
            binding.totalCurrentProgressText.text = bookPos

            if (!isInBackground && playerViewModel.isPlaying) {
                updateDynamicUI()
            } else {
                isUpdatingDynamicUI = false
                Log.d("ProgressTracker", "Stopped updating Now-Playing-UI")
            }
        }
    }, 500)

    // Called when Fragment reaches foreground
    override fun onResume() {
        super.onResume()
        isInBackground = false
        getBackInSyncWithSession()
        playerViewModel.controllerCreated.observe(viewLifecycleOwner) {
            if (it) {
                playerViewModel.addListener(nowPlayingPlayerListener)
                Log.d("AppLifecycle", "NowPlaying listener added!")
            }
        }
        Log.d("AppLifecycle", "NowPlayingFragment resumed!")
    }


    override fun onPause() {
        super.onPause()
        isInBackground = true
        playerViewModel.removeListener(nowPlayingPlayerListener)
        Log.d("AppLifecycle", "NowPlayingFragment paused!")
    }

    private fun onSkipChapter(){
        playerViewModel.skipChapter()
    }

    private fun onGoBackChapter(){
        playerViewModel.goBackChapter()
    }

    private fun onForward(ms: Long){
        playerViewModel.forward(ms)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setSleepTimer(time: Long){
        Toast.makeText(requireContext(), "SleepTimer set for ${Conversion.millisToExtendedStr(time)}", Toast.LENGTH_LONG).show()

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)

        binding.bottomAppBar.menu.findItem(R.id.sleep_timer_button).icon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_moon).setDrawableColor(
                ContextCompat.getColor(requireContext(), typedValue.resourceId)
            )

        val am = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time,
        PendingIntent.getService(requireActivity(), 0,
            Intent(requireActivity(), PlaybackService::class.java).setAction(PlaybackService.ACTION_QUIT)
            , PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE, ))

    }
}