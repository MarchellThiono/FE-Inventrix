package com.example.inventrix.UI.Gudang

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.inventrix.R
import com.example.inventrix.databinding.ActivityMainGudangBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityGudang : AppCompatActivity() {

    private lateinit var binding: ActivityMainGudangBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainGudangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main_gudang)

        // 🔹 Top-level destinations (tanpa ActionBar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_beranda,
                R.id.navigation_permintaan
            )
        )

        navView.setupWithNavController(navController)

        // 🔹 Tambahan opsional: sembunyikan bottom nav saat keyboard muncul
        handleKeyboardVisibility(navView)
    }

    // Fungsi bantu untuk sembunyikan BottomNavigationView saat keyboard muncul
    private fun handleKeyboardVisibility(navView: BottomNavigationView) {
        val rootView = window.decorView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            // Jika keyboard muncul (>15% dari tinggi layar)
            if (keypadHeight > screenHeight * 0.15) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
    }
}
