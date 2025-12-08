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
import com.example.inventrix.Adapter.ListKategori
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.BarangDipilihGudang
import com.example.inventrix.Model.DataItem
import com.example.inventrix.Model.ResKategoriList
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
    private var kategoriTerpilih: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeGudangBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        setupChipKategori()
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

    private fun setupChipKategori() {
        binding.chipKategori.setOnClickListener {
            tampilkanBottomSheetKategori()
        }
    }

    private fun tampilkanBottomSheetKategori() {

        val view = layoutInflater.inflate(R.layout.bottomsheet_kategori, null)
        val rvKategori = view.findViewById<RecyclerView>(R.id.rvKategori)
        val titleKategori = view.findViewById<TextView>(R.id.titleKategori)

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.show()

        rvKategori.layoutManager = LinearLayoutManager(requireContext())

        // tampilkan loading
        rvKategori.adapter = ListKategori(listOf("Memuat...")) { }

        ApiClinet.instance.getKategoriList()
            .enqueue(object : Callback<ResKategoriList> {
                override fun onResponse(
                    call: Call<ResKategoriList>,
                    res: Response<ResKategoriList>
                ) {

                    val kategoriDariServer =
                        res.body()?.data?.mapNotNull { it?.nama } ?: emptyList()

                    val listKategori = mutableListOf("Semua Kategori")
                    listKategori.addAll(kategoriDariServer)

                    val kategoriAdapter = ListKategori(listKategori) { kategori ->

                        kategoriTerpilih =
                            if (kategori == "Semua Kategori") null else kategori

                        binding.chipKategori.text = kategori
                        filterBarang(binding.searchViewBarang.query.toString())

                        dialog.dismiss()
                    }

                    rvKategori.adapter = kategoriAdapter
                    titleKategori.text = "Pilih Kategori"
                }

                override fun onFailure(call: Call<ResKategoriList>, t: Throwable) {
                    rvKategori.adapter = ListKategori(listOf("Gagal Memuat")) { }
                    titleKategori.text = "Gagal Memuat"
                }
            })
    }


    private fun setupChipMerek() {
        binding.chipSemua.setOnClickListener { tampilkanBottomSheetMerek() }
    }

    private fun tampilkanBottomSheetMerek() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_merek, null)

        val rv = view.findViewById<RecyclerView>(R.id.rvMerek)
        val title = view.findViewById<TextView>(R.id.titleMerek)

        rv.layoutManager = LinearLayoutManager(requireContext())

        // ✅ Loading sementara
        val loadingAdapter = ListMerek(listOf("Memuat...")) { }
        rv.adapter = loadingAdapter

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(
                call: Call<ResTampilMerek>,
                res: Response<ResTampilMerek>
            ) {
                val list = mutableListOf("Semua Merek")
                list += res.body()?.data?.mapNotNull { it?.namaMerek } ?: emptyList()

                val adapterMerek = ListMerek(list) { merek ->
                    merekTerpilih = if (merek == "Semua Merek") null else merek

                    // ✅ INI YANG DIPERBAIKI
                    binding.chipSemua.text = merek
                    filterBarang(binding.searchViewBarang.query.toString())

                    dialog.dismiss()
                }

                rv.adapter = adapterMerek
                title.text = "Pilih Merek"
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                title.text = "Gagal memuat"
                Toast.makeText(requireContext(), "Gagal memuat merek", Toast.LENGTH_SHORT).show()
            }
        })

        dialog.setContentView(view)
        dialog.show()
    }

    private fun filterBarang(q: String?) {
        var filtered = allBarangList

        // ✅ Filter nama & kode
        if (!q.isNullOrEmpty()) {
            filtered = filtered.filter {
                it.namaBarang!!.lowercase().contains(q.lowercase()) ||
                        it.kodeBarang!!.lowercase().contains(q.lowercase())
            }
        }

        // ✅ Filter merek
        merekTerpilih?.let { m ->
            filtered = filtered.filter { it.merek == m }
        }

        // ✅ Filter kategori (INI YANG BARU)
        kategoriTerpilih?.let { k ->
            filtered = filtered.filter { it.kategori == k }
        }

        adapter.updateData(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
