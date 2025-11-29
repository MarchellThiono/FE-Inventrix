package com.example.inventrix.UI.Gudang.ui.permintaan

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPermintaanAdapter
import com.example.inventrix.Model.ResListPermintaanItem
import com.example.inventrix.Model.ResPesan
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentPermintaanGudangBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PermintaanGudangFragment : Fragment() {

    private var _binding: FragmentPermintaanGudangBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListPermintaanAdapter

    private var dimintaList = listOf<ResListPermintaanItem>()
    private var dikirimList = listOf<ResListPermintaanItem>()
    private var isDiminta = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPermintaanGudangBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupListeners()

        loadDiminta()
        loadDikirim()

        highlightTab()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ListPermintaanAdapter { item ->
            if (item.status == "DIPINTA") {
                showConfirmDialog(item)
            }
        }

        binding.rvPermintaan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPermintaan.adapter = adapter
    }

    private fun setupListeners() {
        binding.diminta.setOnClickListener {
            isDiminta = true
            adapter.updateData(dimintaList)
            highlightTab()
        }

        binding.dikirim.setOnClickListener {
            isDiminta = false
            adapter.updateData(dikirimList)
            highlightTab()
        }
    }

    // LOAD DATA API ======================
    private fun loadDiminta() {
        ApiClinet.instance.getPermintaanGudangDiminta()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    if (response.isSuccessful) {
                        dimintaList = response.body() ?: emptyList()
                        if (isDiminta) refreshUI()
                    }
                }

                override fun onFailure(call: Call<List<ResListPermintaanItem>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun loadDikirim() {
        ApiClinet.instance.getPermintaanGudangDikirim()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    if (response.isSuccessful) {
                        dikirimList = response.body() ?: emptyList()
                        if (!isDiminta) refreshUI()
                    }
                }

                override fun onFailure(call: Call<List<ResListPermintaanItem>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    // KONFIRMASI & KIRIM BARANG ======================
    private fun showConfirmDialog(item: ResListPermintaanItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Kirim Barang?")
            .setMessage("Apakah Anda ingin mengirim barang '${item.barang?.namaBarang}' ?")
            .setPositiveButton("Kirim") { dialog, _ ->
                dialog.dismiss()
                kirimPermintaan(item.id!!)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun kirimPermintaan(id: Int) {
        ApiClinet.instance.kirimBarang(id)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(
                    call: Call<ResPesan>,
                    response: Response<ResPesan>
                ) {
                    // reload semua list
                    loadDiminta()
                    loadDikirim()

                    // pindah ke tab Dikirim
                    isDiminta = false
                    refreshUI()
                }

                override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    // REFRESH UI ======================
    private fun refreshUI() {
        if (isDiminta) {
            adapter.updateData(dimintaList)
        } else {
            adapter.updateData(dikirimList)
        }
        highlightTab()
    }

    // UI TAB ======================
    private fun highlightTab() {
        val active = ContextCompat.getColor(requireContext(), android.R.color.white)
        val inactive = ContextCompat.getColor(requireContext(), android.R.color.black)

        binding.diminta.setBackgroundResource(
            if (isDiminta) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )
        binding.dikirim.setBackgroundResource(
            if (!isDiminta) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )

        binding.diminta.setTextColor(if (isDiminta) active else inactive)
        binding.dikirim.setTextColor(if (!isDiminta) active else inactive)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
