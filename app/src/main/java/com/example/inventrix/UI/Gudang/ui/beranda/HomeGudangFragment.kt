package com.example.inventrix.UI.Gudang.ui.beranda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListBarangGudang
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.BarangDipilihGudang
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

    private val viewModel: HomeGudangViewModel by activityViewModels()

    private lateinit var adapter: ListBarangGudang
    private var allBarangList = listOf<DataItem>()
    private var merekTerpilih: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeGudangBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        loadBarang()

        // tombol lanjut
        binding.btnLanjut.setOnClickListener {
            if (viewModel.selectedBarang.value!!.isEmpty()) {
                Toast.makeText(requireContext(), "Belum memilih barang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedList = viewModel.selectedBarang.value!!.map { (id, jumlah) ->
                val barang = allBarangList.first { it.id == id }

                BarangDipilihGudang(
                    barangId = id,
                    nama = barang.namaBarang ?: "-",
                    merek = barang.merek ?: "-",
                    kodeBarang = barang.kodeBarang ?: "-",
                    stokGudang = jumlah
                )
            }

            val bundle = Bundle().apply {
                putSerializable("selected_list", ArrayList(selectedList))
            }

            findNavController().navigate(
                R.id.action_homeGudang_to_buatLaporanFragment, bundle
            )
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ListBarangGudang(
            onItemClick = { item ->
                val b = Bundle().apply { putInt("id", item.id!!) }
                findNavController().navigate(
                    R.id.action_homeGudangFragment_to_detailFragment,
                    b
                )
            },
            onJumlahChange = { id, jumlah ->
                viewModel.setJumlah(id, jumlah)
            },
            getJumlah = { id ->
                viewModel.getJumlah(id)
            }
        )

        binding.rvBarangGudang.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarangGudang.adapter = adapter
    }

    private fun loadBarang() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getBarangList()
            .enqueue(object : Callback<TampilBarangRes> {
                override fun onResponse(
                    call: Call<TampilBarangRes>,
                    response: Response<TampilBarangRes>
                ) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        allBarangList = response.body()?.data?.filterNotNull() ?: emptyList()
                        adapter.updateData(allBarangList)
                    }
                }

                override fun onFailure(call: Call<TampilBarangRes>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSearchBar() {
        binding.searchViewBarang.queryHint = "Cari barang gudang"

        binding.searchViewBarang.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true.also { filterBarang(q) }
            override fun onQueryTextChange(t: String?) = true.also { filterBarang(t) }
        })
    }

    private fun setupChipMerek() {
        binding.chipSemua.setOnClickListener { tampilkanBottomSheetMerek() }
    }

    private fun tampilkanBottomSheetMerek() {
        val view = layoutInflater.inflate(R.layout.bottomsheet_merek, null)
        val rv = view.findViewById<RecyclerView>(R.id.rvMerek)
        val tvTitle = view.findViewById<TextView>(R.id.titleMerek)

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.show()

        val adapterMerek = ListMerek(emptyList()) { merek ->
            merekTerpilih = merek
            binding.chipSemua.text = merek
            filterBarang(binding.searchViewBarang.query.toString())
            dialog.dismiss()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapterMerek

        ApiClinet.instance.getMerekList()
            .enqueue(object : Callback<ResTampilMerek> {
                override fun onResponse(
                    call: Call<ResTampilMerek>,
                    response: Response<ResTampilMerek>
                ) {
                    val list = response.body()?.data?.mapNotNull { it?.namaMerek } ?: emptyList()
                    adapterMerek.updateData(list)
                }

                override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                    tvTitle.text = "Gagal memuat merek"
                }
            })
    }

    private fun filterBarang(q: String?) {
        var filtered = allBarangList

        if (!q.isNullOrEmpty()) {
            filtered = filtered.filter {
                it.namaBarang!!.lowercase().contains(q.lowercase()) ||
                        it.kodeBarang!!.lowercase().contains(q.lowercase())
            }
        }

        merekTerpilih?.let { m ->
            filtered = filtered.filter { it.merek == m }
        }

        adapter.updateData(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
