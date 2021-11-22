package de.erikspall.audiobookapp


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.core.view.ViewCompat.requestApplyInsets
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import de.erikspall.audiobookapp.databinding.ActivityMainBinding
import de.erikspall.audiobookapp.ui.library.LibraryFragmentDirections

/**
 * Main Activity and entry point for the app. Displays a RecyclerView of audiobooks.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController




        //setupActionBarWithNavController(navController)

        //supportActionBar?.hide()
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}