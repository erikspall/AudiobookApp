package de.erikspall.audiobookapp.ui.library

import android.Manifest
import android.content.ComponentName
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
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import de.erikspall.audiobookapp.AudioBookApp
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.handling.import.Importer
import de.erikspall.audiobookapp.data.viewmodels.DatabaseViewModel
import de.erikspall.audiobookapp.data.viewmodels.DatabaseViewModelFactory
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding
import de.erikspall.audiobookapp.uamp.PlaybackService
import de.erikspall.audiobookapp.ui.bottom_sheets.ModalBottomSheet
import de.erikspall.audiobookapp.utils.Conversion
import kotlinx.coroutines.launch

@androidx.media3.common.util.UnstableApi
class LibraryFragment : Fragment() {
    //private lateinit var browserFuture: ListenableFuture<MediaBrowser>
   // private val browser: MediaBrowser?
    //    get() = if (browserFuture.isDone) browserFuture.get() else null
    //private val treePathStack: ArrayDeque<MediaItem> = ArrayDeque()

    private var _binding: FragmentLibraryBinding? = null
    private var isGridLayout = true
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())

    //TODO: JUST TESTING
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

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

        databaseViewModel.allAudiobooksWithAuthor.observe(viewLifecycleOwner) {audiobooks ->
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
                    databaseViewModel.viewModelScope.launch {
                        //initializeBrowser()
                    }
                    initializeController()
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

                    databaseViewModel.viewModelScope.launch {
                        Importer.createLocalImporter(requireContext()).getAllAsync()

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
        binding.miniPlayer.playButton.setOnClickListener {
            if (controller != null) {
                if (controller!!.isPlaying)
                    controller!!.pause()
                else {
                    controller!!.play()
                    updatePlaybackProgress()
                }
            }
        }
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
        databaseViewModel.allAudiobooksWithAuthor.observe(viewLifecycleOwner) {audiobooks ->
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

    private fun initializeController() {
        controllerFuture = MediaController.Builder(
            requireContext(),
            SessionToken(requireContext(), ComponentName(requireActivity(), PlaybackService::class.java))
        ).buildAsync()

        // Listener executes when Controller is finished
        controllerFuture.addListener(
            { setConrtoller() },
            MoreExecutors.directExecutor()
        )
    }

    private fun setConrtoller() {
        val controller = this.controller ?: return

        updateMiniPlayerUI(controller.mediaMetadata)
    }

    private fun updateMiniPlayerUI(mediaMetadata: MediaMetadata) {
        Glide.with(requireContext())
            .load(mediaMetadata.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.ic_image)
            .into(binding.miniPlayer.currentBookImage)
        binding.miniPlayer.currentBookTitle.text = mediaMetadata.albumTitle
        binding.miniPlayer.currentBookChapter.text = mediaMetadata.title
    }

    private fun updatePlaybackProgress(): Boolean = handler.postDelayed({
        if (controller != null) {
            val progress = ((controller!!.currentPosition.toDouble()/controller!!.duration)*100).toInt()

            //Ensure that progressbar is always visible
            if (progress <= 1)
                binding.miniPlayer.currentBookProgress.progress = 1
            else
                binding.miniPlayer.currentBookProgress.progress = progress

            Log.d("Progress", "$progress%")
            Log.d("PlayerStats", "CurrentPos: " + (controller!!.currentPosition).toString() + "ms")
            Log.d("PlayerStats", "Duration: " + (controller!!.duration).toString() + "ms")
            if (controller!!.isPlaying) {
                updatePlaybackProgress()
            }
        }
    }, 1000)

    /* // TESTING
     private fun initializeBrowser() {
         browserFuture =
             MediaBrowser.Builder(
                 requireContext(),
                 SessionToken(requireContext(), ComponentName(requireActivity(), PlaybackService::class.java))
             )
                 .buildAsync()
         browserFuture.addListener({ pushRoot() }, MoreExecutors.directExecutor())
     }

     private fun releaseBrowser() {
         MediaBrowser.releaseFuture(browserFuture)
     }

     private fun pushRoot() {
         // browser can be initialized many times
         // only push root at the first initialization
         if (!treePathStack.isEmpty()) {
             return
         }
         val browser = this.browser ?: return
         val rootFuture = browser.getLibraryRoot(/* params= */ null)
         rootFuture.addListener(
             {
                 val result: LibraryResult<MediaItem> = rootFuture.get()!!
                 val root: MediaItem = result.value!!
                 pushPathStack(root)
             },
             MoreExecutors.directExecutor()
         )
     }

     override fun onStart() {
         super.onStart()
         initializeController()
     }

     private fun initializeController() {
         controllerFuture =
             MediaController.Builder(
                 requireContext(),
                 SessionToken(requireContext(), ComponentName(requireContext(), PlaybackService::class.java))
             )
                 .buildAsync()
         controllerFuture.addListener({ setConrtoller() }, MoreExecutors.directExecutor())
     }

     private fun setConrtoller() {
         val controller = this.controller ?: return

     }

     private fun pushPathStack(mediaItem: MediaItem) {
         treePathStack.addLast(mediaItem)
         //displayChildrenList(treePathStack.last())
     }*/
}
