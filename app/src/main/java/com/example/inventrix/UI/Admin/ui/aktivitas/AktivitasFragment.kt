package com.example.inventrix.UI.Admin.ui.aktivitas

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
import com.example.inventrix.databinding.FragmentAktivitasBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AktivitasFragment : Fragment() {

    private var _binding: FragmentAktivitasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListPermintaanAdapter

    private var diprosesList = listOf<ResListPermintaanItem>()
    private var dikirimList = listOf<ResListPermintaanItem>()

    private var isDiproses = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAktivitasBinding.inflate(inflater, container, false)

        setupRecycler()
        setupListeners()

        loadDiproses()
        loadDikirim()

        highlightTab()

        return binding.root
    }

    private fun setupRecycler() {
        adapter = ListPermintaanAdapter { item ->
            handleItemClick(item)
        }

        binding.rvPermintaan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPermintaan.adapter = adapter
    }

    private fun setupListeners() {
        binding.diproses.setOnClickListener {
            isDiproses = true
            refreshUI()
        }

        binding.dikirim.setOnClickListener {
            isDiproses = false
            refreshUI()
        }
    }

    private fun highlightTab() {
        val active = ContextCompat.getColor(requireContext(), android.R.color.white)
        val inactive = ContextCompat.getColor(requireContext(), android.R.color.black)

        binding.diproses.setBackgroundResource(
            if (isDiproses) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )
        binding.dikirim.setBackgroundResource(
            if (!isDiproses) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )

        binding.diproses.setTextColor(if (isDiproses) active else inactive)
        binding.dikirim.setTextColor(if (!isDiproses) active else inactive)
    }

    // ============================
    // LOAD DATA ADMIN
    // ============================
    private fun loadDiproses() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getAdminPermintaanDiminta()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        diprosesList = response.body() ?: emptyList()
                        if (isDiproses) refreshUI()
                    }
                }

                override fun onFailure(call: Call<List<ResListPermintaanItem>>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                }
            })
    }

    private fun loadDikirim() {
        ApiClinet.instance.getAdminPermintaanDikirim()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    if (response.isSuccessful) {
                        dikirimList = response.body() ?: emptyList()
                        if (!isDiproses) refreshUI()
                    }
                }

                override fun onFailure(call: Call<List<ResListPermintaanItem>>, t: Throwable) {}
            })
    }

    // ============================
    //   REFRESH UI ADMIN
    // ============================
    private fun refreshUI() {
        if (isDiproses) {
            adapter.updateData(diprosesList)
        } else {
            adapter.updateData(dikirimList)
        }

        highlightTab()
    }

    // ============================
    // DETAIL + KONFIRMASI
    // ============================
    private fun handleItemClick(item: ResListPermintaanItem) {
        val barang = item.barang

        if (item.status == "DIKIRIM") {
            // ---- KONFIRMASI BARANG SUDAH TIBA ----
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Barang")
                .setMessage("Apakah barang '${barang?.namaBarang}' sudah tiba di toko dan sesuai?")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Konfirmasi") { d, _ ->
                    d.dismiss()
                    konfirmasiBarangTiba(item.id!!)
                }
                .show()

        } else {
            // ---- HANYA DETAIL ----
            AlertDialog.Builder(requireContext())
                .setTitle("Detail Permintaan")
                .setMessage(
                    "Nama Barang : ${barang?.namaBarang}\n" +
                            "Kode Barang : ${barang?.kodeBarang}\n" +
                            "Jumlah      : ${item.jumlah}\n" +
                            "Merek       : ${barang?.merek?.namaMerek}\n" +
                            "Status      : ${item.status}"
                )
                .setPositiveButton("OK", null)
                .show()
        }
    }

    // ============================
    //  KONFIRMASI BARANG TIBA
    // ============================
    private fun konfirmasiBarangTiba(id: Int) {
        ApiClinet.instance.konfirmasiPermintaanSelesai(id)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(
                    call: Call<ResPesan>,
                    response: Response<ResPesan>
                ) {
                    // refresh data dari server
                    loadDiproses()
                    loadDikirim()
                }

                override fun onFailure(call: Call<ResPesan>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
