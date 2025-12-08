package com.example.inventrix.UI.Admin.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.inventrix.R

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        // Tampilkan MenuFragment hanya sekali
        if (supportFragmentManager.findFragmentById(R.id.container_main_menu) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_main_menu, MenuFragment())
                .commit()
        }
    }
}
