package de.erikspall.audiobookapp.ui.library


import android.os.Bundle
import android.view.*
import android.widget.*

import androidx.fragment.app.Fragment

import androidx.lifecycle.ViewModelProvider

import com.google.android.material.chip.Chip
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.adapter.AudioBookCardAdapter
import de.erikspall.audiobookapp.databinding.FragmentLibraryBinding
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.core.view.get
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LibraryFragment : Fragment() {

    private lateinit var libraryViewModel: LibraryViewModel
    private var _binding: FragmentLibraryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        libraryViewModel =
            ViewModelProvider(this).get(LibraryViewModel::class.java)

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.gridRecyclerView.adapter = AudioBookCardAdapter(
            this.requireContext()
        )



        // Specify fixed size to improve performance
        binding.gridRecyclerView.setHasFixedSize(true)

        val chip = Chip(this.context)
        chip.text = "Mark-Uwe-Kling"
        chip.isCheckable = true
        chip.isClickable = true

        binding.libraryChipGroup.addView(chip)





        //TODO: Probably not good practice
        binding.libraryToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_search -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setIcon(R.drawable.ic_search)
                        .setTitle(getString(R.string.input_title))
                        .setView(R.layout.dialog_edit_text)
                        .setNeutralButton(getString(R.string.input_cancel)) { dialog, which ->
                            // Do nothing
                        }
                        .setPositiveButton(getString(R.string.input_accept)) { dialog, which ->
                            /*
                            if (textInputEditText.text.toString() == "") {

                            } else {
                                enterSearchState(textInputEditText.text.toString())
                            }*/
                        }

                        .show()
                    true
                }
                R.id.menu_add -> {
                    // Handle delete icon press
                    true
                }
                else -> false
            }
        }

        binding.libraryToolbar.setNavigationOnClickListener {
            Toast.makeText(requireContext(), "Exit search", Toast.LENGTH_LONG).show()
            leaveSearchState()
        }


        return root
    }

    fun enterSearchState(searchText:String){
        binding.libraryToolbar.setNavigationIcon(R.drawable.ic_close)

        //TODO: maybe replace with group?
        binding.libraryToolbar.menu.getItem(0).isVisible = false //Search
        binding.libraryToolbar.menu.getItem(1).isVisible = false //Add

        //TODO: Search stuff here

        binding.libraryCollapsingtoolbarlayout.title = "\"" + searchText + "\""
    }

    fun leaveSearchState(){
        binding.libraryToolbar.navigationIcon = null
        binding.libraryToolbar.menu.getItem(0).isVisible = true //Search
        binding.libraryToolbar.menu.getItem(1).isVisible = true //Add

        //TODO: Restore here

        binding.libraryCollapsingtoolbarlayout.title = getString(R.string.title_library)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
