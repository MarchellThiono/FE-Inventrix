package com.example.inventrix.UI.Karyawan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventrix.Adapter.ListBarangKaryawan
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

    private lateinit var adapter: ListBarangKaryawan
    private var allBarangList = listOf<DataItem>()
    private var merekTerpilih: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeKaryawanBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearch()
        setupMerek()
        loadBarang()

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


    private fun setupMerek() {
        binding.chipSemua.setOnClickListener {
            val sheet = MerekBottomSheet { merek ->
                merekTerpilih = merek
                binding.chipSemua.text = merek
                filter(binding.searchViewBarang.query.toString())
            }
            sheet.show(parentFragmentManager, "MerekBottomSheetKaryawan")
        }
    }


    private fun filter(q: String?) {
        var result = allBarangList

        if (!q.isNullOrEmpty()) {
            result = result.filter {
                it.namaBarang!!.lowercase().contains(q.lowercase()) ||
                        it.kodeBarang!!.lowercase().contains(q.lowercase())
            }
        }

        merekTerpilih?.let {
            result = result.filter { b -> b.merek == it }
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
