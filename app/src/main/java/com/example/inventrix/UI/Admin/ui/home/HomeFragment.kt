package com.example.inventrix.UI.Admin.ui.home

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventrix.Adapter.ListBarang
import com.example.inventrix.Adapter.ListKategori
import com.example.inventrix.Adapter.ListMerek
import com.example.inventrix.Model.*
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.Admin.ui.keranjang.KeranjangManager
import com.example.inventrix.databinding.FragmentHomeBinding
import com.example.inventrix.formatRupiah
import com.example.inventrix.toHargaInt
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListBarang
    private var allBarangList = listOf<DataItem>()
    private var kategoriTerpilih: String? = null
    private var merekTerpilih: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        setupChipKategori()
        loadBarang()
        updateCartBadge()

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnback.setOnClickListener {
            requireActivity().finish() // ✅ LANGSUNG KEMBALI, TANPA EFEK, TANPA NAV
        }
    }
    private fun setupRecyclerView() {
        adapter = ListBarang(
            onItemClick = { item ->
                val bundle = Bundle().apply { putInt("id", item.id!!) }
                findNavController().navigate(
                    R.id.action_homeFragment_to_detailAdminFragment,
                    bundle
                )
            },
            onAddClick = { barangId, jumlah ->
                val barang = allBarangList.find { it.id == barangId } ?: return@ListBarang
                val hargaFix = toHargaInt(barang.hargaJual)

                KeranjangManager.tambahAtauUpdate(
                    ReqKeranjang(
                        barangId = barang.id!!,
                        namaBarang = barang.namaBarang!!,
                        harga = hargaFix,
                        imageUrl = barang.imageUrl,
                        jumlah = jumlah,
                        merek = barang.merek,
                        kodeBarang = barang.kodeBarang,
                        stokToko = barang.stokToko
                    ),
                    qty = jumlah
                )

                adapter.notifyDataSetChanged()
                updateCartBadge()
            }
        )

        binding.rvbarangadmin.layoutManager = LinearLayoutManager(requireContext())
        binding.rvbarangadmin.adapter = adapter
    }

    private fun updateCartBadge() {
        try {
            val totalHarga = KeranjangManager.getTotalHarga()
            val tv = binding.root.findViewById<TextView>(R.id.totalSemuaBarang)
            tv?.text = "Total Harga : ${formatRupiah(totalHarga)}"
        } catch (_: Exception) { }
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

                    if (!response.isSuccessful || response.body()?.data == null) return
                    allBarangList = response.body()!!.data!!.filterNotNull()
                    adapter.updateData(allBarangList)
                }

                override fun onFailure(call: Call<TampilBarangRes>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSearchBar() {
        binding.searchViewBarang.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(q: String?) = true.also { filterBarang(q) }
                override fun onQueryTextChange(t: String?) = true.also { filterBarang(t) }
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
        rv.adapter = ListMerek(listOf("Memuat...")) { }

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, res: Response<ResTampilMerek>) {
                val list = mutableListOf("Semua Merek")
                list += res.body()?.data?.mapNotNull { it?.namaMerek } ?: emptyList()

                rv.adapter = ListMerek(list) { merek ->
                    merekTerpilih = if (merek == "Semua Merek") null else merek
                    binding.chipSemua.text = merek
                    filterBarang(binding.searchViewBarang.query.toString())
                    dialog.dismiss()
                }

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
        rvKategori.adapter = ListKategori(listOf("Memuat...")) { }

        ApiClinet.instance.getKategoriList()
            .enqueue(object : Callback<ResKategoriList> {
                override fun onResponse(call: Call<ResKategoriList>, res: Response<ResKategoriList>) {
                    val kategoriDariServer =
                        res.body()?.data?.mapNotNull { it?.nama } ?: emptyList()

                    val listKategori = mutableListOf("Semua Kategori")
                    listKategori.addAll(kategoriDariServer)

                    rvKategori.adapter = ListKategori(listKategori) { kategori ->
                        kategoriTerpilih =
                            if (kategori == "Semua Kategori") null else kategori

                        binding.chipKategori.text = kategori
                        filterBarang(binding.searchViewBarang.query.toString())
                        dialog.dismiss()
                    }

                    titleKategori.text = "Pilih Kategori"
                }

                override fun onFailure(call: Call<ResKategoriList>, t: Throwable) {
                    rvKategori.adapter = ListKategori(listOf("Gagal Memuat")) { }
                    titleKategori.text = "Gagal Memuat"
                }
            })
    }

    private fun filterBarang(query: String?) {
        var filtered = allBarangList

        if (!query.isNullOrEmpty()) {
            filtered = filtered.filter {
                it.namaBarang.orEmpty().contains(query, true) ||
                        it.kodeBarang.orEmpty().contains(query, true)
            }
        }

        merekTerpilih?.let { m ->
            filtered = filtered.filter { it.merek == m }
        }

        kategoriTerpilih?.let { k ->
            filtered = filtered.filter { it.kategori == k }
        }

        adapter.updateData(filtered)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        updateCartBadge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
