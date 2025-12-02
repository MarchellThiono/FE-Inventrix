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
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun tampilkanData(data: EditData) {

        Glide.with(this)
            .load(data.imageUrl)
            .into(binding.ivLogo)

        binding.kodeBarang.text = "Kode Barang : ${data.kodeBarang}"
        binding.nameBarang.text = "Nama Barang : ${data.namaBarang}"
        binding.tvMerek.text = "Merek : ${data.merek?.namaMerek}"
        binding.tvHargaBeli.text = "Harga Beli : Rp ${data.hargaBeli ?: "0"}"
        binding.tvHargaJual.text = "Harga Jual : Rp ${data.hargaJual ?: "0"}"
        binding.tvStokToko.text = "Stok Toko : ${data.stokToko}"
        binding.tvStokGudang.text = "Stok Gudang : ${data.stokGudang}"
        binding.tvDescription.text = data.deskripsi ?: "-"
    }

    private fun setupListeners() {

        binding.menuBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvlapor.setOnClickListener {
            val bundle = Bundle().apply { putInt("idBarang", barangId) }
            findNavController().navigate(
                R.id.action_detailAdminFragment_to_laporanBarangAdminFragment,
                bundle
            )
        }

        binding.tvpermintaan.setOnClickListener {
            showDialogPermintaan()
        }
    }

    private fun showDialogPermintaan() {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Masukkan jumlah"

        AlertDialog.Builder(requireContext())
            .setTitle("Permintaan Barang")
            .setMessage("Masukkan jumlah barang yang diminta:")
            .setView(input)
            .setPositiveButton("Kirim") { _, _ ->
                val jumlah = input.text.toString().toIntOrNull() ?: 0
                if (jumlah <= 0) {
                    Toast.makeText(requireContext(), "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
                } else {
                    kirimPermintaan(jumlah)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun kirimPermintaan(jumlah: Int) {

        val body = mapOf(
            "barangId" to barangId,
            "jumlah" to jumlah
        )

        ApiClinet.instance.kirimPermintaanGudang(body)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(call: Call<ResPesan>, response: Response<ResPesan>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), response.body()?.pesan ?: "Berhasil", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal mengirim permintaan", Toast.LENGTH_LONG).show()
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
