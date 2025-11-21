package com.example.inventrix.UI.Admin.ui.home

import android.content.Context
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
import com.example.inventrix.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListBarang
    private var allBarangList = listOf<DataItem>()
    private var merekTerpilih: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        loadBarang()

        binding.btntmbh.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_tambahBarangFragment)
        }
        binding.kelolaMerek.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_kelolaMerekFragment)
        }
        binding.notif.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_notifikasiFragment)
        }

        return root
    }

    private fun setupRecyclerView() {
        val prefs = requireContext().getSharedPreferences("InventrixSession", Context.MODE_PRIVATE)
        val role = prefs.getString("ROLE", "owner") ?: "owner"

        adapter = ListBarang(role,
            onItemClick = { item -> },
            onEditClick = { item ->
                val bundle = Bundle().apply {
                    putBoolean("isEdit", true)
                    putInt("id", item.id ?: -1)
                }
                findNavController().navigate(R.id.action_homeFragment_to_tambahBarangFragment, bundle)
            }
        )

        val lm = LinearLayoutManager(requireContext())
        lm.reverseLayout = false      // item mulai dari atas
        lm.stackFromEnd = false       // jangan scroll ke bawah otomatis

        binding.rvbarangadmin.layoutManager = lm

        (binding.rvbarangadmin.itemAnimator as? androidx.recyclerview.widget.SimpleItemAnimator)
            ?.supportsChangeAnimations = false

        binding.rvbarangadmin.adapter = adapter
    }


    private fun loadBarang() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getBarangList().enqueue(object : Callback<TampilBarangRes> {
            override fun onResponse(call: Call<TampilBarangRes>, response: Response<TampilBarangRes>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.data != null) {
                    allBarangList = response.body()?.data?.filterNotNull() ?: emptyList()
                    if (allBarangList.isEmpty()) {
                        Toast.makeText(requireContext(), "Belum ada data barang", Toast.LENGTH_SHORT).show()
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

    private fun setupSearchBar() {
        val searchView = binding.searchViewBarang
        searchView.queryHint = "Cari nama atau kode barang"

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

        val merekAdapter = ListMerek(emptyList()) { merek ->
            merekTerpilih = merek
            binding.chipSemua.text = merek
            filterBarang(binding.searchViewBarang.query.toString())
            bottomSheet.dismiss()
        }
        rvMerek.adapter = merekAdapter

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val listMerek = response.body()?.data
                        ?.filterNotNull()
                        ?.mapNotNull { it.namaMerek }
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

    private fun filterBarang(query: String?) {
        var filtered = allBarangList

        if (!query.isNullOrEmpty()) {
            filtered = filtered.filter { item ->
                val nama = item.namaBarang?.lowercase() ?: ""
                val kode = item.kodeBarang?.lowercase() ?: ""
                query.lowercase() in nama || query.lowercase() in kode
            }
        }

        merekTerpilih?.let { merek ->
            filtered = filtered.filter { it.merek?.equals(merek, ignoreCase = true) == true }
        }

        adapter.updateData(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
