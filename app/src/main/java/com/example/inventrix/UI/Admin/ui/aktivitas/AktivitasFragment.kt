package com.example.inventrix.UI.Admin.ui.aktivitas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListPermintaanAdapter
import com.example.inventrix.Model.ResListPermintaanItem
import com.example.inventrix.Model.ResPesan
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.Admin.DetailPermintaanActivity
import com.example.inventrix.databinding.FragmentAktivitasBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AktivitasFragment : Fragment() {

    private var _binding: FragmentAktivitasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListPermintaanAdapter

    private var dimintaList = listOf<ResListPermintaanItem>()
    private var dikirimList = listOf<ResListPermintaanItem>()
    private var selesaiList = listOf<ResListPermintaanItem>()

    private var currentTab = 0 // 0 = DIMINTA, 1 = DIKIRIM, 2 = SELESAI

    // ============================================
    // Launcher untuk menerima hasil dari DetailPermintaanActivity
    // ============================================
    private val startDetailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                // Refresh semua data
                loadDiminta()
                loadDikirim()
                loadSelesai()

                // Pindah ke tab SELESAI otomatis
                currentTab = 2
                refreshUI()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAktivitasBinding.inflate(inflater, container, false)

        setupRecycler()
        setupTabListeners()

        loadDiminta()
        loadDikirim()
        loadSelesai()

        highlightTab()

        return binding.root
    }

    // ============================================
    // RECYCLER VIEW
    // ============================================
    private fun setupRecycler() {
        adapter = ListPermintaanAdapter(
            onItemClick = { item -> openDetail(item.id!!) },
            onDeleteClick = { confirmDelete(it) },
            showDelete = false
        )

        binding.rvPermintaan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPermintaan.adapter = adapter
    }

    // ============================================
    // OPEN DETAIL PERMINTAAN
    // ============================================
    private fun openDetail(id: Int) {
        val intent = Intent(requireContext(), DetailPermintaanActivity::class.java).apply {
            putExtra("permintaanId", id)
            putExtra("role", "OWNER") // Owner/admin toko
        }

        startDetailLauncher.launch(intent)
    }

    // ============================================
    // REFRESH UI
    // ============================================
    private fun refreshUI() {
        adapter.showDelete = (currentTab == 2)

        when (currentTab) {
            0 -> adapter.updateData(dimintaList)
            1 -> adapter.updateData(dikirimList)
            2 -> adapter.updateData(selesaiList)
        }

        highlightTab()
    }

    private fun setupTabListeners() {
        binding.diproses.setOnClickListener {
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

    private fun highlightTab() {
        val active = ContextCompat.getColor(requireContext(), android.R.color.white)
        val inactive = ContextCompat.getColor(requireContext(), android.R.color.black)

        binding.diproses.setBackgroundResource(
            if (currentTab == 0) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )
        binding.dikirim.setBackgroundResource(
            if (currentTab == 1) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )
        binding.Selesai.setBackgroundResource(
            if (currentTab == 2) R.drawable.bg_chip_selected else R.drawable.bg_chip
        )

        binding.diproses.setTextColor(if (currentTab == 0) active else inactive)
        binding.dikirim.setTextColor(if (currentTab == 1) active else inactive)
        binding.Selesai.setTextColor(if (currentTab == 2) active else inactive)
    }

    // ============================================
    // LOAD DATA
    // ============================================
    private fun loadDiminta() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getAdminPermintaanDiminta()
            .enqueue(object : Callback<List<ResListPermintaanItem>> {
                override fun onResponse(
                    call: Call<List<ResListPermintaanItem>>,
                    response: Response<List<ResListPermintaanItem>>
                ) {
                    binding.progressBar.visibility = View.GONE
                    dimintaList = response.body() ?: emptyList()
                    if (currentTab == 0) refreshUI()
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

    // ============================================
    // DELETE PERMINTAAN (Hanya Selesai)
    // ============================================
    private fun confirmDelete(item: ResListPermintaanItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Permintaan")
            .setMessage("Yakin ingin menghapus permintaan ini?")
            .setPositiveButton("Hapus") { _, _ -> hapusPermintaan(item.id!!) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusPermintaan(id: Int) {
        ApiClinet.instance.hapusPermintaan(id)
            .enqueue(object : Callback<ResPesan> {
                override fun onResponse(
                    call: Call<ResPesan>,
                    response: Response<ResPesan>
                ) {
                    loadSelesai()
                }

                override fun onFailure(call: Call<ResPesan>, t: Throwable) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
