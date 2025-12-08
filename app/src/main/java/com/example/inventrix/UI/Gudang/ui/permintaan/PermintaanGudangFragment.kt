package com.example.inventrix.UI.Gudang.ui.permintaan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPermintaanAdapter
import com.example.inventrix.Model.ResListPermintaanItem
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.Admin.DetailPermintaanActivity
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
    private var selesaiList = listOf<ResListPermintaanItem>()

    private var currentTab = 0 // 0=DIMINTA, 1=DIKIRIM, 2=SELESAI

    // ============================
    // LISTENER JIKA DETAIL ADA PERUBAHAN
    // ============================
    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Auto refresh semua data setelah balik dari detail
            loadAll()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPermintaanGudangBinding.inflate(inflater, container, false)

        setupRecycler()
        setupTabs()

        // Load pertama
        loadAll()

        return binding.root
    }

    // ============================
    // RECYCLER
    // ============================
    private fun setupRecycler() {
        adapter = ListPermintaanAdapter(
            onItemClick = { openDetail(it.id!!) },
            onDeleteClick = {},
            showDelete = false
        )

        binding.rvPermintaan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPermintaan.adapter = adapter
    }

    // ============================
    // TAB
    // ============================
    private fun setupTabs() {

        binding.diminta.setOnClickListener {
            currentTab = 0
            refreshUI()
        }

        binding.dikirim.setOnClickListener {
            currentTab = 1
            refreshUI()
        }

        binding.Selesai.setOnClickListener {
            currentTab = 2
            refreshUI()
        }
    }

    // ============================
    // REFRESH UI
    // ============================
    private fun refreshUI() {

        when (currentTab) {
            0 -> adapter.updateData(dimintaList)
            1 -> adapter.updateData(dikirimList)
            2 -> adapter.updateData(selesaiList)
        }

        highlightTab()
    }

    private fun highlightTab() {
        val active = ContextCompat.getColor(requireContext(), android.R.color.white)
        val inactive = ContextCompat.getColor(requireContext(), android.R.color.black)

        binding.diminta.setBackgroundResource(
            if (currentTab == 0) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )
        binding.dikirim.setBackgroundResource(
            if (currentTab == 1) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )
        binding.Selesai.setBackgroundResource(
            if (currentTab == 2) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )

        binding.diminta.setTextColor(if (currentTab == 0) active else inactive)
        binding.dikirim.setTextColor(if (currentTab == 1) active else inactive)
        binding.Selesai.setTextColor(if (currentTab == 2) active else inactive)
    }

    // ============================
    // LOAD DATA API
    // ============================
    private fun loadDiminta() {
        ApiClinet.instance.getPermintaanGudangDiminta()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    dimintaList = response.body() ?: emptyList()
                    if (currentTab == 0) refreshUI()
                }

                override fun onFailure(call: Call<List<ResListPermintaanItem>>, t: Throwable) {}
            })
    }

    private fun loadDikirim() {
        ApiClinet.instance.getPermintaanGudangDikirim()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    dikirimList = response.body() ?: emptyList()
                    if (currentTab == 1) refreshUI()
                }

                override fun onFailure(call: Call<List<ResListPermintaanItem>>, t: Throwable) {}
            })
    }

    private fun loadSelesai() {
        ApiClinet.instance.getAdminPermintaanSelesai()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    selesaiList = response.body() ?: emptyList()
                    if (currentTab == 2) refreshUI()
                }

                override fun onFailure(call: Call<List<ResListPermintaanItem>>, t: Throwable) {}
            })
    }

    // Load semua tab sekaligus
    private fun loadAll() {
        loadDiminta()
        loadDikirim()
        loadSelesai()
    }

    // ============================
    // BUKA DETAIL
    // ============================
    private fun openDetail(id: Int) {
        val intent = Intent(requireContext(), DetailPermintaanActivity::class.java).apply {
            putExtra("permintaanId", id)
            putExtra("role", "GUDANG")
        }

        detailLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
