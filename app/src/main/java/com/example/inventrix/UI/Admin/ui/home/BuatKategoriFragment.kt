package com.example.inventrix.UI.Admin.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inventrix.Model.KategoriData
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuatKategoriFragment : Fragment() {

    private var editId: Int? = null

    private lateinit var etNama: EditText
    private lateinit var etKode: EditText
    private lateinit var btnSimpan: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_buat_kategori, container, false)

        etNama = view.findViewById(R.id.etNamaKategori)
        etKode = view.findViewById(R.id.etKodeKategori)
        btnSimpan = view.findViewById(R.id.btnSimpan)

        // Tombol Back
        view.findViewById<View>(R.id.btnback).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Ambil ID edit (jika ada)
        editId = arguments?.getInt("id")

        if (editId != null) {
            // === MODE EDIT ===
            btnSimpan.text = "Perbarui"
            etKode.isEnabled = false     // kode tidak bisa diubah
            loadDetailKategori(editId!!) // isi otomatis
        } else {
            // === MODE TAMBAH BARU ===
            btnSimpan.text = "Simpan"
            etKode.isEnabled = true
            etNama.setText("")
            etKode.setText("")
        }

        btnSimpan.setOnClickListener {
            if (editId == null) simpanKategoriBaru()
            else updateKategori(editId!!)
        }

        return view
    }

    // ===============================
    //  LOAD DETAIL KATEGORI SAAT EDIT
    // ===============================
    private fun loadDetailKategori(id: Int) {

        ApiClinet.instance.getKategoriDetail(id)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    val body = response.body() ?: return

                    // Ambil objek "data" dari response
                    val data = body["data"] as? Map<*, *> ?: return

                    val nama = data["nama"] as? String ?: ""
                    val kode = data["kodeAwal"] as? String ?: ""

                    // TAMPILKAN DI FORM
                    etNama.setText(nama)
                    etKode.setText(kode)

                    etKode.isEnabled = false
                    etKode.isFocusable = false
                    etKode.isClickable = false
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }


    // ===============================
    //  SIMPAN KATEGORI BARU
    // ===============================
    private fun simpanKategoriBaru() {
        val nama = etNama.text.toString().trim()
        val kode = etKode.text.toString().trim().uppercase()

        if (nama.isEmpty() || kode.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (kode.length != 3) {
            Toast.makeText(requireContext(), "Kode harus 3 huruf", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClinet.instance.tambahKategori(nama, kode)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Gagal menambah kategori", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ===============================
    //  UPDATE KATEGORI (kode tidak berubah)
    // ===============================
    private fun updateKategori(id: Int) {
        val nama = etNama.text.toString().trim()

        if (nama.isEmpty()) {
            Toast.makeText(requireContext(), "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClinet.instance.updateKategori(id, nama, null)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Kategori diperbarui", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Gagal memperbarui kategori", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
