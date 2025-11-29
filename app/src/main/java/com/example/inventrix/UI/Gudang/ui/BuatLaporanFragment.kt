package com.example.inventrix.UI.Gudang.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListLaporanBarang
import com.example.inventrix.Model.BarangDipilihGudang
import com.example.inventrix.Model.ReqCreateLaporan
import com.example.inventrix.Model.ReqLaporanItem
import com.example.inventrix.Model.ResPesan
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentBuatLaporanBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuatLaporanFragment : Fragment() {

    private var _binding: FragmentBuatLaporanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListLaporanBarang
    private lateinit var barangDipilih: List<BarangDipilihGudang>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuatLaporanBinding.inflate(inflater, container, false)

        ambilDataDariBundle()
        setupRecyclerView()
        setupSpinner()
        setupListeners()

        return binding.root
    }

    private fun ambilDataDariBundle() {
        val mapSelected =
            arguments?.getSerializable("selected_barang") as? HashMap<Int, Int>

        if (mapSelected == null || mapSelected.isEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada barang dipilih", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        barangDipilih = mapSelected.map { (idBarang, jumlah) ->
            BarangDipilihGudang(
                barangId = idBarang,
                nama = "Barang $idBarang",
                merek = "",
                kodeBarang = "BRG$idBarang",
                stokGudang = jumlah       // jumlah klik dari Home
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = ListLaporanBarang(barangDipilih)
        binding.rvBarangDipilih.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarangDipilih.adapter = adapter
    }

    private fun setupSpinner() {
        val jenisList = listOf("Barang Masuk", "Barang Hilang", "Barang Rusak")

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            jenisList
        )
        binding.spinnerJenisLaporan.adapter = spinnerAdapter

        binding.spinnerJenisLaporan.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    binding.layoutSupplier.visibility =
                        if (position == 0) View.VISIBLE else View.GONE
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupListeners() {
        binding.icBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnKonfirmasi.setOnClickListener {
            submitLaporan()
        }
    }

    private fun submitLaporan() {
        val jenisText = binding.spinnerJenisLaporan.selectedItem.toString()
        val jenis = when (jenisText) {
            "Barang Masuk" -> "MASUK"
            "Barang Hilang" -> "HILANG"
            "Barang Rusak" -> "RUSAK"
            else -> "MASUK"
        }

        val supplier =
            if (jenis == "MASUK") binding.etSupplier.text.toString().takeIf { it.isNotBlank() }
            else null

        if (jenis == "MASUK" && supplier.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Supplier wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val items = adapter.getListJumlah()
            .filter { it.jumlah > 0 }
            .map { ReqLaporanItem(it.barangId, it.jumlah, null) }

        if (items.isEmpty()) {
            Toast.makeText(requireContext(), "Minimal 1 barang harus diisi jumlahnya", Toast.LENGTH_SHORT).show()
            return
        }

        val body = ReqCreateLaporan(
            jenis = jenis,
            supplier = supplier,
            items = items
        )

        ApiClinet.instance.createLaporan(body).enqueue(object : Callback<ResPesan> {
            override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), response.body()?.pesan ?: "Berhasil", Toast.LENGTH_LONG).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(requireContext(), "Gagal mengirim laporan", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
