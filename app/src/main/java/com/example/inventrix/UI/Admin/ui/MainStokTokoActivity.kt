package com.example.inventrix.UI.Admin.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.inventrix.R
import com.example.inventrix.UI.Admin.ui.home.HomeStokTokoFragment
import com.example.inventrix.UI.Admin.ui.aktivitas.AktivitasFragment

class MainStokTokoActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navAktivitas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_stok_toko)

        navHome = findViewById(R.id.navHome)
        navAktivitas = findViewById(R.id.navActivitas)

        if (savedInstanceState == null) {
            setActive(navHome)
            openFragment(HomeStokTokoFragment())
        }

        navHome.setOnClickListener {
            setActive(navHome)
            openFragment(HomeStokTokoFragment())
        }

        navAktivitas.setOnClickListener {
            setActive(navAktivitas)
            openFragment(AktivitasFragment())
        }
    }

    private fun setActive(menu: LinearLayout) {
        navHome.alpha = 0.5f
        navAktivitas.alpha = 0.5f
        menu.alpha = 1f
    }

    fun openFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val tx = supportFragmentManager.beginTransaction()
            .replace(R.id.containerStokToko, fragment)

        if (addToBackStack) tx.addToBackStack(null)

        tx.commit()
    }
}
