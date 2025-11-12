package com.example.inventrix.UI.Gudang.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListLaporanBarang
import com.example.inventrix.Model.ReqPermintaanBarang
import com.example.inventrix.R
import com.example.inventrix.databinding.FragmentBuatLaporanBinding

class BuatLaporanFragment : Fragment() {

    private var _binding: FragmentBuatLaporanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListLaporanBarang

    // contoh data dummy (nanti bisa diganti dengan data dari fragment sebelumnya)
    private val barangDipilih = listOf(
        ReqPermintaanBarang("Kipas Angin", "Maspion", "BRG001", 10, null, "diminta"),
        ReqPermintaanBarang("Setrika", "LG", "BRG002", 5, null, "diminta"),
        ReqPermintaanBarang("Dispenser", "Miyako", "BRG003", 8, null, "diminta")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuatLaporanBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSpinner()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ListLaporanBarang(barangDipilih)
        binding.rvBarangDipilih.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarangDipilih.adapter = adapter
    }

    private fun setupSpinner() {
        val jenisLaporan = listOf("Barang Masuk", "Barang Hilang", "Barang Rusak")

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            jenisLaporan
        )
        binding.spinnerJenisLaporan.adapter = spinnerAdapter

        binding.spinnerJenisLaporan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = jenisLaporan[position]
                // tampilkan kolom supplier hanya jika Barang Masuk
                binding.layoutSupplier.visibility =
                    if (selected == "Barang Masuk") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupListeners() {
        binding.icBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnKonfirmasi.setOnClickListener {
            // TODO: Aksi konfirmasi laporan
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
