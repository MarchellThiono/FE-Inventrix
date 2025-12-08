package com.example.inventrix.UI.Admin.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.inventrix.R
import com.example.inventrix.UI.Admin.MainAdminActivity
import com.example.inventrix.UI.Admin.ui.home.KelolaDataBarangFragment
import com.example.inventrix.UI.Admin.ui.home.NotifikasiFragment
import com.example.inventrix.UI.Admin.ui.riwayat.RiwayatFragment
import com.example.inventrix.UI.MainActivity

class MenuFragment : Fragment() {

    private lateinit var menuLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, containerParent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, containerParent, false)

        menuLayout = view.findViewById(R.id.menuLayout)

        val menuKelola = view.findViewById<View>(R.id.menuKelola)
        val menuNotif = view.findViewById<View>(R.id.notif)
        val menuStokToko = view.findViewById<View>(R.id.menuStokToko)
        val menuKasir = view.findViewById<View>(R.id.menuKasir)
        val logout = view.findViewById<View>(R.id.logout)
        val menuLaporan = view.findViewById<View>(R.id.menuLaporan)

        menuKelola.setOnClickListener { openFragment(KelolaDataBarangFragment()) }
        menuNotif.setOnClickListener { openFragment(NotifikasiFragment()) }

        menuKasir.setOnClickListener {
            startActivity(Intent(requireContext(), MainAdminActivity::class.java))
        }

        menuStokToko.setOnClickListener {
            startActivity(Intent(requireContext(), MainStokTokoActivity::class.java))
        }



        menuLaporan.setOnClickListener {
            openFragment(RiwayatFragment())
        }

        logout.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun openFragment(fragment: Fragment) {
        menuLayout.visibility = View.GONE

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container_main_menu, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity().supportFragmentManager.backStackEntryCount == 0) {
            menuLayout.visibility = View.VISIBLE
        }
    }

    private fun showLogoutDialog() {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda ingin keluar dari aplikasi?")
            .setPositiveButton("Ya") { _, _ ->

                val prefs = requireContext().getSharedPreferences("APP_PREF", android.content.Context.MODE_PRIVATE)
                prefs.edit().clear().apply()

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)

                requireActivity().finish()
            }
            .setNegativeButton("Tidak") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
    }
}
