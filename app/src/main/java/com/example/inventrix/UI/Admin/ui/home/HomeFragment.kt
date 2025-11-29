package com.example.inventrix.UI.Admin.ui.home

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
import com.example.inventrix.Model.ReqKeranjang
import com.example.inventrix.Model.ResTampilMerek
import com.example.inventrix.Model.TampilBarangRes
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.Admin.ui.keranjang.KeranjangManager
import com.example.inventrix.databinding.FragmentHomeBinding
import com.example.inventrix.formatRupiah
import com.example.inventrix.toHargaInt
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        loadBarang()
        updateCartBadge() // initial

        binding.btntmbh.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_tambahBarangFragment)
        }

        binding.kelolaMerek.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_kelolaMerekFragment)
        }

        binding.notif.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notifikasiFragment)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ListBarang(
            onItemClick = { item ->
                val bundle = Bundle().apply { putInt("id", item.id!!) }
                findNavController().navigate(
                    R.id.action_homeFragment_to_detailAdminFragment, bundle
                )
            },
            onEditClick = { item ->
                val bundle = Bundle().apply {
                    putBoolean("isEdit", true)
                    putInt("id", item.id!!)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_tambahBarangFragment,
                    bundle
                )
            },
            onAddClick = { barangId, jumlah ->
                val barang = allBarangList.find { it.id == barangId } ?: return@ListBarang

                val hargaFix = toHargaInt(barang.hargaJual)

                // update keranjang (manager)
                KeranjangManager.tambahAtauUpdate(
                    ReqKeranjang(
                        barangId = barang.id!!,
                        namaBarang = barang.namaBarang ?: "",
                        harga = hargaFix,
                        imageUrl = barang.imageUrl,
                        jumlah = jumlah,
                        merek = barang.merek,
                        kodeBarang = barang.kodeBarang,
                        stokToko = barang.stokToko
                    ),
                    qty = jumlah
                )

                // refresh only changed item for performance
                val idx = allBarangList.indexOfFirst { it.id == barangId }
                if (idx >= 0) adapter.notifyItemChanged(idx) else adapter.notifyDataSetChanged()

                updateCartBadge()
            }
        )

        binding.rvbarangadmin.layoutManager = LinearLayoutManager(requireContext())
        binding.rvbarangadmin.adapter = adapter
    }

    private fun updateCartBadge() {
        val totalItems = KeranjangManager.getKeranjang().sumOf { it.jumlah }
        val totalHarga = KeranjangManager.getTotalHarga()
        // contoh: tampilkan total harga di toolbar / chip jika ada
        // Jika layout tidak punya, ini safe-call — sesuaikan sendiri
        // binding.tvCartBadge?.text = totalItems.toString()
        // binding.tvCartTotal?.text = formatRupiah(totalHarga)
        // Untuk memastikan user bisa lihat total: (jika ada TextView named totalSemuaBarang misal)
        // safe guard: only set if exists
        try {
            val tv = binding.root.findViewById<TextView>(R.id.totalSemuaBarang)
            tv?.text = "Total Harga : ${formatRupiah(totalHarga)}"
        } catch (_: Exception) { /* ignore if not exist */ }
    }

    private fun loadBarang() {
        binding.progressBar.visibility = View.VISIBLE

        ApiClinet.instance.getBarangList().enqueue(object : Callback<TampilBarangRes> {
            override fun onResponse(call: Call<TampilBarangRes>, response: Response<TampilBarangRes>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.data != null) {
                    allBarangList = response.body()!!.data!!.filterNotNull()
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
        val search = binding.searchViewBarang
        search.queryHint = "Cari nama atau kode barang"

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true.also { filterBarang(q) }
            override fun onQueryTextChange(t: String?) = true.also { filterBarang(t) }
        })
    }

    private fun setupChipMerek() {
        binding.chipSemua.setOnClickListener {
            tampilkanBottomSheetMerek()
        }
    }

    private fun tampilkanBottomSheetMerek() {
        val view = layoutInflater.inflate(R.layout.bottomsheet_merek, null)
        val rv = view.findViewById<RecyclerView>(R.id.rvMerek)
        val tv = view.findViewById<TextView>(R.id.titleMerek)

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.show()

        val merekAdapter = ListMerek(emptyList()) { merek ->
            merekTerpilih = merek
            binding.chipSemua.text = merek
            filterBarang(binding.searchViewBarang.query.toString())
            dialog.dismiss()
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = merekAdapter

        ApiClinet.instance.getMerekList().enqueue(object : Callback<ResTampilMerek> {
            override fun onResponse(call: Call<ResTampilMerek>, response: Response<ResTampilMerek>) {
                val list = response.body()?.data?.mapNotNull { it?.namaMerek } ?: emptyList()
                merekAdapter.updateData(list)
                tv.text = "Pilih Merek"
            }

            override fun onFailure(call: Call<ResTampilMerek>, t: Throwable) {
                tv.text = "Gagal memuat merek"
            }
        })
    }

    private fun filterBarang(query: String?) {
        var filtered = allBarangList

        if (!query.isNullOrEmpty()) {
            filtered = filtered.filter {
                (it.namaBarang ?: "").lowercase().contains(query.lowercase())
                        || (it.kodeBarang ?: "").lowercase().contains(query.lowercase())
            }
        }

        merekTerpilih?.let { m ->
            filtered = filtered.filter { it.merek == m }
        }

        adapter.updateData(filtered)
    }

    override fun onResume() {
        super.onResume()
        // rebind supaya counter sesuai KeranjangManager (jika user ubah dari Fragment Keranjang)
        adapter.notifyDataSetChanged()
        updateCartBadge()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
