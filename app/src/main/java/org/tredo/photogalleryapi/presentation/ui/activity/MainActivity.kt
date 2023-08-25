package org.tredo.photogalleryapi.presentation.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.tredo.photogalleryapi.R
import org.tredo.photogalleryapi.databinding.ActivityMainBinding
import org.tredo.photogalleryapi.presentation.ui.utils.ConnectivityStatus

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)

    private val connectivityStatus: ConnectivityStatus by lazy { ConnectivityStatus(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
        observeConnectivityStatus()
    }

    private fun observeConnectivityStatus() = with(binding) {
        connectivityStatus.observe(this@MainActivity) { isConnected ->
            if (!isConnected) {
                imNoInternet.visibility = View.VISIBLE
                tvOhShucks.visibility = View.VISIBLE
                tvOhShucksDesc.visibility = View.VISIBLE
            } else {
                imNoInternet.visibility = View.GONE
                tvOhShucks.visibility = View.GONE
                tvOhShucksDesc.visibility = View.GONE
            }
        }
    }

    private fun setupNavigation() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)

        if (fragment is NavHostFragment) {
            val navController = fragment.navController
            setupWithNavController(binding.bottomNav, navController)
        } else {
            Toast.makeText(this, getString(R.string.navigation_error), Toast.LENGTH_SHORT)
                .show()
        }
    }
}