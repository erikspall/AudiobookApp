package de.erikspall.audiobookapp.ui.now_playing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.erikspall.audiobookapp.databinding.FragmentPlayerBinding

class NowPlayingFragment : Fragment(){
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun setupObservers(){

    }
}