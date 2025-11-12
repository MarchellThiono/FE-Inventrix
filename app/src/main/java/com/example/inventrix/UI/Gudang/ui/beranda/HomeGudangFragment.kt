package com.example.inventrix.UI.Gudang.ui.beranda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListBarang
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.DataItem
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.Model.TampilBarangRes
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.databinding.FragmentHomeGudangBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeGudangFragment : Fragment() {

    private var _binding: FragmentHomeGudangBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListBarang
    private var allBarangList = listOf<DataItem>()
    private var merekTerpilih: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeGudangBinding.inflate(inflater, container, false)
        val root = binding.root

        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        loadBarang()

        return root
    }

    /** -------------------------------
     *  SETUP RECYCLER VIEW BARANG
     *  ------------------------------*/
    private fun setupRecyclerView() {
        adapter = ListBarang(role = "gudang") { item ->
            val bundle = Bundle().apply {
                putInt("id", item.id ?: -1)
            }

            // Navigasi ke DetailFragment
            findNavController().navigate(R.id.action_homeGudangFragment_to_detailFragment, bundle)
        }

        binding.rvBarangGudang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarangGudang.adapter = adapter
    }

    /** -------------------------------
     *  LOAD DATA DARI BACKEND
     *  ------------------------------*/
    private fun loadBarang() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getBarangList().enqueue(object : Callback<TampilBarangRes> {
            override fun onResponse(
                call: Call<TampilBarangRes>,
                response: Response<TampilBarangRes>
            ) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.data != null) {
                    allBarangList = response.body()?.data?.filterNotNull() ?: emptyList()

                    if (allBarangList.isEmpty()) {
                        Toast.makeText(requireContext(), "Tidak ada data barang", Toast.LENGTH_SHORT).show()
                    }

                    adapter.updateData(allBarangList)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TampilBarangRes>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** -------------------------------
     *  FITUR SEARCH BAR
     *  ------------------------------*/
    private fun setupSearchBar() {
        val searchView = binding.searchViewBarang
        searchView.queryHint = "Cari barang di gudang"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterBarang(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBarang(newText)
                return true
            }
        })
    }

    /** -------------------------------
     *  FITUR PILIH MEREK (BOTTOM SHEET)
     *  ------------------------------*/
    private fun setupChipMerek() {
        val chipMerek = binding.chipSemua
        chipMerek.setOnClickListener {
            tampilkanBottomSheetMerek()
        }
    }

    private fun tampilkanBottomSheetMerek() {
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_merek, null)
        val rvMerek = dialogView.findViewById<RecyclerView>(R.id.rvMerek)
        val tvTitle = dialogView.findViewById<TextView>(R.id.titleMerek)

        rvMerek.layoutManager = LinearLayoutManager(requireContext())
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(dialogView)
        bottomSheet.show()

        // Adapter pakai ListMerek (bukan MerekAdapter)
        val merekAdapter = ListMerek(emptyList()) { merek ->
            merekTerpilih = merek
            binding.chipSemua.text = merek
            filterBarang(binding.searchViewBarang.query.toString())
            bottomSheet.dismiss()
        }
        rvMerek.adapter = merekAdapter

        // 🔹 Ambil data merek dari backend
        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val listMerek = response.body()?.data
                        ?.filterNotNull()
                        ?.mapNotNull { it.namaMerek } // ambil hanya namaMerek (String)
                        ?: emptyList()

                    merekAdapter.updateData(listMerek)
                    tvTitle.text = "Pilih Merek"
                } else {
                    tvTitle.text = "Gagal memuat merek"
                }
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                tvTitle.text = "Gagal terhubung: ${t.message}"
            }
        })
    }

    /** -------------------------------
     *  FILTER BARANG BERDASARKAN SEARCH & MEREK
     *  ------------------------------*/
    private fun filterBarang(query: String?) {
        var filtered = allBarangList

        // 🔍 Filter berdasarkan nama/kode barang
        if (!query.isNullOrEmpty()) {
            filtered = filtered.filter { item ->
                val nama = item.namaBarang?.lowercase() ?: ""
                val kode = item.kodeBarang?.lowercase() ?: ""
                query.lowercase() in nama || query.lowercase() in kode
            }
        }

        // 🔹 Filter berdasarkan merek (jika ada yang dipilih)
        merekTerpilih?.let { merek ->
            filtered = filtered.filter { it.merek?.equals(merek, ignoreCase = true) == true }
        }

        // 🔄 Update adapter
        adapter.updateData(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
