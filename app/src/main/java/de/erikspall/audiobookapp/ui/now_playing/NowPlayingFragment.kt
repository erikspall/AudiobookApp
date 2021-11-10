package de.erikspall.audiobookapp.ui.now_playing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.erikspall.audiobookapp.databinding.FragmentNowPlayingBinding

class NowPlayingFragment : Fragment() {

    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private var _binding: FragmentNowPlayingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        nowPlayingViewModel =
            ViewModelProvider(this).get(NowPlayingViewModel::class.java)

        _binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}