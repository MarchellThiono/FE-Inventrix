package com.example.inventrix.UI.Karyawan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListBarangKaryawan
import com.example.inventrix.Adapter.ListKategori
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.DataItem
import com.example.inventrix.Model.ResKategoriList
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.Model.TampilBarangRes
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.MerekBottomSheet
import com.example.inventrix.databinding.FragmentHomeKaryawanBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeKaryawanFragment : Fragment() {

    private var _binding: FragmentHomeKaryawanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListBarangKaryawan
    private var allBarangList = listOf<DataItem>()
    private var merekTerpilih: String? = null
    private var kategoriTerpilih: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeKaryawanBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearch()
        setupChipMerek()
        loadBarang()
        setupChipKategori()

        return binding.root
    }


    private fun setupRecyclerView() {
        adapter = ListBarangKaryawan { item ->
            val bundle = Bundle().apply { putInt("id", item.id!!) }
            val detail = com.example.inventrix.UI.DetailFragment()
            detail.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right, R.anim.fade_out,
                    R.anim.fade_in, R.anim.slide_out_right
                )
                .replace(R.id.frame_container, detail)
                .addToBackStack(null)
                .commit()
        }

        binding.namaBarangKaryawan.layoutManager = LinearLayoutManager(requireContext())
        binding.namaBarangKaryawan.adapter = adapter
    }


    private fun loadBarang() {
        showLoading(true)

        ApiClinet.instance.getBarangList().enqueue(object : Callback<TampilBarangRes> {
            override fun onResponse(call: Call<TampilBarangRes>, response: Response<TampilBarangRes>) {
                showLoading(false)

                if (response.isSuccessful && response.body()?.data != null) {
                    allBarangList = response.body()!!.data!!.filterNotNull()
                    adapter.updateData(allBarangList)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TampilBarangRes>, t: Throwable) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupSearch() {
        val search = binding.searchViewBarang
        search.queryHint = "Cari barang"

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true.also { filter(q) }
            override fun onQueryTextChange(t: String?) = true.also { filter(t) }
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

                    // ✅ FIX FINAL DI SINI
                    binding.chipSemua.text = merek
                    filter(binding.searchViewBarang.query.toString())

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
                        filter(binding.searchViewBarang.query.toString())


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

    private fun filter(q: String?) {
        var result = allBarangList

        // ✅ Filter nama & kode
        if (!q.isNullOrEmpty()) {
            result = result.filter {
                it.namaBarang!!.lowercase().contains(q.lowercase()) ||
                        it.kodeBarang!!.lowercase().contains(q.lowercase())
            }
        }

        // ✅ Filter merek
        merekTerpilih?.let {
            result = result.filter { b -> b.merek == it }
        }

        // ✅ Filter kategori (INI YANG BARU)
        kategoriTerpilih?.let {
            result = result.filter { b -> b.kategori == it }
        }

        adapter.updateData(result)
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
            binding.namaBarangKaryawan.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.namaBarangKaryawan.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
