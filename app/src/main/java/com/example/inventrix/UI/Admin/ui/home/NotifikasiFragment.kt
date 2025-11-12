package com.example.inventrix.UI.Admin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPemberitahuan
import com.example.inventrix.Adapter.ListPeringatan
import com.example.inventrix.Model.Peringatan
import com.example.inventrix.Model.ReqPemberitahuan
import com.example.inventrix.R

class NotifikasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifikasi, container, false)

        // 🔹 Tombol back
        val btnBack = view.findViewById<android.widget.ImageView>(R.id.btnback)
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // 🔹 Data dummy
        val listPeringatan = listOf(
            Peringatan("Kipas Angin", "BRG001", 10, "10-05-2025", "12:20:05"),
            Peringatan("Setrika", "BRG002", 5, "10-05-2025", "13:10:20")
        )

        val listPemberitahuan = listOf(
            ReqPemberitahuan("Barang dari gudang sedang dikirim ke toko", "10-05-2025", "12:22:05"),
            ReqPemberitahuan("Barang pengganti telah diterima oleh toko", "11-05-2025", "09:05:50")
        )

        // 🔹 Setup RecyclerView Peringatan
        val rvPeringatan = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPeringatan)
        rvPeringatan.layoutManager = LinearLayoutManager(requireContext())
        rvPeringatan.adapter = ListPeringatan(listPeringatan)

        // 🔹 Setup RecyclerView Pemberitahuan
        val rvPemberitahuan = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPemberitahuan)
        rvPemberitahuan.layoutManager = LinearLayoutManager(requireContext())
        rvPemberitahuan.adapter = ListPemberitahuan(listPemberitahuan)

        return view
    }
}
