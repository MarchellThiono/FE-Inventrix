package com.example.inventrix.UI.Admin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inventrix.databinding.FragmentLaporanBarangAdminBinding
import com.example.inventrix.Model.*
import com.example.inventrix.Server.ApiClinet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LaporanBarangAdminFragment : Fragment() {

    private var _binding: FragmentLaporanBarangAdminBinding? = null
    private val binding get() = _binding!!

    private var barangId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaporanBarangAdminBinding.inflate(inflater, container, false)

        ambilIdBarang()
        setTanggal()
        loadDetailBarang()
        setupListeners()

        return binding.root
    }

    private fun ambilIdBarang() {
        barangId = arguments?.getInt("idBarang") ?: -1
        if (barangId == -1) {
            Toast.makeText(requireContext(), "ID barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setTanggal() {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val tanggal = sdf.format(Date())
        binding.rvTanggal.text = "Tanggal : $tanggal"
    }

    private fun loadDetailBarang() {
        ApiClinet.instance.getDetailBarang(barangId)
            .enqueue(object : Callback<ResEditBarang> {
                override fun onResponse(call: Call<ResEditBarang>, response: Response<ResEditBarang>) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        val data = response.body()!!.data!!
                        binding.KodeBarang.text = "Kode Barang : ${data.kodeBarang}"
                        binding.NameBarang.text = "Nama Barang : ${data.namaBarang}"
                        binding.tvMerek.text = "Merek : ${data.merek?.namaMerek}"
                        binding.tvLokasi.text = "Lokasi : Toko"
                    } else {
                        Toast.makeText(requireContext(), "Gagal memuat data barang", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupListeners() {
        binding.menuBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rvKirim.setOnClickListener {
            kirimLaporan()
        }
    }

    private fun kirimLaporan() {
        val jenis = when {
            binding.rbBarangHilang.isChecked -> "HILANG"
            binding.rbBarangRusak.isChecked -> "RUSAK"
            else -> {
                Toast.makeText(requireContext(), "Pilih jenis laporan!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val jumlahText = binding.jumlahbarang.text.toString().trim()

        if (jumlahText.isEmpty()) {
            Toast.makeText(requireContext(), "Isi jumlah barang!", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlah = jumlahText.toIntOrNull()
        if (jumlah == null || jumlah <= 0) {
            Toast.makeText(requireContext(), "Jumlah tidak valid!", Toast.LENGTH_SHORT).show()
            return
        }


        val item = ReqLaporanItem(
            barangId = barangId,
            jumlah = jumlah,
            keterangan = "Laporan dari Admin"
        )

        val body = ReqCreateLaporan(
            jenis = jenis,
            supplier = null,
            items = listOf(item)
        )

        ApiClinet.instance.createLaporan(body)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), response.body()?.pesan ?: "Berhasil", Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        Toast.makeText(requireContext(), "Gagal membuat laporan", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
