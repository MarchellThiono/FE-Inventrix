package com.example.inventrix.UI.Admin.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListKelolaKategori
import com.example.inventrix.Model.KategoriData
import com.example.inventrix.Model.ResKategoriList
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KelolaKategoriFragment : Fragment() {

    private lateinit var adapter: ListKelolaKategori

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_kelola_kategori, container, false)

        // === SETUP RECYCLER ===
        adapter = ListKelolaKategori(
            mutableListOf(),
            onEdit = { kategori -> bukaEditKategori(kategori) },
            onDelete = { kategori -> showDeleteDialog(kategori) }   // ← PENTING! panggil alert dulu
        )


        val rv = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvKategori)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // === BTN TAMBAH ===
        val btnTambah = view.findViewById<View>(R.id.btnTambahKategori)
        btnTambah.setOnClickListener {
            val frag = BuatKategoriFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main_menu, frag)
                .addToBackStack(null)
                .commit()
        }

        // === BTN BACK ===
        view.findViewById<View>(R.id.btnback).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        loadKategori()

        return view
    }

    private fun loadKategori() {
        ApiClinet.instance.getKategoriList()
            .enqueue(object : Callback<ResKategoriList> {
                override fun onResponse(
                    call: Call<ResKategoriList>,
                    response: Response<ResKategoriList>
                ) {
                    val data = response.body()?.data?.filterNotNull() ?: emptyList()
                    adapter.setData(data)
                }

                override fun onFailure(call: Call<ResKategoriList>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal memuat kategori", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun bukaEditKategori(kategori: KategoriData) {
        val frag = BuatKategoriFragment()
        frag.arguments = Bundle().apply {
            putInt("id", kategori.id)
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container_main_menu, frag)
            .addToBackStack(null)
            .commit()
    }

    private fun showDeleteDialog(kategori: KategoriData) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Kategori")
            .setMessage("Yakin ingin menghapus kategori \"${kategori.nama}\"?")
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Hapus") { dialog, _ ->
                hapusKategori(kategori)
                dialog.dismiss()
            }
            .show()
    }


    private fun hapusKategori(kategori: KategoriData) {

        ApiClinet.instance.deleteKategori(kategori.id)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        adapter.removeItem(kategori.id)
                        Toast.makeText(requireContext(), "Kategori dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal menghapus kategori", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}

