package com.example.inventrix.UI

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.inventrix.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Hanya tampilkan LoginFragment
        if (supportFragmentManager.findFragmentByTag("LOGIN") == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, LoginFragment(), "LOGIN")
                .commit()
        }
    }
}