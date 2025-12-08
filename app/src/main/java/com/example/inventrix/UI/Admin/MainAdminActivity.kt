package com.example.inventrix.UI.Admin

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.inventrix.R
import com.example.inventrix.databinding.ActivityMainAdminBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main_admin)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_keranjang,
                R.id.navigation_home
            )
        )
        navView.setupWithNavController(navController)

        // Tambahan opsional: sembunyikan bottom nav saat keyboard muncul
        handleKeyboardVisibility(navView)
    }

    private fun handleKeyboardVisibility(navView: BottomNavigationView) {
        val rootView = window.decorView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            // Jika keyboard muncul (>15% tinggi layar)
            if (keypadHeight > screenHeight * 0.15) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
    }
}
