package de.erikspall.audiobookapp.ui.now_playing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import de.erikspall.audiobookapp.databinding.FragmentPlayerBinding
import de.erikspall.audiobookapp.viewmodels.AppViewModel

class NowPlayingFragment: Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: AppViewModel by activityViewModels()

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

        Log.d("FragmentStuff", "NowPlayingFragment created/re-created!")
        Log.d("FragmentStuff", "<!-- Values here -->")

        ViewCompat.setOnApplyWindowInsetsListener(binding.nowPlayingLayout){ view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams>{
                topMargin = insets.top
            }
            windowInsets
        }

        return root
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("FragmentStuff", "NowPlayingFragment destroyed!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}