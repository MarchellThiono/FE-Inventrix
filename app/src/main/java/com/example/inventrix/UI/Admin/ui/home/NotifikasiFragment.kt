package com.example.inventrix.UI.Admin.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPemberitahuan
import com.example.inventrix.Adapter.ListPeringatan
import com.example.inventrix.Model.Notifikasi
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifikasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifikasi, container, false)

        val rvPeringatan = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPeringatan)
        val rvPemberitahuan = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPemberitahuan)

        loadNotifikasi(rvPeringatan, rvPemberitahuan)

        return view
    }

    private fun loadNotifikasi(
        rvPeringatan: androidx.recyclerview.widget.RecyclerView,
        rvPemberitahuan: androidx.recyclerview.widget.RecyclerView
    ) {
        val prefs = requireContext().getSharedPreferences("APP_PREF", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getLong("USER_ID", 0)

        if (userId == 0L) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClinet.instance.getNotifikasi(userId).enqueue(object : Callback<List<Notifikasi>> {
            override fun onResponse(call: Call<List<Notifikasi>>, response: Response<List<Notifikasi>>) {
                if (!response.isSuccessful) return

                val data = response.body() ?: emptyList()

                val peringatan = data.filter { it.tipe == "STOK_MINIM" }
                val info = data.filter { it.tipe != "STOK_MINIM" }

                rvPeringatan.layoutManager = LinearLayoutManager(requireContext())
                rvPeringatan.adapter = ListPeringatan(peringatan) { notif ->
                    confirmDelete(notif)
                }

                rvPemberitahuan.layoutManager = LinearLayoutManager(requireContext())
                rvPemberitahuan.adapter = ListPemberitahuan(info) { notif ->
                    confirmDelete(notif)
                }
            }

            override fun onFailure(call: Call<List<Notifikasi>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmDelete(notif: Notifikasi) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Notifikasi")
            .setMessage("Apakah Anda yakin ingin menghapus notifikasi ini?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteNotif(notif.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteNotif(id: Long) {
        ApiClinet.instance.deleteNotifikasi(id).enqueue(object : Callback<com.example.inventrix.Model.ResPesan> {
            override fun onResponse(call: Call<com.example.inventrix.Model.ResPesan>, response: Response<com.example.inventrix.Model.ResPesan>) {
                Toast.makeText(requireContext(), "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                requireActivity().recreate()
            }

            override fun onFailure(call: Call<com.example.inventrix.Model.ResPesan>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal menghapus", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
