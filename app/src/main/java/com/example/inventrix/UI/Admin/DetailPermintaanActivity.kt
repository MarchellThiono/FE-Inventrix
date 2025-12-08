package com.example.inventrix.UI.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.inventrix.R
import com.example.inventrix.UI.Admin.ui.home.DetailPermintaanFragment

class DetailPermintaanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_permintaan)

        val id = intent.getIntExtra("permintaanId", 0)
        val role = intent.getStringExtra("role") ?: "ADMIN"

        val fragment = DetailPermintaanFragment().apply {
            arguments = Bundle().apply {
                putInt("permintaanId", id)
                putString("role", role)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_detail, fragment)
            .commit()
    }
}
