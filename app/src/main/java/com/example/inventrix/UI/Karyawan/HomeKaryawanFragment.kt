package com.example.inventrix.UI.Karyawan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListBarang
import com.example.inventrix.Model.DataItem
import com.example.inventrix.Model.TampilBarangRes
import com.example.inventrix.R
import com.example.inventrix.Server.ApiClinet
import com.example.inventrix.UI.MerekBottomSheet
import com.example.inventrix.databinding.FragmentHomeKaryawanBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeKaryawanFragment : Fragment() {

    private var _binding: FragmentHomeKaryawanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListBarang
    private var allBarangList = listOf<DataItem>()
    private var merekTerpilih: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeKaryawanBinding.inflate(inflater, container, false)
        val root = binding.root

        setupRecyclerView()
        setupSearchBar()
        setupChipMerek()
        loadBarang()

        return root
    }

    /** -------------------------------------
     *  SETUP RECYCLERVIEW & ITEM CLICK
     *  ------------------------------------*/
    private fun setupRecyclerView() {
        adapter = ListBarang(
            role = "karyawan",
            onItemClick = { item ->
                val bundle = Bundle().apply { putInt("id", item.id ?: -1) }
                val detailFragment = com.example.inventrix.UI.DetailFragment()
                detailFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right)
                    .replace(R.id.frame_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )


        binding.namaBarangKaryawan.layoutManager = LinearLayoutManager(requireContext())
        binding.namaBarangKaryawan.adapter = adapter
    }

    /** -------------------------------------
     *  LOAD DATA DARI BACKEND
     *  ------------------------------------*/
    private fun loadBarang() {
        showLoading(true)

        ApiClinet.instance.getBarangList().enqueue(object : Callback<TampilBarangRes> {
            override fun onResponse(
                call: Call<TampilBarangRes>,
                response: Response<TampilBarangRes>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body()?.data != null) {
                    allBarangList = response.body()?.data?.filterNotNull() ?: emptyList()
                    adapter.updateData(allBarangList)
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data barang", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<TampilBarangRes>, t: Throwable) {
                showLoading(false)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** -------------------------------------
     *  SEARCH BAR
     *  ------------------------------------*/
    private fun setupSearchBar() {
        val searchView = binding.searchViewBarang
        searchView.queryHint = "Cari barang"

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
     *  FITUR PILIH MEREK (BOTTOM SHEET DARI BACKEND)
     *  ------------------------------*/
    private fun setupChipMerek() {
        val chipMerek = binding.chipSemua
        chipMerek.setOnClickListener {
            val bottomSheet = MerekBottomSheet { merek ->
                merekTerpilih = merek
                chipMerek.text = merek
                filterBarang(binding.searchViewBarang.query.toString())
            }
            bottomSheet.show(parentFragmentManager, "MerekBottomSheetKaryawan")
        }
    }

    /** -------------------------------------
     *  FILTER DATA (SEARCH + MEREK)
     *  ------------------------------------*/
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

    /** -------------------------------------
     *  PROGRESSBAR LOADING
     *  ------------------------------------*/
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.animate().alpha(1f).setDuration(200).start()
            binding.progressBar.visibility = View.VISIBLE
            binding.namaBarangKaryawan.visibility = View.INVISIBLE
        } else {
            binding.progressBar.animate().alpha(0f).setDuration(200).withEndAction {
                binding.progressBar.visibility = View.GONE
            }.start()
            binding.namaBarangKaryawan.visibility = View.VISIBLE
        }
    }
}