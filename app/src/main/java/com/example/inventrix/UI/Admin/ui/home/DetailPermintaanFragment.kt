package com.example.inventrix.UI.Admin.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPermintaanAdmin
import com.example.inventrix.Model.BarangDipilihAdmin
import com.example.inventrix.Model.ResDetailPermintaan
import com.example.inventrix.Model.ResPesan
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentDetailPermintaanBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPermintaanFragment : Fragment() {

    private var _binding: FragmentDetailPermintaanBinding? = null
    private val binding get() = _binding!!

    private var permintaanId: Int = 0
    private var userRole: String = ""   // OWNER / WAREHOUSE / TOKO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPermintaanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        permintaanId = arguments?.getInt("permintaanId", 0) ?: 0
        userRole = arguments?.getString("role", "") ?: ""

        if (permintaanId == 0) {
            Toast.makeText(requireContext(), "ID permintaan tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        binding.menuBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        loadDetailPermintaan()
    }

    private fun loadDetailPermintaan() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getDetailPermintaan(permintaanId)
            .enqueue(object : Callback<ResDetailPermintaan> {
                override fun onResponse(
                    call: Call<ResDetailPermintaan>,
                    response: Response<ResDetailPermintaan>
                ) {
                    binding.progressBar.visibility = View.GONE

                    val data = response.body()
                    if (data == null) {
                        Toast.makeText(requireContext(), "Data kosong", Toast.LENGTH_SHORT).show()
                        return
                    }

                    tampilkanDetail(data)
                }

                override fun onFailure(call: Call<ResDetailPermintaan>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun tampilkanDetail(detail: ResDetailPermintaan) {

        val created = detail.createdAt ?: "-"
        binding.tgl.text = created.substring(0, 10)
        binding.tvwaktu.text = created.substring(11, 19)

        val status = detail.status.value?.uppercase() ?: "-"
        binding.rvJenis.text = status

        // LIST BARANG
        val listBarang = detail.items.map {
            BarangDipilihAdmin(
                barangId = it.barangId ?: 0,
                nama = it.namaBarang ?: "-",
                kodeBarang = it.kodeBarang ?: "-",
                merek = "",
                stokToko = it.jumlah ?: 0
            )
        }

        binding.rvPermintaanbarang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPermintaanbarang.adapter =
            ListPermintaanAdmin(listBarang, isReadOnly = true)

        // Atur tombol sesuai role
        setupButtonByRole(status)
    }

    // =============================
    // TAMPILKAN TOMBOL SESUAI ROLE
    // =============================
    private fun setupButtonByRole(status: String) {

        val st = status.uppercase().trim()
        val role = userRole.uppercase().trim()

        binding.konfirmasi.visibility = View.GONE

        when (role) {

            "GUDANG", "WAREHOUSE" -> {
                if (st == "DIMINTA") {
                    binding.konfirmasi.visibility = View.VISIBLE
                    binding.konfirmasi.text = "KIRIM BARANG KE TOKO"
                    binding.konfirmasi.setOnClickListener { confirmKirimBarang() }
                }
            }

            "OWNER", "ADMIN" -> {
                if (st == "DIPROSES") {
                    binding.konfirmasi.visibility = View.VISIBLE
                    binding.konfirmasi.text = "BARANG SUDAH SAMPAI?"
                    binding.konfirmasi.setOnClickListener { selesaikanPermintaan() }
                }
            }

            else -> {
                binding.konfirmasi.visibility = View.GONE
            }
        }
    }


    // =======================
    // GUDANG KIRIM BARANG
    // =======================
    private fun confirmKirimBarang() {
        AlertDialog.Builder(requireContext())
            .setTitle("Kirim Barang?")
            .setMessage("Konfirmasi gudang ingin mengirim barang ke toko.")
            .setPositiveButton("Kirim") { _, _ -> kirimBarangKeToko() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun kirimBarangKeToko() {
        ApiClinet.instance.kirimBarang(permintaanId)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                    Toast.makeText(requireContext(), "Barang dikirim ke toko", Toast.LENGTH_SHORT).show()

                    requireActivity().setResult(AppCompatActivity.RESULT_OK)
                    requireActivity().finish()
                }

                override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // =======================
    // OWNER SELESAIKAN
    // =======================
    private fun selesaikanPermintaan() {
        ApiClinet.instance.konfirmasiPermintaanSelesai(permintaanId)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                    Toast.makeText(requireContext(), "Permintaan selesai", Toast.LENGTH_SHORT).show()

                    requireActivity().setResult(AppCompatActivity.RESULT_OK)
                    requireActivity().finish()
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
