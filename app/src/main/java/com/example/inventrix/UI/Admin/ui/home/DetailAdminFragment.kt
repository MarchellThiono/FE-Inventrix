package com.example.inventrix.UI.Admin.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.inventrix.Model.EditData
import com.example.inventrix.Model.ResEditBarang
import com.example.inventrix.Model.ResPesan
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentDetailAdminBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAdminFragment : Fragment() {

    private var _binding: FragmentDetailAdminBinding? = null
    private val binding get() = _binding!!

    private var barangId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailAdminBinding.inflate(inflater, container, false)

        ambilIdDariBundle()
        loadDetailBarang()
        setupListeners()

        return binding.root
    }

    private fun ambilIdDariBundle() {
        barangId = arguments?.getInt("id") ?: -1

        if (barangId == -1) {
            Toast.makeText(requireContext(), "ID barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadDetailBarang() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getDetailBarang(barangId)
            .enqueue(object : Callback<ResEditBarang> {
                override fun onResponse(
                    call: Call<ResEditBarang>,
                    response: Response<ResEditBarang>
                ) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body()?.data != null) {
                        tampilkanData(response.body()!!.data!!)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal memuat detail barang",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResEditBarang>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun tampilkanData(data: EditData) {
        Glide.with(this)
            .load(data.imageUrl)
            .into(binding.ivLogo)

        binding.kodeBarang.text = "Kode Barang : ${data.kodeBarang}"
        binding.nameBarang.text = "Nama Barang : ${data.namaBarang}"

        binding.tvKategori.text = "Kategori : ${data.kategori}"      // ✔ TAMBAHKAN
        binding.tvMerek.text = "Merek : ${data.merek?.namaMerek}"

        binding.tvHargaBeli.text = "Harga Beli : Rp ${data.hargaBeli ?: "0"}"
        binding.tvHargaJual.text = "Harga Jual : Rp ${data.hargaJual ?: "0"}"

        binding.tvStokToko.text = "Stok Toko : ${data.stokToko}"
        binding.tvStokGudang.text = "Stok Gudang : ${data.stokGudang}"

        binding.tvStokMinimum.text = "Stok Minimum : ${data.stokMinimum}"  // ✔ TAMBAHKAN

        binding.tvDescription.text = data.deskripsi ?: "-"
    }


    private fun setupListeners() {

        // Kembali
        binding.menuBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Tombol hapus barang
        binding.rvhapus.setOnClickListener { showDialogHapus() }
        binding.rvhapusbarang.setOnClickListener { showDialogHapus() }
    }

    // ============================================
    //              DIALOG HAPUS BARANG
    // ============================================
    private fun showDialogHapus() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Barang")
            .setMessage("Apakah Anda yakin ingin menghapus barang ini?\nTindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Ya") { _, _ ->
                hapusBarang()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusBarang() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.hapusBarang(barangId)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            response.body()?.pesan ?: "Barang berhasil dihapus",
                            Toast.LENGTH_LONG
                        ).show()

                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Gagal menghapus barang",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
