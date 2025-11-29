package com.example.inventrix.UI.Admin.ui.keranjang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListKeranjang
import com.example.inventrix.Model.ReqTransaksiKeluar
import com.example.inventrix.Model.ResTransaksiKeluar
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentKeranjangBinding
import com.example.inventrix.formatRupiah
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KeranjangFragment : Fragment() {

    private var _binding: FragmentKeranjangBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListKeranjang

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeranjangBinding.inflate(inflater, container, false)

        setupRecyclerView()
        updateTotalHarga()

        binding.btntmbh.setOnClickListener { kirimTransaksi() }

        return binding.root
    }

    private fun setupRecyclerView() {
        val data = KeranjangManager.getKeranjang().toMutableList()

        adapter = ListKeranjang(data) {
            // callback ketika jumlah berubah di adapter
            updateTotalHarga()
        }

        binding.rvpilihbarang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvpilihbarang.adapter = adapter
    }

    private fun updateTotalHarga() {
        val total = KeranjangManager.getTotalHarga()
        binding.totalSemuaBarang.text = "Total Harga : ${formatRupiah(total)}"
    }

    private fun kirimTransaksi() {

        val itemsReq = KeranjangManager.getKeranjang()
            .filter { it.jumlah > 0 }   // WAJIB!
            .map { ReqTransaksiKeluar.Item(it.barangId, it.jumlah) }

        if (itemsReq.isEmpty()) {
            Toast.makeText(requireContext(), "Keranjang kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val body = ReqTransaksiKeluar(itemsReq)

        ApiClinet.instance.createTransaksi(body).enqueue(object : Callback<ResTransaksiKeluar> {

            override fun onResponse(
                call: Call<ResTransaksiKeluar>,
                response: Response<ResTransaksiKeluar>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    KeranjangManager.clear()
                    adapter.updateData(mutableListOf())
                    updateTotalHarga()

                    Toast.makeText(requireContext(), "Transaksi Berhasil!", Toast.LENGTH_SHORT).show()

                } else {
                    val err = response.errorBody()?.string()
                    android.util.Log.e("API_ERROR", "Server Response: $err")

                    Toast.makeText(requireContext(), "Error Server: $err", Toast.LENGTH_LONG).show()
                }
            }


            override fun onFailure(call: Call<ResTransaksiKeluar>, t: Throwable) {
                Toast.makeText(requireContext(), "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        // refresh data dari manager (mis. user kembali dari Home)
        adapter.updateData(KeranjangManager.getKeranjang().toMutableList())
        updateTotalHarga()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
